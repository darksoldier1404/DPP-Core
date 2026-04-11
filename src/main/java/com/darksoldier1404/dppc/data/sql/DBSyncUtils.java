package com.darksoldier1404.dppc.data.sql;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.api.logger.DLogManager;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.IDataHandler;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Collections;

/**
 * Utility class for bidirectional synchronisation between a {@link DPlugin}'s
 * file-based {@link IDataHandler} data and a MySQL / SQLite database.
 *
 * <h3>Sync flow (per key)</h3>
 * <ol>
 *   <li>Flush in-memory data to disk via {@link IDataHandler#saveAll()}.</li>
 *   <li>Fetch DB rows ({@code checksum}, {@code updated_at}).</li>
 *   <li>List YAML files on disk.</li>
 *   <li>For every key that exists on either side:
 *     <ul>
 *       <li><b>Server-only</b>  → upload to DB.</li>
 *       <li><b>DB-only</b>      → download to disk, reload handler.</li>
 *       <li><b>Checksum match</b> → skip (already in sync).</li>
 *       <li><b>DB newer</b>     → download to disk, reload handler.</li>
 *       <li><b>Server newer</b> → upload to DB.</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h3>Integrity check</h3>
 * SHA-256 checksum of the JSON-serialised {@link YamlConfiguration}.
 * If checksums are equal the row is skipped regardless of timestamps.
 *
 * <h3>Table schema</h3>
 * <pre>
 * ┌─────────────────────────────────────────────────────┐
 * │  data_key   VARCHAR(255) / TEXT  NOT NULL  PK       │
 * │  data_value MEDIUMTEXT   / TEXT  NOT NULL           │
 * │  checksum   CHAR(64)     / TEXT  NOT NULL  SHA-256  │
 * │  updated_at BIGINT       / INT   NOT NULL  epoch ms │
 * └─────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * DBConfig cfg = new DBConfig("localhost", 3306, "mydb", "root", "pass", "myplugin_");
 *
 * // Bidirectional sync – all handlers (USER / YAML only; CUSTOM needs clazz)
 * DBSyncUtils.syncAll(this, cfg);
 *
 * // Bidirectional sync – specific handler by registered key
 * DBSyncUtils.sync(this, "udata", cfg);
 *
 * // Bidirectional sync – specific handler object (CUSTOM type)
 * DBSyncUtils.sync(this, handler, cfg, MyDataCargo.class);
 *
 * // Force server → DB (no integrity check)
 * DBSyncUtils.uploadToDB(this, handler, cfg);
 *
 * // Force DB → server (no integrity check)
 * DBSyncUtils.downloadFromDB(this, handler, cfg);
 * }</pre>
 */
@DPPCoreVersion(since = "5.4.0")
public final class DBSyncUtils {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private DBSyncUtils() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Inner holder
    // ─────────────────────────────────────────────────────────────────────────

    /** Snapshot of a DB row used for integrity comparison. */
    private static final class RowMeta {
        final String value;
        final String checksum;  // SHA-256 hex
        final long   updatedAt; // epoch millis

        RowMeta(String value, String checksum, long updatedAt) {
            this.value     = value;
            this.checksum  = checksum;
            this.updatedAt = updatedAt;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public sync API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Bidirectional sync for <b>all</b> handlers registered in {@code plugin}.
     * <p>
     * {@link DataType#CUSTOM} handlers are skipped because a deserialisation
     * class ({@code clazz}) is required. Use
     * {@link #sync(DPlugin, IDataHandler, DBConfig, Class)} for those.
     *
     * @param plugin   Owning plugin
     * @param dbConfig DB connection configuration
     */
    public static void syncAll(DPlugin plugin, DBConfig dbConfig) {
        for (IDataHandler<?, ?> handler : plugin.getDataHandlers().values()) {
            if (handler.getDataType() == DataType.CUSTOM) {
                plugin.getLog().warning(
                        "[DBSyncUtils] syncAll: CUSTOM handler at path '" + handler.getPath()
                                + "' skipped — call sync(plugin, handler, dbConfig, clazz) directly.",
                        DLogManager.printDataContainerLogs);
                continue;
            }
            sync(plugin, handler, dbConfig, null);
        }
    }

    /**
     * Bidirectional sync for the handler registered under {@code dataKey}.
     *
     * @param plugin   Owning plugin
     * @param dataKey  Key used in {@link DPlugin#set(String, IDataHandler)}
     * @param dbConfig DB connection configuration
     */
    public static void sync(DPlugin plugin, String dataKey, DBConfig dbConfig) {
        sync(plugin, dataKey, dbConfig, null);
    }

    /**
     * Bidirectional sync for the handler registered under {@code dataKey}
     * (CUSTOM type variant).
     *
     * @param plugin      Owning plugin
     * @param dataKey     Key used in {@link DPlugin#set(String, IDataHandler)}
     * @param dbConfig    DB connection configuration
     * @param customClass Class implementing {@code DataCargo}; {@code null} for USER / YAML
     */
    public static void sync(DPlugin plugin, String dataKey, DBConfig dbConfig,
                            @Nullable Class<?> customClass) {
        IDataHandler<?, ?> handler = plugin.get(dataKey);
        if (handler == null) {
            plugin.getLog().warning(
                    "[DBSyncUtils] No handler registered under key: " + dataKey,
                    DLogManager.printDataContainerLogs);
            return;
        }
        sync(plugin, handler, dbConfig, customClass);
    }

    /**
     * Bidirectional sync for a specific handler (USER / YAML type).
     *
     * @param plugin   Owning plugin
     * @param handler  Handler to synchronise
     * @param dbConfig DB connection configuration
     */
    public static void sync(DPlugin plugin, IDataHandler<?, ?> handler, DBConfig dbConfig) {
        sync(plugin, handler, dbConfig, null);
    }

    /**
     * Bidirectional sync for a specific handler with full CUSTOM type support.
     *
     * <p>Steps performed automatically:
     * <ol>
     *   <li>Flush memory → disk ({@link IDataHandler#saveAll()}).</li>
     *   <li>Compare every key by SHA-256 checksum and {@code updated_at}.</li>
     *   <li>Upload server-side winners to DB.</li>
     *   <li>Download DB-side winners to disk.</li>
     *   <li>Reload disk → memory ({@link IDataHandler#loadAll(Class)}) if anything was downloaded.</li>
     * </ol>
     *
     * @param plugin      Owning plugin
     * @param handler     Handler to synchronise
     * @param dbConfig    DB connection configuration
     * @param customClass Class implementing {@code DataCargo}; {@code null} for USER / YAML
     */
    public static void sync(DPlugin plugin, IDataHandler<?, ?> handler,
                            DBConfig dbConfig, @Nullable Class<?> customClass) {
        // ① 메모리 → disk (DataContainer.saveAll 내부에서 useDB=true면 DB sync도 자동 수행)
        handler.saveAll();
        // ② useDB가 꺼져 있거나 외부 dbConfig가 명시된 경우 추가 sync
        //    useDB=true & plugin.dbConfig 동일 → saveAll 내에서 이미 처리됨 → 체크섬으로 빠르게 스킵
        syncFromDisk(plugin, handler, dbConfig, customClass);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal hooks (called by DataContainer / DPlugin)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Reads the YAML file for {@code fileKey} from disk and upserts the single
     * row to DB. Called automatically by {@link com.darksoldier1404.dppc.data.DataContainer#save}
     * and {@link com.darksoldier1404.dppc.data.SingleDataContainer#save()} when
     * {@code DPlugin.useDB} is {@code true}.
     *
     * @param plugin   Owning plugin
     * @param handler  Handler whose path contains the file
     * @param fileKey  File name without {@code .yml} extension
     * @param dbConfig DB connection configuration
     */
    public static void upsertSingle(DPlugin plugin, IDataHandler<?, ?> handler,
                                    String fileKey, DBConfig dbConfig) {
        File file = new File(plugin.getDataFolder(), handler.getPath() + "/" + fileKey + ".yml");
        if (!file.exists()) {
            plugin.getLog().warning(
                    "[DBSyncUtils] upsertSingle: file not found for key '" + fileKey + "'",
                    DLogManager.printDataContainerLogs);
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String tableName = buildTableName(dbConfig, handler.getPath());
        List<Map.Entry<String, YamlConfiguration>> rows =
                Collections.singletonList(new AbstractMap.SimpleEntry<>(fileKey, yaml));
        try (Connection conn = openConnection(plugin, dbConfig)) {
            createTableIfNotExists(conn, dbConfig, tableName);
            ensureIntegrityColumns(conn, dbConfig, tableName, plugin);
            upsertBatch(conn, dbConfig, tableName, rows);
            plugin.getLog().info(
                    "[DBSyncUtils] ↑ Upserted '" + fileKey + "' → '" + tableName + "'.",
                    DLogManager.printDataContainerLogs);
        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] upsertSingle failed for key '" + fileKey + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    /**
     * Bidirectional disk ↔ DB sync <em>without</em> calling
     * {@link com.darksoldier1404.dppc.data.IDataHandler#saveAll()} first.
     *
     * <p>Called automatically by
     * {@link com.darksoldier1404.dppc.data.DataContainer#saveAll()} when
     * {@code DPlugin.useDB} is {@code true}, after all files have already been
     * written to disk.
     *
     * <p>Integrity decision per key:
     * <ul>
     *   <li><b>Checksum match</b>      → skip.</li>
     *   <li><b>Server-only</b>         → upload to DB.</li>
     *   <li><b>DB-only</b>             → download to disk + reload handler.</li>
     *   <li><b>Checksum mismatch, DB newer</b>  → WARNING + download + reload.</li>
     *   <li><b>Checksum mismatch, server newer</b> → WARNING + upload.</li>
     * </ul>
     *
     * @param plugin      Owning plugin
     * @param handler     Handler to sync
     * @param dbConfig    DB connection configuration
     * @param customClass Class implementing {@code DataCargo}; {@code null} for USER / YAML
     */
    public static void syncFromDisk(DPlugin plugin, IDataHandler<?, ?> handler,
                                    DBConfig dbConfig, @Nullable Class<?> customClass) {
        String tableName = buildTableName(dbConfig, handler.getPath());
        File   folder    = new File(plugin.getDataFolder(), handler.getPath());

        try (Connection conn = openConnection(plugin, dbConfig)) {
            createTableIfNotExists(conn, dbConfig, tableName);
            ensureIntegrityColumns(conn, dbConfig, tableName, plugin);

            Map<String, RowMeta> dbMeta      = fetchAllRowMeta(conn, dbConfig, tableName);
            Map<String, File>    serverFiles = listYamlFiles(folder);

            Set<String> allKeys = new LinkedHashSet<>(dbMeta.keySet());
            allKeys.addAll(serverFiles.keySet());

            List<Map.Entry<String, YamlConfiguration>> toUpload   = new ArrayList<>();
            List<Map.Entry<String, String>>             toDownload = new ArrayList<>();
            int skipped = 0;

            for (String key : allKeys) {
                RowMeta dbRow      = dbMeta.get(key);
                File    serverFile = serverFiles.get(key);

                if (dbRow == null) {
                    // 서버에만 존재 → 업로드
                    toUpload.add(new AbstractMap.SimpleEntry<>(
                            key, YamlConfiguration.loadConfiguration(serverFile)));

                } else if (serverFile == null) {
                    // DB에만 존재 → 다운로드
                    toDownload.add(new AbstractMap.SimpleEntry<>(key, dbRow.value));

                } else {
                    // 양쪽 존재 → 무결성 비교
                    YamlConfiguration yaml        = YamlConfiguration.loadConfiguration(serverFile);
                    String            serverJson   = yamlToJson(yaml);
                    String            serverChksum = computeChecksum(serverJson);

                    if (dbRow.checksum.equals(serverChksum)) {
                        skipped++;  // 체크섬 일치 → 스킵

                    } else if (dbRow.updatedAt > serverFile.lastModified()) {
                        // ⚠ 무결성 충돌 — DB 최신
                        plugin.getLog().warning(
                                "[DBSyncUtils] Integrity conflict [" + tableName + "/" + key
                                        + "]: DB is newer (db=" + dbRow.updatedAt
                                        + " server=" + serverFile.lastModified()
                                        + "). Downloading DB version.",
                                DLogManager.printDataContainerLogs);
                        toDownload.add(new AbstractMap.SimpleEntry<>(key, dbRow.value));

                    } else {
                        // ⚠ 무결성 충돌 — 서버 최신 (또는 동시)
                        plugin.getLog().warning(
                                "[DBSyncUtils] Integrity conflict [" + tableName + "/" + key
                                        + "]: Server is newer (db=" + dbRow.updatedAt
                                        + " server=" + serverFile.lastModified()
                                        + "). Uploading server version.",
                                DLogManager.printDataContainerLogs);
                        toUpload.add(new AbstractMap.SimpleEntry<>(key, yaml));
                    }
                }
            }

            // 업로드 (서버 → DB)
            if (!toUpload.isEmpty()) {
                upsertBatch(conn, dbConfig, tableName, toUpload);
                plugin.getLog().info(
                        "[DBSyncUtils] ↑ Uploaded " + toUpload.size() + " row(s) → '" + tableName + "'.",
                        DLogManager.printDataContainerLogs);
            }

            // 다운로드 (DB → disk + 메모리 재로드)
            if (!toDownload.isEmpty()) {
                for (Map.Entry<String, String> e : toDownload) {
                    ConfigUtils.saveCustomData(plugin, jsonToYaml(e.getValue()),
                            e.getKey(), handler.getPath());
                }
                plugin.getLog().info(
                        "[DBSyncUtils] ↓ Downloaded " + toDownload.size() + " row(s) ← '" + tableName + "'.",
                        DLogManager.printDataContainerLogs);
                handler.loadAll(customClass);
            }

            plugin.getLog().info(
                    "[DBSyncUtils] syncFromDisk [" + tableName + "] ↑" + toUpload.size()
                            + " ↓" + toDownload.size() + " =" + skipped + " skipped.",
                    DLogManager.printDataContainerLogs);

        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] syncFromDisk failed for '" + handler.getPath() + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Force upload / download (integrity check 생략)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Force-uploads all disk files to DB (no integrity check, always overwrites).
     * Calls {@link IDataHandler#saveAll()} before reading files.
     *
     * @param plugin   Owning plugin
     * @param handler  Handler to upload
     * @param dbConfig DB connection configuration
     */
    public static void uploadToDB(DPlugin plugin, IDataHandler<?, ?> handler, DBConfig dbConfig) {
        handler.saveAll();

        String            tableName   = buildTableName(dbConfig, handler.getPath());
        Map<String, File> serverFiles = listYamlFiles(new File(plugin.getDataFolder(), handler.getPath()));

        if (serverFiles.isEmpty()) {
            plugin.getLog().info(
                    "[DBSyncUtils] uploadToDB: no files at '" + handler.getPath() + "', skipping.",
                    DLogManager.printDataContainerLogs);
            return;
        }

        List<Map.Entry<String, YamlConfiguration>> rows = new ArrayList<>();
        for (Map.Entry<String, File> e : serverFiles.entrySet()) {
            rows.add(new AbstractMap.SimpleEntry<>(e.getKey(),
                    YamlConfiguration.loadConfiguration(e.getValue())));
        }

        try (Connection conn = openConnection(plugin, dbConfig)) {
            createTableIfNotExists(conn, dbConfig, tableName);
            ensureIntegrityColumns(conn, dbConfig, tableName, plugin);
            upsertBatch(conn, dbConfig, tableName, rows);
            plugin.getLog().info(
                    "[DBSyncUtils] ↑ Force-uploaded " + rows.size() + " row(s) → '" + tableName + "'.",
                    DLogManager.printDataContainerLogs);
        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] uploadToDB failed for '" + handler.getPath() + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    /**
     * Force-downloads all DB rows to disk and reloads the handler (no integrity check).
     *
     * @param plugin      Owning plugin
     * @param handler     Handler to download into
     * @param dbConfig    DB connection configuration
     * @param customClass Class implementing {@code DataCargo}; {@code null} for USER / YAML
     */
    public static void downloadFromDB(DPlugin plugin, IDataHandler<?, ?> handler,
                                      DBConfig dbConfig, @Nullable Class<?> customClass) {
        String tableName = buildTableName(dbConfig, handler.getPath());

        try (Connection conn = openConnection(plugin, dbConfig)) {
            createTableIfNotExists(conn, dbConfig, tableName);
            ensureIntegrityColumns(conn, dbConfig, tableName, plugin);

            Map<String, RowMeta> dbMeta = fetchAllRowMeta(conn, dbConfig, tableName);
            if (dbMeta.isEmpty()) {
                plugin.getLog().info(
                        "[DBSyncUtils] downloadFromDB: no rows in '" + tableName + "', skipping.",
                        DLogManager.printDataContainerLogs);
                return;
            }

            for (Map.Entry<String, RowMeta> e : dbMeta.entrySet()) {
                ConfigUtils.saveCustomData(plugin, jsonToYaml(e.getValue().value),
                        e.getKey(), handler.getPath());
            }

            handler.loadAll(customClass);

            plugin.getLog().info(
                    "[DBSyncUtils] ↓ Force-downloaded " + dbMeta.size() + " row(s) ← '" + tableName + "'.",
                    DLogManager.printDataContainerLogs);
        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] downloadFromDB failed for '" + handler.getPath() + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    /** Overload for USER / YAML handlers (no {@code customClass} needed). */
    public static void downloadFromDB(DPlugin plugin, IDataHandler<?, ?> handler, DBConfig dbConfig) {
        downloadFromDB(plugin, handler, dbConfig, null);
    }

    /**
     * Downloads a single key from DB to disk <b>without</b> triggering a handler reload.
     * Uses checksum + {@code updated_at} to decide whether to write the file.
     *
     * <ul>
     *   <li><b>DB-only</b>                       → write to disk.</li>
     *   <li><b>Checksum match</b>                → skip (already in sync).</li>
     *   <li><b>DB newer</b>                      → overwrite disk file.</li>
     *   <li><b>Server newer / same timestamp</b> → skip (let save handle upload).</li>
     * </ul>
     *
     * <p>Called automatically by
     * {@link com.darksoldier1404.dppc.data.DataContainer#load} and
     * {@link com.darksoldier1404.dppc.data.SingleDataContainer#load} when
     * {@code DPlugin.useDB} is {@code true}.
     *
     * @param plugin   Owning plugin
     * @param handler  Handler whose path contains the file
     * @param fileKey  File name without {@code .yml} extension
     * @param dbConfig DB connection configuration
     */
    public static void downloadSingleKeyToDisk(DPlugin plugin, IDataHandler<?, ?> handler,
                                               String fileKey, DBConfig dbConfig) {
        String tableName = buildTableName(dbConfig, handler.getPath());
        File   serverFile = new File(plugin.getDataFolder(), handler.getPath() + "/" + fileKey + ".yml");

        try (Connection conn = openConnection(plugin, dbConfig)) {
            createTableIfNotExists(conn, dbConfig, tableName);
            ensureIntegrityColumns(conn, dbConfig, tableName, plugin);

            RowMeta dbRow = fetchSingleRowMeta(conn, dbConfig, tableName, fileKey);
            if (dbRow == null) {
                // DB에 해당 키 없음 → 디스크에서만 로드 (아무것도 하지 않음)
                return;
            }

            if (!serverFile.exists()) {
                // 디스크에 없음 → DB에서 다운로드
                ConfigUtils.saveCustomData(plugin, jsonToYaml(dbRow.value), fileKey, handler.getPath());
                plugin.getLog().info(
                        "[DBSyncUtils] ↓ Downloaded '" + fileKey + "' ← '" + tableName + "' (disk absent).",
                        DLogManager.printDataContainerLogs);
            } else {
                // 양쪽 존재 → 체크섬 비교
                YamlConfiguration yaml        = YamlConfiguration.loadConfiguration(serverFile);
                String            serverJson   = yamlToJson(yaml);
                String            serverChksum = computeChecksum(serverJson);

                if (dbRow.checksum.equals(serverChksum)) {
                    // 동일 → 스킵
                } else if (dbRow.updatedAt > serverFile.lastModified()) {
                    // DB 최신 → 덮어쓰기
                    ConfigUtils.saveCustomData(plugin, jsonToYaml(dbRow.value), fileKey, handler.getPath());
                    plugin.getLog().info(
                            "[DBSyncUtils] ↓ Downloaded newer '" + fileKey + "' ← '" + tableName
                                    + "' (db=" + dbRow.updatedAt + " server=" + serverFile.lastModified() + ").",
                            DLogManager.printDataContainerLogs);
                }
                // else: 서버 최신 → save() 시 업로드로 처리
            }
        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] downloadSingleKeyToDisk failed for key '" + fileKey + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    /**
     * Downloads all DB rows to disk <b>without</b> triggering a handler reload.
     * Uses checksum + {@code updated_at} to decide which files to write.
     *
     * <ul>
     *   <li><b>DB-only</b>      → write to disk.</li>
     *   <li><b>Checksum match</b> → skip.</li>
     *   <li><b>DB newer</b>     → overwrite disk file.</li>
     *   <li><b>Server newer</b> → skip.</li>
     * </ul>
     *
     * <p>Called automatically by
     * {@link com.darksoldier1404.dppc.data.DataContainer#loadAll} when
     * {@code DPlugin.useDB} is {@code true}.
     *
     * @param plugin   Owning plugin
     * @param handler  Handler to sync
     * @param dbConfig DB connection configuration
     */
    public static void downloadAllKeysToDisk(DPlugin plugin, IDataHandler<?, ?> handler,
                                             DBConfig dbConfig) {
        String            tableName   = buildTableName(dbConfig, handler.getPath());
        File              folder      = new File(plugin.getDataFolder(), handler.getPath());
        Map<String, File> serverFiles = listYamlFiles(folder);

        try (Connection conn = openConnection(plugin, dbConfig)) {
            createTableIfNotExists(conn, dbConfig, tableName);
            ensureIntegrityColumns(conn, dbConfig, tableName, plugin);

            Map<String, RowMeta> dbMeta = fetchAllRowMeta(conn, dbConfig, tableName);
            if (dbMeta.isEmpty()) return;

            int downloaded = 0;
            int skipped    = 0;

            for (Map.Entry<String, RowMeta> e : dbMeta.entrySet()) {
                String  key        = e.getKey();
                RowMeta dbRow      = e.getValue();
                File    serverFile = serverFiles.get(key);

                if (serverFile == null) {
                    // 디스크에 없음 → 다운로드
                    ConfigUtils.saveCustomData(plugin, jsonToYaml(dbRow.value), key, handler.getPath());
                    downloaded++;
                } else {
                    // 양쪽 존재 → 체크섬 비교
                    YamlConfiguration yaml        = YamlConfiguration.loadConfiguration(serverFile);
                    String            serverJson   = yamlToJson(yaml);
                    String            serverChksum = computeChecksum(serverJson);

                    if (dbRow.checksum.equals(serverChksum)) {
                        skipped++;
                    } else if (dbRow.updatedAt > serverFile.lastModified()) {
                        // DB 최신 → 덮어쓰기
                        ConfigUtils.saveCustomData(plugin, jsonToYaml(dbRow.value), key, handler.getPath());
                        downloaded++;
                    } else {
                        // 서버 최신 → save() 시 업로드로 처리
                        skipped++;
                    }
                }
            }

            plugin.getLog().info(
                    "[DBSyncUtils] downloadAllKeysToDisk [" + tableName + "] ↓" + downloaded
                            + " =" + skipped + " skipped.",
                    DLogManager.printDataContainerLogs);
        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] downloadAllKeysToDisk failed for '" + handler.getPath() + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    /**
     * Deletes a single row from the DB table that corresponds to {@code handler}'s path.
     *
     * @param plugin   Owning plugin
     * @param handler  Handler whose path determines the target table
     * @param fileKey  Row key (file name without {@code .yml})
     * @param dbConfig DB connection configuration
     * @return {@code true} if a row was deleted
     */
    public static boolean deleteFromDB(DPlugin plugin, IDataHandler<?, ?> handler,
                                       String fileKey, DBConfig dbConfig) {
        String tableName = buildTableName(dbConfig, handler.getPath());
        String sql = dbConfig.getDbType() == DBType.MYSQL
                ? "DELETE FROM `" + tableName + "` WHERE `data_key` = ?;"
                : "DELETE FROM \"" + tableName + "\" WHERE \"data_key\" = ?;";

        try (Connection conn = openConnection(plugin, dbConfig);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileKey);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] deleteFromDB failed for key '" + fileKey + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Connection
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens a fresh JDBC connection. Caller is responsible for closing it.
     *
     * @param plugin Owning plugin (needed for SQLite file path resolution)
     * @param config DB configuration
     * @return Open {@link Connection}
     * @throws SQLException           on JDBC error
     * @throws ClassNotFoundException if SQLite driver is not found
     */
    public static Connection openConnection(DPlugin plugin, DBConfig config)
            throws SQLException, ClassNotFoundException {
        if (config.getDbType() == DBType.MYSQL) {
            String url = "jdbc:mysql://" + config.getHost() + ":" + config.getPort()
                    + "/" + config.getDatabase()
                    + "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&characterEncoding=UTF-8&serverTimezone=UTC";
            return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        } else {
            Class.forName("org.sqlite.JDBC");
            File dbFile   = new File(plugin.getDataFolder(), config.getFilePath());
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                plugin.getLog().warning(
                        "[DBSyncUtils] Could not create directory: " + parentDir.getPath(),
                        DLogManager.printDataContainerLogs);
            }
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DDL
    // ─────────────────────────────────────────────────────────────────────────

    private static String buildTableName(DBConfig config, String path) {
        return (config.getTablePrefix() + path)
                .replaceAll("[^a-zA-Z0-9_]", "_")
                .toLowerCase();
    }

    /**
     * Creates the table with the full 4-column schema if it does not yet exist.
     * Includes {@code checksum} and {@code updated_at} from the start.
     */
    private static void createTableIfNotExists(Connection conn, DBConfig config,
                                               String tableName) throws SQLException {
        String sql = config.getDbType() == DBType.MYSQL
                ? "CREATE TABLE IF NOT EXISTS `" + tableName + "` ("
                + "`data_key`   VARCHAR(255) NOT NULL, "
                + "`data_value` MEDIUMTEXT   NOT NULL, "
                + "`checksum`   CHAR(64)     NOT NULL DEFAULT '', "
                + "`updated_at` BIGINT       NOT NULL DEFAULT 0, "
                + "PRIMARY KEY (`data_key`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
                : "CREATE TABLE IF NOT EXISTS \"" + tableName + "\" ("
                + "\"data_key\"   TEXT    NOT NULL PRIMARY KEY, "
                + "\"data_value\" TEXT    NOT NULL, "
                + "\"checksum\"   TEXT    NOT NULL DEFAULT '', "
                + "\"updated_at\" INTEGER NOT NULL DEFAULT 0"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Migrates tables created with the old 2-column schema by adding
     * {@code checksum} and {@code updated_at} columns if they are absent.
     */
    private static void ensureIntegrityColumns(Connection conn, DBConfig config,
                                               String tableName, DPlugin plugin) {
        try {
            if (config.getDbType() == DBType.MYSQL) {
                String checkSql =
                        "SELECT COUNT(*) FROM information_schema.COLUMNS "
                                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = 'checksum';";
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setString(1, tableName);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            try (Statement st = conn.createStatement()) {
                                st.execute("ALTER TABLE `" + tableName + "` "
                                        + "ADD COLUMN `checksum`   CHAR(64) NOT NULL DEFAULT '', "
                                        + "ADD COLUMN `updated_at` BIGINT   NOT NULL DEFAULT 0;");
                            }
                            plugin.getLog().info(
                                    "[DBSyncUtils] Migrated table '" + tableName + "' — added integrity columns.",
                                    DLogManager.printDataContainerLogs);
                        }
                    }
                }
            } else {
                // SQLite: PRAGMA table_info
                boolean hasChecksum = false;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("PRAGMA table_info(\"" + tableName + "\");")) {
                    while (rs.next()) {
                        if ("checksum".equals(rs.getString("name"))) {
                            hasChecksum = true;
                            break;
                        }
                    }
                }
                if (!hasChecksum) {
                    try (Statement st = conn.createStatement()) {
                        st.execute("ALTER TABLE \"" + tableName
                                + "\" ADD COLUMN \"checksum\" TEXT NOT NULL DEFAULT '';");
                        st.execute("ALTER TABLE \"" + tableName
                                + "\" ADD COLUMN \"updated_at\" INTEGER NOT NULL DEFAULT 0;");
                    }
                    plugin.getLog().info(
                            "[DBSyncUtils] Migrated table '" + tableName + "' — added integrity columns.",
                            DLogManager.printDataContainerLogs);
                }
            }
        } catch (SQLException e) {
            plugin.getLog().warning(
                    "[DBSyncUtils] Column migration failed for '" + tableName + "': " + e.getMessage(),
                    DLogManager.printDataContainerLogs);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DML
    // ─────────────────────────────────────────────────────────────────────────

    /** Reads all rows and returns key → {@link RowMeta} mapping. */
    private static Map<String, RowMeta> fetchAllRowMeta(Connection conn, DBConfig config,
                                                        String tableName) throws SQLException {
        Map<String, RowMeta> result = new LinkedHashMap<>();
        String sql = config.getDbType() == DBType.MYSQL
                ? "SELECT `data_key`, `data_value`, `checksum`, `updated_at` FROM `" + tableName + "`;"
                : "SELECT \"data_key\", \"data_value\", \"checksum\", \"updated_at\" FROM \"" + tableName + "\";";
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("data_key"),
                        new RowMeta(rs.getString("data_value"),
                                rs.getString("checksum"),
                                rs.getLong("updated_at")));
            }
        }
        return result;
    }

    /**
     * Reads a single row by {@code fileKey} and returns the {@link RowMeta},
     * or {@code null} if the row does not exist.
     */
    @Nullable
    private static RowMeta fetchSingleRowMeta(Connection conn, DBConfig config,
                                              String tableName, String fileKey) throws SQLException {
        String sql = config.getDbType() == DBType.MYSQL
                ? "SELECT `data_key`, `data_value`, `checksum`, `updated_at` FROM `" + tableName
                  + "` WHERE `data_key` = ?;"
                : "SELECT \"data_key\", \"data_value\", \"checksum\", \"updated_at\" FROM \"" + tableName
                  + "\" WHERE \"data_key\" = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RowMeta(rs.getString("data_value"),
                            rs.getString("checksum"),
                            rs.getLong("updated_at"));
                }
            }
        }
        return null;
    }

    /**
     * Batch-upserts rows. Sets {@code checksum} (SHA-256) and {@code updated_at}
     * (current epoch ms) automatically.
     */
    private static void upsertBatch(Connection conn, DBConfig config, String tableName,
                                    List<Map.Entry<String, YamlConfiguration>> rows) throws SQLException {
        String sql = config.getDbType() == DBType.MYSQL
                ? "INSERT INTO `" + tableName
                + "` (`data_key`, `data_value`, `checksum`, `updated_at`) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "`data_value` = VALUES(`data_value`), "
                + "`checksum`   = VALUES(`checksum`), "
                + "`updated_at` = VALUES(`updated_at`);"
                : "INSERT OR REPLACE INTO \"" + tableName
                + "\" (\"data_key\", \"data_value\", \"checksum\", \"updated_at\") VALUES (?, ?, ?, ?);";

        long now = System.currentTimeMillis();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<String, YamlConfiguration> entry : rows) {
                String json     = yamlToJson(entry.getValue());
                String checksum = computeChecksum(json);
                ps.setString(1, entry.getKey());
                ps.setString(2, json);
                ps.setString(3, checksum);
                ps.setLong(4, now);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // File helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Lists {@code *.yml} files in {@code folder}, keyed by name without extension. */
    private static Map<String, File> listYamlFiles(File folder) {
        Map<String, File> map = new LinkedHashMap<>();
        if (folder.isDirectory()) {
            File[] files = folder.listFiles(f -> f.isFile() && f.getName().endsWith(".yml"));
            if (files != null) {
                for (File f : files) {
                    map.put(f.getName().replace(".yml", ""), f);
                }
            }
        }
        return map;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Serialisation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Serialises a {@link YamlConfiguration} to a string stored in the DB.
     * <p>
     * Uses Bukkit's own {@link YamlConfiguration#saveToString()} so that every
     * type Bukkit supports (ItemStack, Location, TimeZone, …) is handled
     * correctly without relying on Gson reflection — which fails for sealed
     * JDK classes such as {@code java.util.TimeZone} on Java 9+.
     */
    private static String yamlToJson(YamlConfiguration config) {
        return config.saveToString();
    }

    /**
     * Deserialises a string previously produced by {@link #yamlToJson} back
     * into a {@link YamlConfiguration}.
     * <p>
     * Falls back to legacy Gson-JSON parsing for rows written by older versions
     * of this utility, so existing data is not lost after an upgrade.
     */
    private static YamlConfiguration jsonToYaml(String data) {
        YamlConfiguration config = new YamlConfiguration();
        // ① 신규 포맷: Bukkit YAML 문자열
        try {
            config.loadFromString(data);
            return config;
        } catch (Exception ignored) {
            // ② 구 포맷 fallback: Gson JSON (이전 버전 호환)
            try {
                Map<String, Object> map = GSON.fromJson(data, MAP_TYPE);
                if (map != null) map.forEach(config::set);
            } catch (Exception ignored2) {
                // 파싱 실패 시 빈 config 반환
            }
            return config;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Integrity (SHA-256)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Computes the SHA-256 hex digest of {@code data}.
     * Returns an empty string if the algorithm is somehow unavailable.
     */
    private static String computeChecksum(String data) {
        try {
            MessageDigest md   = MessageDigest.getInstance("SHA-256");
            byte[]        hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex  = new StringBuilder(64);
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}


/*
 * Special thanks to: tr7zw, SoSeDiK, Broken arrow
 * From TR's Mod Workshop
 */
package com.darksoldier1404.dppc.data;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.api.logger.DLogManager;
import com.darksoldier1404.dppc.api.logger.DLogNode;
import com.darksoldier1404.dppc.data.sql.DBConfig;
import com.darksoldier1404.dppc.data.sql.DBSyncUtils;
import com.darksoldier1404.dppc.data.sql.DBType;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@DPPCoreVersion(since = "5.3.0")
public class DPlugin extends JavaPlugin {
    public YamlConfiguration config;
    public String prefix;
    private final Map<String, IDataHandler<?, ?>> data = new HashMap<>();
    private final boolean useDLang;
    private final boolean useDB;
    private final @Nullable DBType dbType;
    private @Nullable DBConfig dbConfig;
    private @Nullable DLang lang;
    public @NotNull DLogNode log;

    public DPlugin() {
        this(false);
    }

    public DPlugin(boolean useDLang) {
        this.useDLang = useDLang;
        this.useDB = false;
        this.dbType = null;
        this.dbConfig = null;
        log = DLogManager.init(this);
    }

    public DPlugin(boolean useDLang, boolean useDB, @NotNull DBType dbType) {
        this.useDLang = useDLang;
        this.useDB = useDB;
        this.dbType = dbType;
        this.dbConfig = null;
        log = DLogManager.init(this);
    }

    /**
     * Creates a DPlugin with DB support enabled.
     * {@code useDB} is automatically set to {@code true} and
     * {@code dbType} is derived from {@code dbConfig}.
     *
     * @param useDLang Whether to use DLang
     * @param dbConfig Database connection configuration
     */
    public DPlugin(boolean useDLang, @NotNull DBConfig dbConfig) {
        this.useDLang = useDLang;
        this.useDB = true;
        this.dbType = dbConfig.getDbType();
        this.dbConfig = dbConfig;
        log = DLogManager.init(this);
    }

    public void init() {
        this.config = ConfigUtils.loadDefaultPluginConfig(this);
        this.prefix = ColorUtils.applyColor(config.getString("Settings.prefix"));
        initDLang();
    }

    @Override
    public @NotNull YamlConfiguration getConfig() {
        return config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void set(String key, IDataHandler<?, ?> value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends IDataHandler<?, ?>> T get(String key) {
        return (T) data.get(key);
    }

    /**
     * Returns an unmodifiable view of the internal data handler map.
     * Key is the path string used when the handler was registered.
     */
    public Map<String, IDataHandler<?, ?>> getDataHandlers() {
        return Collections.unmodifiableMap(data);
    }

    public @NotNull DLogNode getLog() {
        return log;
    }

    public void reload() {
        init();
    }

    @Override
    public void saveConfig() {
        ConfigUtils.savePluginConfig(this, config);
    }

    public void initDLang() {
        if (this.useDLang) {
            if (this.config.getString("Settings.Lang") == null) {
                this.config.set("Settings.Lang", "en_US");
            }
            lang = new DLang();
            lang.initPluginLang(this);
            lang.setCurrentLang(Locale.forLanguageTag(this.config.getString("Settings.Lang").replace("_", "-")));
        } else {
            lang = null;
        }
    }

    public void saveAllData() {
        ConfigUtils.savePluginConfig(this, config);
        for (Map.Entry<String, IDataHandler<?, ?>> entry : data.entrySet()) {
            IDataHandler<?, ?> handler = entry.getValue();
            handler.saveAll();
        }
        saveLog();
    }

    public void saveDataContainer() {
        for (Map.Entry<String, IDataHandler<?, ?>> entry : data.entrySet()) {
            IDataHandler<?, ?> handler = entry.getValue();
            handler.saveAll();
        }
    }

    @Nullable
    public <K, V, T extends IDataHandler<K, V>> T loadDataContainer(T container) {
        return loadDataContainer(container, null);
    }

    @Nullable
    public <K, V, T extends IDataHandler<K, V>> T loadDataContainer(T container, Class<?> clazz) {
        try {
            IDataHandler<K, V> idh = container.loadAll(clazz);
            data.put(container.getPath(), idh);
            return (T) idh;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUseDLang() {
        return useDLang;
    }

    public boolean isUseDB() {
        return useDB;
    }

    public @Nullable DBConfig getDbConfig() {
        return dbConfig;
    }

    /**
     * Overrides the DB configuration at runtime.
     * Has no effect if this plugin was not constructed with {@code useDB = true}.
     */
    public void setDbConfig(@NotNull DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    // ── Internal DB sync hooks (called by DataContainer / SingleDataContainer) ──

    /**
     * Upserts a single YAML file to the database after a {@code save(key)} call.
     * No-op if DB is not enabled or {@code dbConfig} is not set.
     *
     * @param handler Handler that owns the file
     * @param fileKey File key (= file name without {@code .yml})
     */
    void syncKeyToDB(IDataHandler<?, ?> handler, String fileKey) {
        if (!useDB || dbConfig == null) return;
        DBSyncUtils.upsertSingle(this, handler, fileKey, dbConfig);
    }

    /**
     * Performs a bidirectional disk ↔ DB sync after a {@code saveAll()} call.
     * No-op if DB is not enabled or {@code dbConfig} is not set.
     *
     * @param handler     Handler to sync
     * @param customClass Class implementing {@code DataCargo}; {@code null} for USER / YAML
     */
    void syncAllToDB(IDataHandler<?, ?> handler, @Nullable Class<?> customClass) {
        if (!useDB || dbConfig == null) return;
        DBSyncUtils.syncFromDisk(this, handler, dbConfig, customClass);
    }

    /**
     * Downloads a single key from DB to disk before a {@code load(key)} call.
     * Uses checksum + {@code updated_at} comparison; only overwrites when DB is newer.
     * No-op if DB is not enabled or {@code dbConfig} is not set.
     *
     * @param handler Handler that owns the file
     * @param fileKey File key (= file name without {@code .yml})
     */
    void syncKeyFromDB(IDataHandler<?, ?> handler, String fileKey) {
        if (!useDB || dbConfig == null) return;
        DBSyncUtils.downloadSingleKeyToDisk(this, handler, fileKey, dbConfig);
    }

    /**
     * Downloads all keys from DB to disk before a {@code loadAll()} call.
     * Uses checksum + {@code updated_at} comparison; only overwrites DB-newer files.
     * No-op if DB is not enabled or {@code dbConfig} is not set.
     *
     * @param handler Handler to sync
     */
    void syncAllFromDB(IDataHandler<?, ?> handler) {
        if (!useDB || dbConfig == null) return;
        DBSyncUtils.downloadAllKeysToDisk(this, handler, dbConfig);
    }

    public @Nullable DLang getLang() {
        return lang;
    }

    public void saveLog() {
        DLogManager.saveLogNode(this, false);
    }
}
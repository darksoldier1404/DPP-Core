package com.darksoldier1404.dppc.api.vfx;

import com.darksoldier1404.dppmc.vfx.protocol.EffectIds;
import com.darksoldier1404.dppmc.vfx.protocol.EffectJson;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol.UploadResult.Status;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Effects uploaded from the DPP-ModCore editor (1.14.0, protocol 3), stored as {@code <folder>/<id>.json}. Every check
 * runs before anything touches the disk, in this order: permission, id, size, JSON, existing file. A write goes to a
 * temporary file beside the target that is then moved over it.
 */
public final class VfxUploads {
    private VfxUploads() {
    }

    public static VfxProtocol.UploadResult handle(File folder, boolean allowed, VfxProtocol.Upload upload) {
        String id = upload.id();
        if (!allowed) {
            return result(id, Status.NO_PERMISSION, "dppc.vfx.edit");
        }
        if (!EffectIds.valid(id)) {
            return result(id, Status.BAD_ID, "");
        }
        Path root = folder.toPath().toAbsolutePath().normalize();
        Path target = root.resolve(id + ".json").normalize();
        // EffectIds already rules out leaving the folder; this guards the rule itself
        if (!target.startsWith(root) || target.equals(root)) {
            return result(id, Status.BAD_ID, "");
        }
        int bytes = upload.json().getBytes(StandardCharsets.UTF_8).length;
        if (bytes > VfxProtocol.MAX_JSON_BYTES) {
            return result(id, Status.TOO_LARGE, bytes + " > " + VfxProtocol.MAX_JSON_BYTES);
        }
        try {
            EffectJson.parse(upload.json());
        } catch (IllegalArgumentException e) {
            return result(id, Status.INVALID, String.valueOf(e.getMessage()));
        }
        if (Files.exists(target) && !upload.overwrite()) {
            return result(id, Status.EXISTS, "");
        }
        Path temp = null;
        try {
            Files.createDirectories(target.getParent());
            temp = Files.createTempFile(target.getParent(), ".upload-", ".tmp");
            Files.writeString(temp, upload.json(), StandardCharsets.UTF_8);
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            temp = null;
        } catch (IOException e) {
            return result(id, Status.UNSUPPORTED, "could not write: " + e.getMessage());
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                    // the write already failed; that is what the client hears about
                }
            }
        }
        return result(id, Status.OK, "");
    }

    private static VfxProtocol.UploadResult result(String id, Status status, String message) {
        return new VfxProtocol.UploadResult(id, status, message);
    }
}

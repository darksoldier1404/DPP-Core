package com.darksoldier1404.dppc.api.vfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darksoldier1404.dppmc.vfx.effect.EffectSpec;
import com.darksoldier1404.dppmc.vfx.effect.EmitterSpec;
import com.darksoldier1404.dppmc.vfx.protocol.EffectJson;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol.UploadResult.Status;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VfxUploadsTest {
    private static final EffectSpec SPEC = EffectSpec.of(EmitterSpec.defaultBurst());
    private static final String JSON = EffectJson.write(SPEC);

    @TempDir
    File root;

    private File folder() {
        return new File(root, "vfx");
    }

    private List<Path> files() throws IOException {
        if (!root.exists()) {
            return List.of();
        }
        try (Stream<Path> s = Files.walk(root.toPath())) {
            return s.filter(Files::isRegularFile).toList();
        }
    }

    private VfxProtocol.UploadResult upload(String id, boolean overwrite, String json) {
        return VfxUploads.handle(folder(), true, new VfxProtocol.Upload(id, overwrite, json));
    }

    @Test
    void withoutPermissionNothingIsWritten() throws IOException {
        VfxProtocol.UploadResult r = VfxUploads.handle(folder(), false, new VfxProtocol.Upload("a", true, JSON));
        assertEquals(Status.NO_PERMISSION, r.status());
        assertEquals(List.of(), files());
    }

    @Test
    void badIdsAreRefusedAndNothingIsWritten() throws IOException {
        for (String id : new String[] {"../x", "/etc/x", "a/../b", "A", "", "x".repeat(65), "a\\b", "a//b", "x/"}) {
            assertEquals(Status.BAD_ID, upload(id, true, JSON).status(), id);
        }
        assertEquals(List.of(), files());
    }

    @Test
    void oversizedJsonIsTooLarge() throws IOException {
        String big = JSON + " ".repeat(VfxProtocol.MAX_JSON_BYTES);
        assertEquals(Status.TOO_LARGE, upload("a", true, big).status());
        assertEquals(List.of(), files());
    }

    @Test
    void invalidJsonIsInvalidWithTheFieldPath() throws IOException {
        VfxProtocol.UploadResult r = upload("a", true, JSON.replace("\"SPHERE\"", "\"CUBEY\""));
        assertEquals(Status.INVALID, r.status());
        assertTrue(r.message().contains("emitters[0].shape.kind"), r.message());
        assertEquals(List.of(), files());
    }

    @Test
    void newIdIsWrittenAndSubfoldersAreCreated() {
        assertEquals(Status.OK, upload("fx/fire/big", false, JSON).status());
        assertTrue(new File(folder(), "fx/fire/big.json").isFile());
    }

    @Test
    void existingIdWithoutOverwriteIsExistsAndUnchanged() throws IOException {
        upload("a", false, JSON);
        String other = EffectJson.write(EffectSpec.of(EmitterSpec.defaultBurst(), EmitterSpec.defaultBurst()));
        assertEquals(Status.EXISTS, upload("a", false, other).status());
        assertEquals(JSON, Files.readString(new File(folder(), "a.json").toPath(), StandardCharsets.UTF_8));
    }

    @Test
    void overwriteReplacesTheFile() throws IOException {
        upload("a", false, JSON);
        String other = EffectJson.write(EffectSpec.of(EmitterSpec.defaultBurst(), EmitterSpec.defaultBurst()));
        assertEquals(Status.OK, upload("a", true, other).status());
        assertEquals(other, Files.readString(new File(folder(), "a.json").toPath(), StandardCharsets.UTF_8));
        assertEquals(1, files().size(), "no temporary file remains");
    }

    @Test
    void theWrittenFileParsesBackToTheUploadedSpec() throws IOException {
        upload("a", false, JSON);
        assertEquals(SPEC, EffectJson.parse(Files.readString(new File(folder(), "a.json").toPath(),
                StandardCharsets.UTF_8)));
    }
}

package com.darksoldier1404.dppc.api.vfx;

import com.darksoldier1404.dppmc.vfx.build.BeamBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * A lightning-style beam sent to DPP-ModCore clients (DPP-ModCore 1.13.0). Obtain with {@link VfxAPI#beam()}; the
 * setters are the engine's {@link BeamBuilder}.
 */
public final class VfxBeam extends BeamBuilder<VfxBeam> {
    VfxBeam() {
    }

    /** To the players near {@code from}. @return how many it was sent to */
    public int play(Location from, Location to) {
        return VfxAPI.beam(from, to, spec());
    }

    public boolean play(Player player, Location from, Location to) {
        return VfxAPI.beam(player, from, to, spec());
    }
}

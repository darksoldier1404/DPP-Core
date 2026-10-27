package com.darksoldier1404.dppc.api.vfx;

import com.darksoldier1404.dppmc.vfx.build.EmitterBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * A particle emitter sent to DPP-ModCore clients (DPP-ModCore 1.13.0). Obtain with {@link VfxAPI#emitter()}; the
 * setters are the engine's {@link EmitterBuilder}, the same ones a client mod uses.
 */
public final class VfxEmitter extends EmitterBuilder<VfxEmitter> {
    VfxEmitter() {
    }

    /** To the players near {@code at}. @return how many it was sent to */
    public int play(Location at) {
        return VfxAPI.emit(at, spec());
    }

    /** To one player. @return false when the player's client does not draw effects */
    public boolean play(Player player, Location at) {
        return VfxAPI.emit(player, at, spec());
    }
}

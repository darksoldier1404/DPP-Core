package com.darksoldier1404.dppc.plugin.commands;

import com.darksoldier1404.dppc.api.vfx.VfxAPI;
import com.darksoldier1404.dppmc.vfx.effect.SpriteId;
import com.darksoldier1404.dppmc.vfx.emitter.ShapeSpec;
import com.darksoldier1404.dppmc.vfx.tween.Easings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Developer command for checking {@link VfxAPI} against live DPP-ModCore 1.13.0 clients: the effect goes to every
 * nearby player whose client draws effects, and the command says how many that was.
 * <p>
 * Usage: {@code /dppcvfx <burst|beam|sparks|clear>}
 */
public class DPPCVfxTestCommand implements CommandExecutor, TabCompleter {
    private static final List<String> FUNCTIONS = Arrays.asList("burst", "beam", "sparks", "clear");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cYou must be op to use this command.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by a player.");
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("§e/" + label + " <burst|beam|sparks|clear> §7— your client draws effects: "
                    + (VfxAPI.canSee(p) ? "§ayes" : "§cno"));
            return true;
        }
        Location eye = p.getEyeLocation();
        Location ahead = eye.clone().add(eye.getDirection().multiply(4));
        int sent;
        switch (args[0].toLowerCase()) {
            case "burst":
                sent = VfxAPI.emitter()
                        .shape(ShapeSpec.sphere(0.5f)).count(200).lifetime(40).speed(0.2f)
                        .gradient(Easings.LINEAR, 0xFFCC33, 0xFF6611, 0xFF2200)
                        .gravity(0f, -0.01f, 0f).drag(0.05f)
                        .play(ahead);
                break;
            case "beam":
                sent = VfxAPI.beam().color(0x88CCFF).width(0.15f).arc(0.6f).duration(20).segments(16)
                        .play(eye.clone().add(0, 3, 0), ahead);
                break;
            case "sparks":
                sent = VfxAPI.emitter()
                        .shape(ShapeSpec.point()).count(60).lifetime(50).speed(0.35f)
                        .sprite(SpriteId.SPARK).color(0xFFEE88, 0xFF5500, Easings.LINEAR)
                        .gravity(0f, -0.03f, 0f).collide(0.5f, 0.2f).trail(6).stretch(2f)
                        .play(ahead);
                break;
            case "clear":
                sender.sendMessage(VfxAPI.clear(p) ? "§aCleared." : "§cYour client does not draw effects.");
                return true;
            default:
                sender.sendMessage("§e/" + label + " <burst|beam|sparks|clear>");
                return true;
        }
        sender.sendMessage("§7Sent to §f" + sent + "§7 player(s).");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                      @NotNull String[] args) {
        if (args.length == 1) {
            return FUNCTIONS.stream().filter(f -> f.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return List.of();
    }
}

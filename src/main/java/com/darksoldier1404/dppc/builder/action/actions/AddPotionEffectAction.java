package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AddPotionEffectAction implements Action {
    private final String effectType;
    private final int duration;
    private final int amplifier;

    public AddPotionEffectAction(String effectType, int duration, int amplifier) {
        this.effectType = effectType.toUpperCase();
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void execute(ActionContext context) {
        PotionEffectType type = PotionEffectType.getByName(effectType);
        if (type != null && context.getPlayer().isOnline()) {
            context.getPlayer().addPotionEffect(new PotionEffect(type, duration * 20, amplifier));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ADD_POTION_EFFECT;
    }

    @Override
    public String serialize() {
        return String.format("add_potion_effect %s %d %d", effectType, duration, amplifier);
    }

    /**
     * Format: add_potion_effect <type> <duration_seconds> <amplifier>
     */
    public static AddPotionEffectAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("add_potion_effect")) return null;
        try {
            return new AddPotionEffectAction(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

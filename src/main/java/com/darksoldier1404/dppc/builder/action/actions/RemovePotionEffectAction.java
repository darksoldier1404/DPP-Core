package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.potion.PotionEffectType;

public class RemovePotionEffectAction implements Action {
    private final String effectType;

    public RemovePotionEffectAction(String effectType) {
        this.effectType = effectType.toUpperCase();
    }

    @Override
    public void execute(ActionContext context) {
        PotionEffectType type = PotionEffectType.getByName(effectType);
        if (type != null && context.getPlayer().isOnline()) {
            context.getPlayer().removePotionEffect(type);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.REMOVE_POTION_EFFECT;
    }

    @Override
    public String serialize() {
        return "remove_potion_effect " + effectType;
    }

    public static RemovePotionEffectAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("remove_potion_effect")) return null;
        return new RemovePotionEffectAction(parts[1]);
    }
}

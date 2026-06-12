package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class ClearEffectsAction implements Action {

    @Override
    public void execute(ActionContext context) {
        if (context.getPlayer().isOnline()) {
            context.getPlayer().getActivePotionEffects()
                    .forEach(e -> context.getPlayer().removePotionEffect(e.getType()));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CLEAR_EFFECTS;
    }

    @Override
    public String serialize() {
        return "clear_effects";
    }

    public static ClearEffectsAction parse(String line) {
        if (!line.trim().equalsIgnoreCase("clear_effects")) return null;
        return new ClearEffectsAction();
    }
}

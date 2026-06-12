package com.darksoldier1404.dppc.builder.action.obj;

import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ActionContext {
    private final Player player;
    private final Map<String, String> variables = new HashMap<>();
    private final Deque<Boolean> conditionStack = new ArrayDeque<>();
    private boolean cancelled = false;

    public ActionContext(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean shouldExecute() {
        if (cancelled) return false;
        for (Boolean b : conditionStack) {
            if (!b) return false;
        }
        return true;
    }

    public void pushCondition(boolean condition) {
        conditionStack.push(condition);
    }

    public void flipTopCondition() {
        if (!conditionStack.isEmpty()) {
            conditionStack.push(!conditionStack.pop());
        }
    }

    public void popCondition() {
        if (!conditionStack.isEmpty()) {
            conditionStack.pop();
        }
    }

    public int getConditionDepth() {
        return conditionStack.size();
    }

    public void setVariable(String name, String value) {
        variables.put(name, value);
    }

    public String getVariable(String name) {
        return variables.getOrDefault(name, "");
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public String applyVariables(String text) {
        if (text == null) return null;
        String result = text
                .replace("{player}", player.getName())
                .replace("{player_world}", player.getWorld().getName())
                .replace("{player_x}", String.format("%.1f", player.getLocation().getX()))
                .replace("{player_y}", String.format("%.1f", player.getLocation().getY()))
                .replace("{player_z}", String.format("%.1f", player.getLocation().getZ()))
                .replace("{player_health}", String.format("%.1f", player.getHealth()))
                .replace("{player_level}", String.valueOf(player.getLevel()))
                .replace("{player_food}", String.valueOf(player.getFoodLevel()));
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}

package com.darksoldier1404.dppc.builder.action;

import com.darksoldier1404.dppc.builder.action.actions.*;
import com.darksoldier1404.dppc.builder.action.obj.Action;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ActionBuilder {
    private final JavaPlugin plugin;
    private final List<Action> actions = new ArrayList<>();
    private String actionName;
    private boolean isEditing = false;
    private int currentEditIndex = 0;

    public ActionBuilder(JavaPlugin plugin, String actionName) {
        this.plugin = plugin;
        this.actionName = actionName;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<Action> getActions() {
        return actions;
    }

    public String getActionName() {
        return actionName;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public int getCurrentEditIndex() {
        return currentEditIndex;
    }

    public void setCurrentEditIndex(int currentEditIndex) {
        this.currentEditIndex = currentEditIndex;
    }

    private void update(Action a) {
        if (isEditing) {
            if (currentEditIndex >= actions.size()) {
                plugin.getLogger().warning("Invalid index for editing action.");
                isEditing = false;
                return;
            }
            actions.set(currentEditIndex, a);
        } else {
            actions.add(a);
        }
        isEditing = false;
    }

    public ActionBuilder playSound(String soundName) {
        update(new PlaySoundAction(soundName, 1, 1, "None", "{player}"));
        return this;
    }

    public ActionBuilder playSound(String soundName, float volume, float pitch, String worldName, Object target) {
        update(new PlaySoundAction(soundName, volume, pitch, worldName, target));
        return this;
    }

    public ActionBuilder delay(long ticks) {
        update(new DelayAction(ticks));
        return this;
    }

    public ActionBuilder executeCommandAsAdmin(String command) {
        update(new ExecuteCommandAsAdminAction(command));
        return this;
    }

    public ActionBuilder executeCommandAsPlayer(String command) {
        update(new ExecuteCommandAsPlayerAction(command));
        return this;
    }

    public ActionBuilder teleport(String worldName, double x, double y, double z) {
        update(new TeleportAction(worldName, x, y, z));
        return this;
    }

    public void execute(Player player) {
        new ActionExecutor(plugin, actions, player).start();
    }

    public ActionBuilder parseScript(String script) {
        String[] lines = script.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            Action action = parseAction(line);
            if (action != null) {
                actions.add(action);
            } else {
                plugin.getLogger().warning("Invalid action: " + line);
            }
        }
        return this;
    }

    private Action parseAction(String line) {
        Action action = PlaySoundAction.parse(line);
        if (action != null) return action;

        action = DelayAction.parse(line);
        if (action != null) return action;

        action = ExecuteCommandAsAdminAction.parse(line);
        if (action != null) return action;

        action = ExecuteCommandAsPlayerAction.parse(line);
        if (action != null) return action;

        action = TeleportAction.parse(line);
        return action;
    }

    public YamlConfiguration exportToYaml() {
        YamlConfiguration file = new YamlConfiguration();
        file.set("ACTION_NAME", actionName);
        List<String> serialized = new ArrayList<String>();
        for (Action action : actions) {
            serialized.add(action.serialize());
        }
        file.set("actions", serialized);
        return file;
    }

    public ActionBuilder importFromYaml(YamlConfiguration file) {
        actions.clear();
        actionName = file.getString("ACTION_NAME");
        List<String> serialized = file.getStringList("actions");
        for (String actionString : serialized) {
            if (actionString != null) {
                parseScript(actionString);
            }
        }
        return this;
    }

    private static class ActionExecutor {
        private final JavaPlugin plugin;
        private final List<Action> actions;
        private final Player player;
        private int currentIndex = 0;

        ActionExecutor(JavaPlugin plugin, List<Action> actions, Player player) {
            this.plugin = plugin;
            this.actions = new ArrayList<Action>(actions);
            this.player = player;
        }

        public void start() {
            scheduleNextAction(0);
        }

        private void scheduleNextAction(long delay) {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        if (currentIndex >= actions.size() || !player.isOnline()) {
                            return;
                        }
                        Action action = actions.get(currentIndex);
                        if (action instanceof DelayAction) {
                            currentIndex++;
                            scheduleNextAction(((DelayAction) action).getTicks());
                        } else {
                            action.execute(player);
                            currentIndex++;
                            scheduleNextAction(0);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().severe("Error in ActionExecutor: " + e.getMessage());
                    }
                }
            }, delay);
        }
    }
}
package com.darksoldier1404.dppc.action;

import com.darksoldier1404.dppc.action.actions.DelayAction;
import com.darksoldier1404.dppc.action.actions.ExecuteCommandAction;
import com.darksoldier1404.dppc.action.actions.PlaySoundAction;
import com.darksoldier1404.dppc.action.actions.TeleportAction;
import com.darksoldier1404.dppc.action.obj.Action;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ActionBuilder {
    private final JavaPlugin plugin;
    private final List<Action> actions = new ArrayList<>();

    public ActionBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<Action> getActions() {
        return actions;
    }

    public ActionBuilder playSound(String soundName) {
        actions.add(new PlaySoundAction(soundName, 1, 1, "None", "{player}"));
        return this;
    }

    public ActionBuilder playSound(String soundName, float volume, float pitch, String worldName, Object target) {
        actions.add(new PlaySoundAction(soundName, volume, pitch, worldName, target));
        return this;
    }

    public ActionBuilder delay(long ticks) {
        actions.add(new DelayAction(ticks));
        return this;
    }

    public ActionBuilder executeCommand(String command) {
        actions.add(new ExecuteCommandAction(command));
        return this;
    }

    public ActionBuilder teleport(String worldName, double x, double y, double z) {
        actions.add(new TeleportAction(worldName, x, y, z));
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

        action = ExecuteCommandAction.parse(line);
        if (action != null) return action;

        action = TeleportAction.parse(line);
        return action;
    }

    public YamlConfiguration exportToYaml() {
        YamlConfiguration file = new YamlConfiguration();
        List<String> serialized = new ArrayList<String>();
        for (Action action : actions) {
            serialized.add(action.serialize());
        }
        file.set("actions", serialized);
        return file;
    }

    public ActionBuilder importFromYaml(YamlConfiguration file) {
        actions.clear();
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
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
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
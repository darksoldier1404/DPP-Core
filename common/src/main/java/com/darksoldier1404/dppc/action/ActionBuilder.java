package com.darksoldier1404.dppc.action;

import com.darksoldier1404.dppc.action.actions.DelayAction;
import com.darksoldier1404.dppc.action.actions.ExecuteCommandAction;
import com.darksoldier1404.dppc.action.actions.PlaySoundAction;
import com.darksoldier1404.dppc.action.actions.TeleportAction;
import com.darksoldier1404.dppc.action.obj.Action;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.HashMap;

@SuppressWarnings("all")
public class ActionBuilder {
    private final List<Action> actions = new ArrayList<Action>();
    private final JavaPlugin plugin;

    public ActionBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
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

    public void exportToYaml(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            Map<String, List<Map<String, String>>> output = new HashMap<String, List<Map<String, String>>>();
            List<Map<String, String>> serialized = new ArrayList<Map<String, String>>();
            for (Action action : actions) {
                Map<String, String> actionMap = new HashMap<String, String>();
                actionMap.put("action", action.serialize());
                serialized.add(actionMap);
            }
            output.put("actions", serialized);
            new Yaml().dump(output, writer);
            writer.close();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to export to YAML: " + e.getMessage());
        }
    }

    public ActionBuilder importFromYaml(File file) {
        try {
            FileReader reader = new FileReader(file);
            Map<String, List<Map<String, String>>> input = new Yaml().load(reader);
            actions.clear();
            if (input != null && input.containsKey("actions")) {
                for (Map<String, String> actionMap : input.get("actions")) {
                    String actionString = actionMap.get("action");
                    if (actionString != null) {
                        parseScript(actionString);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to import from YAML: " + e.getMessage());
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
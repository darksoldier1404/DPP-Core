package com.darksoldier1404.dppc.builder.action;

import com.darksoldier1404.dppc.builder.action.actions.*;
import com.darksoldier1404.dppc.builder.action.obj.Action;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Action builder.
 */
@SuppressWarnings("all")
public class ActionBuilder {
    private final JavaPlugin plugin;
    private final List<Action> actions = new ArrayList<>();
    private String actionName;
    private boolean isEditing = false;
    private int currentEditIndex = 0;

    /**
     * Instantiates a new Action builder.
     *
     * @param plugin     the plugin
     * @param actionName the action name
     */
    public ActionBuilder(JavaPlugin plugin, String actionName) {
        this.plugin = plugin;
        this.actionName = actionName;
    }

    /**
     * Gets plugin.
     *
     * @return the plugin
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Gets actions.
     *
     * @return the actions
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Gets action name.
     *
     * @return the action name
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * Is editing boolean.
     *
     * @return the boolean
     */
    public boolean isEditing() {
        return isEditing;
    }

    /**
     * Sets editing.
     *
     * @param editing the editing
     */
    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    /**
     * Gets current edit index.
     *
     * @return the current edit index
     */
    public int getCurrentEditIndex() {
        return currentEditIndex;
    }

    /**
     * Sets current edit index.
     *
     * @param currentEditIndex the current edit index
     */
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

    /**
     * Play sound action builder.
     *
     * @param soundName the sound name
     * @return the action builder
     */
    public ActionBuilder playSound(String soundName) {
        update(new PlaySoundAction(soundName, 1, 1, "None", "{player}"));
        return this;
    }

    /**
     * Play sound action builder.
     *
     * @param soundName the sound name
     * @param volume    the volume
     * @param pitch     the pitch
     * @param worldName the world name
     * @param target    the target
     * @return the action builder
     */
    public ActionBuilder playSound(String soundName, float volume, float pitch, String worldName, Object target) {
        update(new PlaySoundAction(soundName, volume, pitch, worldName, target));
        return this;
    }

    /**
     * Delay action builder.
     *
     * @param ticks the ticks
     * @return the action builder
     */
    public ActionBuilder delay(long ticks) {
        update(new DelayAction(ticks));
        return this;
    }

    /**
     * Execute command as admin action builder.
     *
     * @param command the command
     * @return the action builder
     */
    public ActionBuilder executeCommandAsAdmin(String command) {
        update(new ExecuteCommandAsAdminAction(command));
        return this;
    }

    /**
     * Execute command as player action builder.
     *
     * @param command the command
     * @return the action builder
     */
    public ActionBuilder executeCommandAsPlayer(String command) {
        update(new ExecuteCommandAsPlayerAction(command));
        return this;
    }

    /**
     * Teleport action builder.
     *
     * @param worldName the world name
     * @param x         the x
     * @param y         the y
     * @param z         the z
     * @return the action builder
     */
    public ActionBuilder teleport(String worldName, double x, double y, double z) {
        update(new TeleportAction(worldName, x, y, z));
        return this;
    }

    /**
     * Send message action builder.
     *
     * @param message the message
     * @return the action builder
     */
    public ActionBuilder sendMessage(String message) {
        update(new SendMessageAction(message));
        return this;
    }

    /**
     * Close inventory action builder.
     *
     * @return the action builder
     */
    public ActionBuilder closeInventory() {
        update(new CloseInventoryAction());
        return this;
    }

    /**
     * Execute.
     *
     * @param player the player
     */
    public void execute(Player player) {
        new ActionExecutor(plugin, actions, player).start();
    }

    /**
     * Parse script action builder.
     *
     * @param script the script
     * @return the action builder
     */
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
        if (action != null) return action;

        action = SendMessageAction.parse(line);
        if (action != null) return action;

        action = CloseInventoryAction.parse(line);
        return action;
    }

    /**
     * Export to yaml yaml configuration.
     *
     * @return the yaml configuration
     */
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

    /**
     * Import from yaml action builder.
     *
     * @param file the file
     * @return the action builder
     */
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

        /**
         * Instantiates a new Action executor.
         *
         * @param plugin  the plugin
         * @param actions the actions
         * @param player  the player
         */
        ActionExecutor(JavaPlugin plugin, List<Action> actions, Player player) {
            this.plugin = plugin;
            this.actions = new ArrayList<Action>(actions);
            this.player = player;
        }

        /**
         * Start.
         */
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
package com.darksoldier1404.dppc.builder.action;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.builder.action.actions.*;
import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.data.DPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@DPPCoreVersion(since = "5.4.0")
@SuppressWarnings("all")
public class ActionBuilder {
    private final DPlugin plugin;
    private final List<Action> actions = new ArrayList<>();
    private String actionName;
    private boolean isEditing = false;
    private int currentEditIndex = 0;

    public ActionBuilder(DPlugin plugin, String actionName) {
        this.plugin = plugin;
        this.actionName = actionName;
    }

    public DPlugin getPlugin() {
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
            if (currentEditIndex < actions.size()) {
                actions.set(currentEditIndex, a);
            }
        } else {
            actions.add(a);
        }
        isEditing = false;
    }

    // --- Timing ---

    public ActionBuilder delay(long ticks) {
        update(new DelayAction(ticks));
        return this;
    }

    // --- Messages ---

    public ActionBuilder sendMessage(String message) {
        update(new SendMessageAction(message));
        return this;
    }

    public ActionBuilder sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        update(new SendTitleAction(title, subtitle, fadeIn, stay, fadeOut));
        return this;
    }

    public ActionBuilder sendActionBar(String message) {
        update(new SendActionBarAction(message));
        return this;
    }

    public ActionBuilder broadcast(String message) {
        update(new BroadcastAction(message));
        return this;
    }

    public ActionBuilder broadcastWorld(String message) {
        update(new BroadcastWorldAction(message));
        return this;
    }

    // --- Commands ---

    public ActionBuilder executeCommandAsAdmin(String command) {
        update(new ExecuteCommandAsAdminAction(command));
        return this;
    }

    public ActionBuilder executeCommandAsPlayer(String command) {
        update(new ExecuteCommandAsPlayerAction(command));
        return this;
    }

    // --- Player Movement ---

    public ActionBuilder teleport(String worldName, double x, double y, double z) {
        update(new TeleportAction(worldName, x, y, z));
        return this;
    }

    // --- Player State ---

    public ActionBuilder closeInventory() {
        update(new CloseInventoryAction());
        return this;
    }

    public ActionBuilder setGamemode(String gamemode) {
        update(new SetGamemodeAction(gamemode));
        return this;
    }

    public ActionBuilder giveExp(int amount) {
        update(new GiveExpAction(amount));
        return this;
    }

    public ActionBuilder takeExp(int amount) {
        update(new TakeExpAction(amount));
        return this;
    }

    public ActionBuilder setHealth(double health) {
        update(new SetHealthAction(health));
        return this;
    }

    public ActionBuilder setHunger(int foodLevel) {
        update(new SetHungerAction(foodLevel));
        return this;
    }

    public ActionBuilder kick(String reason) {
        update(new KickAction(reason));
        return this;
    }

    // --- Effects ---

    public ActionBuilder playSound(String soundName) {
        update(new PlaySoundAction(soundName, 1.0f, 1.0f));
        return this;
    }

    public ActionBuilder playSound(String soundName, float volume, float pitch) {
        update(new PlaySoundAction(soundName, volume, pitch));
        return this;
    }

    public ActionBuilder playParticle(String particleName, int count) {
        update(new PlayParticleAction(particleName, count, 0.5, 0.5, 0.5));
        return this;
    }

    public ActionBuilder addPotionEffect(String effectType, int durationSeconds, int amplifier) {
        update(new AddPotionEffectAction(effectType, durationSeconds, amplifier));
        return this;
    }

    public ActionBuilder removePotionEffect(String effectType) {
        update(new RemovePotionEffectAction(effectType));
        return this;
    }

    public ActionBuilder clearEffects() {
        update(new ClearEffectsAction());
        return this;
    }

    // --- Inventory ---

    public ActionBuilder giveItem(String material, int amount) {
        update(new GiveItemAction(material, amount));
        return this;
    }

    public ActionBuilder takeItem(String material, int amount) {
        update(new TakeItemAction(material, amount));
        return this;
    }

    // --- Variables ---

    public ActionBuilder setVariable(String name, String value) {
        update(new SetVariableAction(name, value));
        return this;
    }

    public ActionBuilder addVariable(String name, double amount) {
        update(new AddVariableAction(name, amount));
        return this;
    }

    public ActionBuilder randomNumber(String name, int min, int max) {
        update(new RandomNumberAction(name, min, max));
        return this;
    }

    // --- Conditions ---

    public ActionBuilder ifHasPermission(String permission) {
        update(new IfHasPermissionAction(permission));
        return this;
    }

    public ActionBuilder ifNotPermission(String permission) {
        update(new IfNotPermissionAction(permission));
        return this;
    }

    public ActionBuilder ifVariableEquals(String name, String value) {
        update(new IfVariableEqualsAction(name, value));
        return this;
    }

    public ActionBuilder ifVariableNotEquals(String name, String value) {
        update(new IfVariableNotEqualsAction(name, value));
        return this;
    }

    public ActionBuilder ifVariableGreater(String name, double threshold) {
        update(new IfVariableGreaterAction(name, threshold));
        return this;
    }

    public ActionBuilder ifVariableLess(String name, double threshold) {
        update(new IfVariableLessAction(name, threshold));
        return this;
    }

    public ActionBuilder orElse() {
        update(new ElseAction());
        return this;
    }

    public ActionBuilder endIf() {
        update(new EndIfAction());
        return this;
    }

    // --- Flow Control ---

    public ActionBuilder cancel() {
        update(new CancelAction());
        return this;
    }

    public ActionBuilder callAction(String actionName) {
        update(new CallActionAction(actionName));
        return this;
    }

    // --- Execution ---

    public void execute(Player player) {
        new ActionExecutor(plugin, actions, player).start();
    }

    // --- Script Parsing ---

    public ActionBuilder parseScript(String script) {
        for (String line : script.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            Action action = parseAction(line);
            if (action != null) {
                actions.add(action);
            } else {
                plugin.getLog().warning("Unknown action: " + line, true);
            }
        }
        return this;
    }

    private Action parseAction(String line) {
        Action a;

        if ((a = DelayAction.parse(line)) != null) return a;
        if ((a = SendMessageAction.parse(line)) != null) return a;
        if ((a = SendTitleAction.parse(line)) != null) return a;
        if ((a = SendActionBarAction.parse(line)) != null) return a;
        if ((a = BroadcastAction.parse(line)) != null) return a;
        if ((a = BroadcastWorldAction.parse(line)) != null) return a;
        if ((a = ExecuteCommandAsAdminAction.parse(line)) != null) return a;
        if ((a = ExecuteCommandAsPlayerAction.parse(line)) != null) return a;
        if ((a = TeleportAction.parse(line)) != null) return a;
        if ((a = CloseInventoryAction.parse(line)) != null) return a;
        if ((a = SetGamemodeAction.parse(line)) != null) return a;
        if ((a = GiveExpAction.parse(line)) != null) return a;
        if ((a = TakeExpAction.parse(line)) != null) return a;
        if ((a = SetHealthAction.parse(line)) != null) return a;
        if ((a = SetHungerAction.parse(line)) != null) return a;
        if ((a = KickAction.parse(line)) != null) return a;
        if ((a = PlaySoundAction.parse(line)) != null) return a;
        if ((a = PlayParticleAction.parse(line)) != null) return a;
        if ((a = AddPotionEffectAction.parse(line)) != null) return a;
        if ((a = RemovePotionEffectAction.parse(line)) != null) return a;
        if ((a = ClearEffectsAction.parse(line)) != null) return a;
        if ((a = GiveItemAction.parse(line)) != null) return a;
        if ((a = TakeItemAction.parse(line)) != null) return a;
        if ((a = SetVariableAction.parse(line)) != null) return a;
        if ((a = AddVariableAction.parse(line)) != null) return a;
        if ((a = RandomNumberAction.parse(line)) != null) return a;
        if ((a = IfHasPermissionAction.parse(line)) != null) return a;
        if ((a = IfNotPermissionAction.parse(line)) != null) return a;
        if ((a = IfVariableEqualsAction.parse(line)) != null) return a;
        if ((a = IfVariableNotEqualsAction.parse(line)) != null) return a;
        if ((a = IfVariableGreaterAction.parse(line)) != null) return a;
        if ((a = IfVariableLessAction.parse(line)) != null) return a;
        if ((a = ElseAction.parse(line)) != null) return a;
        if ((a = EndIfAction.parse(line)) != null) return a;
        if ((a = CancelAction.parse(line)) != null) return a;
        if ((a = CallActionAction.parse(line)) != null) return a;

        return null;
    }

    // --- Serialization ---

    public YamlConfiguration exportToYaml() {
        YamlConfiguration file = new YamlConfiguration();
        file.set("ACTION_NAME", actionName);
        List<String> serialized = new ArrayList<>();
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
        for (String line : serialized) {
            if (line != null && !line.isEmpty()) {
                Action action = parseAction(line.trim());
                if (action != null) {
                    actions.add(action);
                }
            }
        }
        return this;
    }

    // --- Inner Executor ---

    private static class ActionExecutor {
        private final DPlugin plugin;
        private final List<Action> actions;
        private final ActionContext context;
        private int currentIndex = 0;

        ActionExecutor(DPlugin plugin, List<Action> actions, Player player) {
            this.plugin = plugin;
            this.actions = new ArrayList<>(actions);
            this.context = new ActionContext(player);
        }

        public void start() {
            scheduleNextAction(0);
        }

        private void scheduleNextAction(long delay) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                try {
                    if (context.isCancelled() || currentIndex >= actions.size()) return;
                    if (!context.getPlayer().isOnline()) return;

                    Action action = actions.get(currentIndex);

                    if (action instanceof DelayAction) {
                        currentIndex++;
                        scheduleNextAction(((DelayAction) action).getTicks());
                        return;
                    }

                    if (action.isFlowControl() || context.shouldExecute()) {
                        action.execute(context);
                    }

                    currentIndex++;
                    scheduleNextAction(0);
                } catch (Exception e) {
                    plugin.getLog().severe("ActionExecutor error: " + e.getMessage(), true);
                }
            }, delay);
        }
    }
}

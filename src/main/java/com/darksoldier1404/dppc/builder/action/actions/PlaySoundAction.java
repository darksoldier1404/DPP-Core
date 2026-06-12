package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.Sound;

public class PlaySoundAction implements Action {
    private final String soundName;
    private final float volume;
    private final float pitch;

    public PlaySoundAction(String soundName, float volume, float pitch) {
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void execute(ActionContext context) {
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            if (context.getPlayer().isOnline()) {
                context.getPlayer().playSound(context.getPlayer().getLocation(), sound, volume, pitch);
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAY_SOUND;
    }

    @Override
    public String serialize() {
        return String.format("play_sound %s %.2f %.2f", soundName, volume, pitch);
    }

    public static PlaySoundAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (!parts[0].equalsIgnoreCase("play_sound")) return null;
        if (parts.length < 2) return null;
        try {
            String sound = parts[1];
            float volume = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
            float pitch = parts.length > 3 ? Float.parseFloat(parts[3]) : 1.0f;
            return new PlaySoundAction(sound, volume, pitch);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

package com.darksoldier1404.dppc.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class ColorUtils {
    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    public static String applyColor(String message){
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = message.substring(0, matcher.start());
            final String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = hexPattern.matcher(message);
        }
        message = message.replace('ㅁ', 'a');
        message = message.replace('ㅠ', 'b');
        message = message.replace('ㅊ', 'c');
        message = message.replace('ㅇ', 'd');
        message = message.replace('ㄷ', 'e');
        message = message.replace('ㄹ', 'f');
        message = message.replace('ㅏ', 'k');
        message = message.replace('ㅣ', 'l');
        message = message.replace('ㅡ', 'm');
        message = message.replace('ㅜ', 'n');
        message = message.replace('ㅐ', 'o');
        message = message.replace('ㄱ', 'r');
        message = message.replace('ㅌ', 'x');
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

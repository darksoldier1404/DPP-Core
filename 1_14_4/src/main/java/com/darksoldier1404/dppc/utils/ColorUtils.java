package com.darksoldier1404.dppc.utils;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("all")
public class ColorUtils {
    public static String applyColor(String message){
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

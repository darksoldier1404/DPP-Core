package com.darksoldier1404.dppc.api.essentials;

import com.darksoldier1404.dppc.DPPCore;
import com.earth2me.essentials.Essentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class MoneyAPI {
    private final static DPPCore plugin = DPPCore.getInstance();
    private final static Essentials ess = (Essentials) plugin.ess;

    public static boolean isEnabled() {
        return ess != null;
    }

    public static void addMoney(Player player, double amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(player).giveMoney(BigDecimal.valueOf(amount));
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void takeMoney(Player player, double amount) {
        if (!isEnabled()) return;
        ess.getUser(player).takeMoney(BigDecimal.valueOf(amount));
    }

    public static boolean hasEnoughMoney(Player player, double amount) {
        if (!isEnabled()) return false;
        return ess.getUser(player).getMoney().doubleValue() >= amount;
    }

    public static void setMoney(Player player, double amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(player).setMoney(BigDecimal.valueOf(amount));
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void transferMoney(Player player, double amount, Player target) {
        if (!isEnabled()) return;
        try {
            ess.getUser(player).takeMoney(BigDecimal.valueOf(amount));
            ess.getUser(target).giveMoney(BigDecimal.valueOf(amount));
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    // now amount is BigDecimal

    public static void addMoney(Player player, BigDecimal amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(player).giveMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void takeMoney(Player player, BigDecimal amount) {
        if (!isEnabled()) return;
        ess.getUser(player).takeMoney(amount);
    }

    public static BigDecimal getMoney(Player player) {
        if (!isEnabled()) return BigDecimal.ZERO;
        return ess.getUser(player).getMoney();
    }

    public static boolean hasEnoughMoney(Player player, BigDecimal amount) {
        if (!isEnabled()) return false;
        return ess.getUser(player).getMoney().compareTo(amount) >= 0;
    }

    public static void setMoney(Player player, BigDecimal amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(player).setMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void transferMoney(Player player, BigDecimal amount, Player target) {
        if (!isEnabled()) return;
        try {
            ess.getUser(player).takeMoney(amount);
            ess.getUser(target).giveMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }
}
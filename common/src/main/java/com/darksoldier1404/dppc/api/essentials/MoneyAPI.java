package com.darksoldier1404.dppc.api.essentials;

import com.darksoldier1404.dppc.DPPCore;
import com.earth2me.essentials.Essentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class MoneyAPI {
    private final static DPPCore plugin = DPPCore.getInstance();
    private final static Essentials ess = plugin.ess;

    public static boolean isEnabled() {
        return ess != null;
    }

    public static void addMoney(Player p, double amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(p).giveMoney(BigDecimal.valueOf(amount));
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void takeMoney(Player p, double amount) {
        if (!isEnabled()) return;
        ess.getUser(p).takeMoney(BigDecimal.valueOf(amount));
    }

    public static boolean hasEnoughMoney(Player p, double amount) {
        if (!isEnabled()) return false;
        return ess.getUser(p).getMoney().doubleValue() >= amount;
    }

    public static void setMoney(Player p, double amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(p).setMoney(BigDecimal.valueOf(amount));
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void transferMoney(Player p, double amount, Player target) {
        if (!isEnabled()) return;
        try {
            ess.getUser(p).takeMoney(BigDecimal.valueOf(amount));
            ess.getUser(target).giveMoney(BigDecimal.valueOf(amount));
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    // now amount is BigDecimal

    public static void addMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(p).giveMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void takeMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return;
        ess.getUser(p).takeMoney(amount);
    }

    public static BigDecimal getMoney(Player p) {
        if (!isEnabled()) return BigDecimal.ZERO;
        return ess.getUser(p).getMoney();
    }

    public static boolean hasEnoughMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return false;
        return ess.getUser(p).getMoney().compareTo(amount) >= 0;
    }

    public static void setMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return;
        try {
            ess.getUser(p).setMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    public static void transferMoney(Player p, BigDecimal amount, Player target) {
        if (!isEnabled()) return;
        try {
            ess.getUser(p).takeMoney(amount);
            ess.getUser(target).giveMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }
}
package com.darksoldier1404.dppc.api.essentials;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.api.essentials.economy.EconomyProvider;
import com.darksoldier1404.dppc.api.essentials.economy.EssentialsProvider;
import com.darksoldier1404.dppc.api.essentials.economy.NullProvider;
import com.darksoldier1404.dppc.api.essentials.economy.VaultProvider;
import com.darksoldier1404.dppc.api.logger.DLogManager;
import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;

@SuppressWarnings("all")
public class MoneyAPI {
    private final static DPPCore plugin = DPPCore.getInstance();
    private static EconomyProvider provider = new NullProvider();

    /**
     * Resolves the economy backend based on {@code Settings.economy-provider}.
     * <p>
     * Values: {@code AUTO} (EssentialsX first, otherwise Vault), {@code EssentialsX},
     * {@code Vault}. A forced provider that is unavailable falls back to the other.
     * Must be called after soft-depend plugins have been initialised.
     */
    public static void init() {
        String setting = "AUTO";
        if (plugin != null && plugin.config != null) {
            setting = plugin.config.getString("Settings.economy-provider", "AUTO");
        }

        EconomyProvider ess = resolveEssentials();
        EconomyProvider vault = resolveVault();

        if (setting.equalsIgnoreCase("EssentialsX")) {
            provider = pickForced(ess, vault, "EssentialsX");
        } else if (setting.equalsIgnoreCase("Vault")) {
            provider = pickForced(vault, ess, "Vault");
        } else {
            if (!setting.equalsIgnoreCase("AUTO")) {
                warn("Unknown economy-provider '" + setting + "'. Falling back to AUTO.");
            }
            provider = (ess != null) ? ess : (vault != null ? vault : new NullProvider());
        }

        if (provider.isEnabled()) {
            info("MoneyAPI economy provider: " + provider.getName());
        } else {
            warn("No economy provider available. MoneyAPI is disabled.");
        }
    }

    private static EconomyProvider pickForced(EconomyProvider preferred, EconomyProvider fallback, String preferredName) {
        if (preferred != null) {
            return preferred;
        }
        if (fallback != null) {
            warn(preferredName + " was selected but is not available. Falling back to " + fallback.getName() + ".");
            return fallback;
        }
        warn(preferredName + " was selected but no economy provider is available. MoneyAPI is disabled.");
        return new NullProvider();
    }

    private static EconomyProvider resolveEssentials() {
        if (DPPCore.ess == null) {
            return null;
        }
        return new EssentialsProvider((Essentials) DPPCore.ess);
    }

    private static EconomyProvider resolveVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        Economy econ = rsp.getProvider();
        if (econ == null) {
            return null;
        }
        return new VaultProvider(econ);
    }

    private static void info(String message) {
        if (plugin != null) {
            plugin.getLog().info(message, DLogManager.printPluginUtilsLogs);
        }
    }

    private static void warn(String message) {
        if (plugin != null) {
            plugin.getLog().warning(message, DLogManager.printPluginUtilsLogs);
        }
    }

    public static boolean isEnabled() {
        return provider.isEnabled();
    }

    public static void addMoney(Player p, double amount) {
        if (!isEnabled()) return;
        provider.add(p, BigDecimal.valueOf(amount));
    }

    public static void takeMoney(Player p, double amount) {
        if (!isEnabled()) return;
        provider.take(p, BigDecimal.valueOf(amount));
    }

    public static boolean hasEnoughMoney(Player p, double amount) {
        if (!isEnabled()) return false;
        return provider.has(p, BigDecimal.valueOf(amount));
    }

    public static void setMoney(Player p, double amount) {
        if (!isEnabled()) return;
        provider.set(p, BigDecimal.valueOf(amount));
    }

    public static void transferMoney(Player p, double amount, Player target) {
        if (!isEnabled()) return;
        BigDecimal value = BigDecimal.valueOf(amount);
        provider.take(p, value);
        provider.add(target, value);
    }

    public static void addMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return;
        provider.add(p, amount);
    }

    public static void takeMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return;
        provider.take(p, amount);
    }

    public static BigDecimal getMoney(Player p) {
        if (!isEnabled()) return BigDecimal.ZERO;
        return provider.getMoney(p);
    }

    public static boolean hasEnoughMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return false;
        return provider.has(p, amount);
    }

    public static void setMoney(Player p, BigDecimal amount) {
        if (!isEnabled()) return;
        provider.set(p, amount);
    }

    public static void transferMoney(Player p, BigDecimal amount, Player target) {
        if (!isEnabled()) return;
        provider.take(p, amount);
        provider.add(target, amount);
    }
}

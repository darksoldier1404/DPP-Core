package com.darksoldier1404.dppc.api.essentials.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * Vault backed economy provider. Works with any economy plugin that registers a
 * Vault {@link Economy} service (EssentialsX, CMI, ...).
 * <p>
 * Note: Vault's API is {@code double} based, so values are converted from
 * {@link BigDecimal}; very large or high precision amounts may lose precision.
 * Vault has no native {@code setMoney}, so {@link #set} is emulated by depositing
 * or withdrawing the difference from the current balance.
 */
public class VaultProvider implements EconomyProvider {
    private final Economy econ;

    public VaultProvider(Economy econ) {
        this.econ = econ;
    }

    @Override
    public String getName() {
        return "Vault";
    }

    @Override
    public boolean isEnabled() {
        return econ != null && econ.isEnabled();
    }

    @Override
    public BigDecimal getMoney(Player p) {
        return BigDecimal.valueOf(econ.getBalance(p));
    }

    @Override
    public boolean has(Player p, BigDecimal amount) {
        return econ.has(p, amount.doubleValue());
    }

    @Override
    public void add(Player p, BigDecimal amount) {
        econ.depositPlayer(p, amount.doubleValue());
    }

    @Override
    public void take(Player p, BigDecimal amount) {
        econ.withdrawPlayer(p, amount.doubleValue());
    }

    @Override
    public void set(Player p, BigDecimal amount) {
        double current = econ.getBalance(p);
        double target = amount.doubleValue();
        if (target > current) {
            econ.depositPlayer(p, target - current);
        } else if (target < current) {
            econ.withdrawPlayer(p, current - target);
        }
    }
}

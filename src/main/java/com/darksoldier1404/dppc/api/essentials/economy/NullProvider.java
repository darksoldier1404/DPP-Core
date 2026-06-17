package com.darksoldier1404.dppc.api.essentials.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * No-op provider used when no economy backend is available. Mirrors the original
 * MoneyAPI behaviour of silently doing nothing when Essentials was missing.
 */
public class NullProvider implements EconomyProvider {

    @Override
    public String getName() {
        return "None";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public BigDecimal getMoney(Player p) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean has(Player p, BigDecimal amount) {
        return false;
    }

    @Override
    public void add(Player p, BigDecimal amount) {
    }

    @Override
    public void take(Player p, BigDecimal amount) {
    }

    @Override
    public void set(Player p, BigDecimal amount) {
    }
}

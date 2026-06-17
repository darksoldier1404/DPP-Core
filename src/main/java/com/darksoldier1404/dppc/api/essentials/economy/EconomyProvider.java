package com.darksoldier1404.dppc.api.essentials.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * Backend abstraction used by {@link com.darksoldier1404.dppc.api.essentials.MoneyAPI}.
 * <p>
 * Implementations wrap a concrete economy backend (EssentialsX, Vault, ...) so that
 * MoneyAPI's public methods can stay unchanged while the underlying provider varies.
 */
public interface EconomyProvider {

    /**
     * @return human readable backend name (e.g. {@code "EssentialsX"}, {@code "Vault"}).
     */
    String getName();

    /**
     * @return {@code true} if this provider is usable.
     */
    boolean isEnabled();

    BigDecimal getMoney(Player p);

    boolean has(Player p, BigDecimal amount);

    void add(Player p, BigDecimal amount);

    void take(Player p, BigDecimal amount);

    void set(Player p, BigDecimal amount);
}

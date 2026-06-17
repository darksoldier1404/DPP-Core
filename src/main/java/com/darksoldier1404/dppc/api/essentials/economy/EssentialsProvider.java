package com.darksoldier1404.dppc.api.essentials.economy;

import com.earth2me.essentials.Essentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * EssentialsX backed economy provider. Uses {@link BigDecimal} natively, so no
 * precision is lost compared to the original MoneyAPI implementation.
 */
public class EssentialsProvider implements EconomyProvider {
    private final Essentials ess;

    public EssentialsProvider(Essentials ess) {
        this.ess = ess;
    }

    @Override
    public String getName() {
        return "Essentials";
    }

    @Override
    public boolean isEnabled() {
        return ess != null;
    }

    @Override
    public BigDecimal getMoney(Player p) {
        return ess.getUser(p).getMoney();
    }

    @Override
    public boolean has(Player p, BigDecimal amount) {
        return ess.getUser(p).getMoney().compareTo(amount) >= 0;
    }

    @Override
    public void add(Player p, BigDecimal amount) {
        try {
            ess.getUser(p).giveMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void take(Player p, BigDecimal amount) {
        ess.getUser(p).takeMoney(amount);
    }

    @Override
    public void set(Player p, BigDecimal amount) {
        try {
            ess.getUser(p).setMoney(amount);
        } catch (MaxMoneyException e) {
            e.printStackTrace();
        }
    }
}

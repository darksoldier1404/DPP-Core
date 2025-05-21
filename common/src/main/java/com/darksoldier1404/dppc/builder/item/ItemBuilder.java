package com.darksoldier1404.dppc.builder.item;

import com.sk89q.worldedit.antlr4.runtime.misc.MultiMap;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta im;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.im = item.getItemMeta();
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder addLore(String line) {
        List<String> lore = (im.getLore() == null ? new ArrayList<>() : im.getLore());
        lore.add(line);
        im.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        im.setLore(lore);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        im.setDisplayName(name);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment e, int level, boolean ignoreRestrictions) {
        im.addEnchant(e, level, ignoreRestrictions);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        im.addItemFlags(flag);
        return this;
    }

    public ItemBuilder setItemName(String name){
        im.setItemName(name);
        return this;
    }

    public ItemBuilder setEnchantable(int enchantable){
        im.setEnchantable(enchantable);
        return this;
    }

    public ItemBuilder setEnchantmentGlintOverride(Boolean glint){
        im.setEnchantmentGlintOverride(glint);
        return this;
    }

    public ItemBuilder setUnbreakable(Boolean unbreakable){
        im.setUnbreakable(unbreakable);
        return this;
    }
    public ItemBuilder setRepairCost(int cost){
        if (im instanceof Repairable){
            Repairable repairMeta = (Repairable) im;
            repairMeta.setRepairCost(cost);
            item.setItemMeta(repairMeta);
        }
        return this;
    }
    public ItemBuilder setBlockData(BlockData blockData){
        if (im instanceof BlockDataMeta){
            BlockDataMeta blockDataMeta = (BlockDataMeta) im;
            blockDataMeta.setBlockData(blockData);
            item.setItemMeta(blockDataMeta);
        }
        return this;
    }
    public ItemBuilder setDamage(int damage){
        if (im instanceof Damageable){
            Damageable damageMeta = (Damageable) im;
            damageMeta.setDamage(damage);
            item.setItemMeta(damageMeta);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(im);
        return item;
    }
}

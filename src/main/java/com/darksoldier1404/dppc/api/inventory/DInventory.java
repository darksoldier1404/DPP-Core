package com.darksoldier1404.dppc.api.inventory;


import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryCustom;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class DInventory extends CraftInventoryCustom {
    private final String handlerName;
    private final UUID uuid;
    private boolean usePage;
    private int pages = 0;
    private int currentPage = 0;
    private ItemStack[] pageTools = new ItemStack[8];
    private Map<Integer, ItemStack[]> pageItems = new HashMap<>();

    public DInventory(InventoryHolder holder, String title, int size, JavaPlugin plugin) {
        super(holder, size, title);
        usePage = false;
        handlerName = plugin.getName();
        uuid = UUID.randomUUID();
    }

    public DInventory(InventoryHolder holder, String title, int size, boolean usePage, JavaPlugin plugin) {
        super(holder, size, title);
        this.handlerName = plugin.getName();
        this.usePage = usePage;
        uuid = UUID.randomUUID();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public boolean isValidHandler(JavaPlugin plugin) {
        return plugin.getName().equals(handlerName);
    }

    public boolean isUsePage() {
        return usePage;
    }

    public int getPages() {
        return pages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public ItemStack[] getPageTools() {
        return pageTools;
    }

    public Map<Integer, ItemStack[]> getPageItems() {
        return pageItems;
    }

    public void setUsePage(boolean usePage) {
        this.usePage = usePage;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageTools(ItemStack[] pageTools) {
        this.pageTools = pageTools;
    }

    public void setPageTool(int index, ItemStack item) {
        pageTools[index] = item;
    }

    public boolean setPageItems(Map<Integer, ItemStack[]> pageItems) {
        this.pageItems = pageItems;
        return true;
    }

    public boolean setPageItem(int slot, ItemStack item) {
        if (slot < 0 || slot > 44) return false;
        pageItems.get(currentPage)[slot] = item;
        return true;
    }

    public boolean setPageContent(int page, ItemStack[] items) {
        if (page < 0 || page > pages) return false;
        pageItems.put(page, items);
        return true;
    }

    // add pageContent
    public void addPageContent(ItemStack[] items) {
        pageItems.put(pages, items);
        pages++;
    }

    public void update() {
        clear();
        for (int i = 0; i < pageItems.get(currentPage).length; i++) {
            setItem(i, pageItems.get(currentPage)[i]);
        }
        int pt = 0;
        for (int i = getSize() - 9; i < getSize(); i++) {
            setItem(i, NBT.setStringTag(pageTools[pt], "pageTools", "true"));
            pt++;
        }
    }

    public boolean nextPage() {
        if (!usePage) return false;
        if (currentPage >= pages) return false;
        currentPage++;
        update();
        return true;
    }

    public boolean prevPage() {
        if (!usePage) return false;
        if (currentPage <= 0) return false;
        currentPage--;
        update();
        return true;
    }

    public boolean turnPage(int page) {
        if (!usePage) return false;
        if (page < 0 || page > pages) return false;
        currentPage = page;
        update();
        return true;
    }
}

package com.darksoldier1404.dppc.api.inventory;

import com.darksoldier1404.dppc.utils.DInventoryManager;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class DInventory implements InventoryHolder, Cloneable {
    private Inventory inventory;
    private String handlerName;
    private final String title;
    private JavaPlugin plugin;
    private String name;
    private UUID uuid;
    private boolean usePage;
    private boolean usePageTools;
    private boolean useDefaultPageTools;
    private int pages = 0;
    private int currentPage = 0;
    private ItemStack[] pageTools = new ItemStack[9];
    private Map<Integer, ItemStack[]> pageItems = new HashMap<>();
    private Object obj;
    private int channel;

    public DInventory(String title, int size, JavaPlugin plugin) {
        this.title = title;
        this.inventory = Bukkit.createInventory(this, size, title);
        usePage = false;
        handlerName = plugin.getName();
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

    public DInventory(String title, int size, boolean usePage, JavaPlugin plugin) {
        this.title = title;
        this.inventory = Bukkit.createInventory(this, size, title);
        this.handlerName = plugin.getName();
        this.usePage = usePage;
        usePageTools = true;
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

    public DInventory(String title, int size, boolean usePage, boolean useDefaultPageTools, JavaPlugin plugin) {
        this.title = title;
        this.inventory = Bukkit.createInventory(this, size, title);
        this.handlerName = plugin.getName();
        this.usePage = usePage;
        usePageTools = true;
        this.useDefaultPageTools = useDefaultPageTools;
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

    public void updateTitle(String title) { // use only not opened inventory
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.inventory = Bukkit.createInventory(this, inventory.getSize(), title);
        update();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    public String getHandlerName() {
        return handlerName;
    }

    public boolean isValidHandler(JavaPlugin plugin) {
        return plugin.getName().equals(handlerName);
    }

    public boolean isUsePage() {
        return usePage;
    }

    public boolean isUsePageTools() {
        return usePageTools;
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
        usePageTools = true;
    }

    public void setUsePageTools(boolean usePageTools) {
        this.usePageTools = usePageTools;
    }

    public boolean isUseDefaultPageTools() {
        return useDefaultPageTools;
    }

    public void setUseDefaultPageTools(boolean useDefaultPageTools) {
        this.useDefaultPageTools = useDefaultPageTools;
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

    public boolean setPageItem(int page, int slot, ItemStack item) {
        if (page < 0 || page > pages) return false;
        if (slot < 0 || slot > 44) return false;
        pageItems.get(page)[slot] = item;
        return true;
    }

    public boolean setPageContent(int page, ItemStack[] items) {
        if (page < 0 || page > pages) return false;
        pageItems.put(page, items);
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPageContent(ItemStack[] items) {
        pageItems.put(pages, items);
        pages++;
    }

    public void updatePageTools() {
        if (useDefaultPageTools) {
            applyDefaultPageTools();
        }
        int pt = 0;
        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
            if (pageTools[pt] != null) {
                NBT.setStringTag(pageTools[pt], "pageTools", "true");
                inventory.setItem(i, pageTools[pt]);
            }
            pt++;
        }
    }

    public void applyChanges() {
        ItemStack[] contents = new ItemStack[inventory.getSize()];
        for (int i = 0; i < inventory.getSize(); i++) {
            contents[i] = inventory.getItem(i);
        }
        pageItems.put(currentPage, contents);
    }

    public void update() {
        inventory.clear();
        if (pageItems.get(currentPage) != null) {
            for (int i = 0; i < pageItems.get(currentPage).length; i++) {
                if (pageItems.get(currentPage)[i] != null) {
                    inventory.setItem(i, pageItems.get(currentPage)[i]);
                }
            }
        }
        if (usePageTools) {
            updatePageTools();
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

    public void openInventory(Player p) {
        p.openInventory(inventory);
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isValidChannel(int channel) {
        return this.channel == channel;
    }

    public Map<Integer, ItemStack[]> getPageItemsWithoutTools() {
        if (pageItems.isEmpty()) return null;
        Map<Integer, ItemStack[]> resultItems = new HashMap<>();
        int contentSize = inventory.getSize() - 9;
        for (Map.Entry<Integer, ItemStack[]> entry : pageItems.entrySet()) {
            ItemStack[] items = new ItemStack[contentSize];
            ItemStack[] pageContent = entry.getValue();
            for (int i = 0; i < contentSize && i < pageContent.length; i++) {
                items[i] = pageContent[i];
            }
            resultItems.put(entry.getKey(), items);
        }
        return resultItems;
    }

    public void applyDefaultPageTools() {
        if (useDefaultPageTools) {
            ItemStack[] defaultPageTools = new ItemStack[9];
            ItemStack pane = NBT.setStringTag(new ItemStack(org.bukkit.Material.BLACK_STAINED_GLASS_PANE), "dppc_clickcancel", "true");
            ItemMeta meta = pane.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
            ItemStack nextPage = NBT.setStringTag(new ItemStack(org.bukkit.Material.ARROW), "dppc_clickcancel", "true");
            nextPage = NBT.setStringTag(nextPage, "dppc_nextpage", "true");
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§aNext Page");
            nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            nextPage.setItemMeta(nextMeta);
            ItemStack prevPage = NBT.setStringTag(new ItemStack(org.bukkit.Material.ARROW), "dppc_clickcancel", "true");
            prevPage = NBT.setStringTag(prevPage, "dppc_prevpage", "true");
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§aPrevious Page");
            prevMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            prevPage.setItemMeta(prevMeta);
            ItemStack currentPageItem = NBT.setStringTag(new ItemStack(org.bukkit.Material.PAPER), "dppc_clickcancel", "true");
            currentPageItem = NBT.setStringTag(currentPageItem, "dppc_currentpage", "true");
            ItemMeta currentMeta = currentPageItem.getItemMeta();
            currentMeta.setDisplayName("§aCurrent Page: <dppc_currentpage> / <dppc_maxpage>");
            currentMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            currentPageItem.setItemMeta(currentMeta);
            defaultPageTools[0] = pane; // Slot 0
            defaultPageTools[1] = prevPage; // Slot 1
            defaultPageTools[2] = pane; // Slot 2
            defaultPageTools[3] = pane; // Slot 3
            defaultPageTools[4] = currentPageItem; // Slot 4
            defaultPageTools[5] = pane; // Slot 5
            defaultPageTools[6] = pane; // Slot 6
            defaultPageTools[7] = nextPage; // Slot 7
            defaultPageTools[8] = pane; // Slot 8
            this.pageTools = defaultPageTools;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DInventory that = (DInventory) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public DInventory deserialize(YamlConfiguration data) {
        update();
        if (!data.contains("DInventory")) return this;
        this.handlerName = data.getString("DInventory.handlerName");
        this.uuid = UUID.fromString(data.getString("DInventory.uuid"));
        this.usePage = data.getBoolean("DInventory.usePage");
        this.usePageTools = data.getBoolean("DInventory.usePageTools");
        this.pages = data.getInt("DInventory.pages");
        this.currentPage = data.getInt("DInventory.currentPage");
        this.pageTools = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            String itemPath = "DInventory.pageTools." + i;
            if (data.contains(itemPath)) {
                pageTools[i] = data.getItemStack(itemPath);
            } else {
                pageTools[i] = null;
            }
        }
        this.pageItems = new HashMap<>();
        if (data.contains("DInventory.pageItems")) {
            for (String page : data.getConfigurationSection("DInventory.pageItems").getKeys(false)) {
                for (String itemPath : data.getConfigurationSection("DInventory.pageItems." + page).getKeys(false)) {
                    if (!data.isItemStack("DInventory.pageItems." + page + "." + itemPath)) {
                        continue;
                    }
                    ItemStack item = data.getItemStack("DInventory.pageItems." + page + "." + itemPath);
                    int slot = Integer.parseInt(itemPath);
                    if (slot < 0 || slot > 44) continue;
                    if (!pageItems.containsKey(Integer.parseInt(page))) {
                        pageItems.put(Integer.parseInt(page), new ItemStack[45]);
                    }
                    pageItems.get(Integer.parseInt(page))[slot] = item;
                }
            }
        }
        if (data.contains("obj")) {
            this.obj = decodeObjectFromBase64(data.getString("DInventory.obj"));
        } else {
            this.obj = null;
        }
        if (data.contains("DInventory.channel")) {
            this.channel = data.getInt("DInventory.channel");
        } else {
            this.channel = 0;
        }
        return this;
    }

    public YamlConfiguration serialize(YamlConfiguration data) {
        update();
        data.set("DInventory.handlerName", handlerName);
        data.set("DInventory.uuid", uuid.toString());
        data.set("DInventory.usePage", usePage);
        data.set("DInventory.usePageTools", usePageTools);
        data.set("DInventory.pages", pages);
        data.set("DInventory.currentPage", currentPage);
        for (int i = 0; i < pageTools.length; i++) {
            if (pageTools[i] != null) {
                data.set("DInventory.pageTools." + i, pageTools[i]);
            }
        }
        for (Map.Entry<Integer, ItemStack[]> entry : pageItems.entrySet()) {
            int page = entry.getKey();
            ItemStack[] items = entry.getValue();
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    data.set("DInventory.pageItems." + page + "." + i, items[i]);
                }
            }
        }
        if (obj != null) {
            data.set("DInventory.obj", encodeObjectToBase64(obj));
        }
        data.set("DInventory.channel", channel);
        return data;
    }

    public static String encodeObjectToBase64(Object input) {
        if (input == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(input);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public static Object decodeObjectFromBase64(String base64Input) {
        if (base64Input == null) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Input);
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public int getSize() {
        return inventory.getSize();
    }

    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    public void setMaxStackSize(int size) {
        inventory.setMaxStackSize(size);
    }

    public ItemStack getItem(int index) {
        return inventory.getItem(index);
    }

    public void setItem(int index, ItemStack item) {
        inventory.setItem(index, item);
    }

    public java.util.HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        return inventory.addItem(items);
    }

    public java.util.HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        return inventory.removeItem(items);
    }

    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    public void setContents(ItemStack[] items) {
        inventory.setContents(items);
    }

    public ItemStack[] getStorageContents() {
        return inventory.getStorageContents();
    }

    public void setStorageContents(ItemStack[] items) {
        inventory.setStorageContents(items);
    }

    public boolean contains(org.bukkit.Material material) {
        return inventory.contains(material);
    }

    public boolean contains(ItemStack item) {
        return inventory.contains(item);
    }

    public boolean contains(org.bukkit.Material material, int amount) {
        return inventory.contains(material, amount);
    }

    public boolean contains(ItemStack item, int amount) {
        return inventory.contains(item, amount);
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        return inventory.containsAtLeast(item, amount);
    }

    public java.util.HashMap<Integer, ? extends ItemStack> all(org.bukkit.Material material) {
        return inventory.all(material);
    }

    public java.util.HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return inventory.all(item);
    }

    public int first(org.bukkit.Material material) {
        return inventory.first(material);
    }

    public int first(ItemStack item) {
        return inventory.first(item);
    }

    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    public void remove(org.bukkit.Material material) {
        inventory.remove(material);
    }

    public void remove(ItemStack item) {
        inventory.remove(item);
    }

    public void clear(int index) {
        inventory.clear(index);
    }

    public void clear() {
        inventory.clear();
    }

    public java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    public org.bukkit.event.inventory.InventoryType getType() {
        return inventory.getType();
    }

    public InventoryHolder getHolder() {
        return inventory.getHolder();
    }

    public java.util.ListIterator<ItemStack> iterator() {
        return inventory.iterator();
    }

    public java.util.ListIterator<ItemStack> iterator(int index) {
        return inventory.iterator(index);
    }

    public org.bukkit.Location getLocation() {
        return inventory.getLocation();
    }

    public static class PageItemSet {
        private final int page;
        private final int slot;
        private final ItemStack item;

        public PageItemSet(int page, int slot, ItemStack item) {
            this.page = page;
            this.slot = slot;
            this.item = item;
        }

        public int getPage() {
            return page;
        }

        public int getSlot() {
            return slot;
        }

        public ItemStack getItem() {
            return item;
        }
    }

    public void applyAllItemChanges(Consumer<PageItemSet> consumer) {
        if (pageItems.isEmpty()) return;
        for (Map.Entry<Integer, ItemStack[]> entry : pageItems.entrySet()) {
            int page = entry.getKey();
            ItemStack[] items = entry.getValue();
            for (int slot = 0; slot < items.length; slot++) {
                if (items[slot] != null) {
                    consumer.accept(new PageItemSet(page, slot, items[slot]));
                }
            }
        }
    }

    @Override
    public DInventory clone() {
        try {
            DInventory clone = (DInventory) super.clone();
            clone.inventory = Bukkit.createInventory(clone, inventory.getSize(), title);
            clone.handlerName = handlerName;
            clone.name = name;
            clone.plugin = plugin;
            clone.uuid = UUID.randomUUID();
            clone.usePage = usePage;
            clone.usePageTools = usePageTools;
            clone.useDefaultPageTools = useDefaultPageTools;
            clone.pages = pages;
            clone.currentPage = currentPage;
            clone.pageTools = new ItemStack[9];
            for (int i = 0; i < pageTools.length; i++) {
                if (pageTools[i] != null) {
                    clone.pageTools[i] = pageTools[i].clone();
                } else {
                    clone.pageTools[i] = null;
                }
            }
            clone.pageItems = new HashMap<>();
            for (Map.Entry<Integer, ItemStack[]> entry : pageItems.entrySet()) {
                int page = entry.getKey();
                ItemStack[] items = new ItemStack[54];
                for (int j = 0; j < entry.getValue().length; j++) {
                    if (entry.getValue()[j] != null) {
                        items[j] = entry.getValue()[j].clone();
                    }
                }
                clone.pageItems.put(page, items);
            }
            clone.obj = obj;
            clone.channel = channel;
            ItemStack[] contents = inventory.getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    clone.inventory.setItem(i, contents[i].clone());
                } else {
                    clone.inventory.setItem(i, null);
                }
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
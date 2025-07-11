package com.darksoldier1404.dppc.api.inventory;

import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.DInventoryManager;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The type DInventory.
 */
@SuppressWarnings("unused")
public class DInventory implements InventoryHolder {
    private Inventory inventory;
    private String handlerName;
    private JavaPlugin plugin;
    private String name;
    private UUID uuid;
    private boolean usePage;
    private boolean usePageTools;
    private int pages = 0;
    private int currentPage = 0;
    private ItemStack[] pageTools = new ItemStack[9];
    private Map<Integer, ItemStack[]> pageItems = new HashMap<>();
    private Object obj;
    private int channel;
    private YamlConfiguration data;

    /**
     * Instantiates a new DInventory.
     *
     * @param title  the title
     * @param size   the size
     * @param plugin the plugin
     */
    public DInventory(String title, int size, JavaPlugin plugin) {
        this.inventory = Bukkit.createInventory(this, size, title);
        usePage = false;
        handlerName = plugin.getName();
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

    /**
     * Instantiates a new DInventory.
     *
     * @param title   the title
     * @param size    the size
     * @param usePage the use page
     * @param plugin  the plugin
     */
    public DInventory(String title, int size, boolean usePage, JavaPlugin plugin) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.handlerName = plugin.getName();
        this.usePage = usePage;
        usePageTools = true;
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets obj.
     *
     * @return the obj
     */
    public Object getObj() {
        return obj;
    }

    /**
     * Sets obj.
     *
     * @param obj the obj
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }

    /**
     * Gets unique id.
     *
     * @return the unique id
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Gets handler name.
     *
     * @return the handler name
     */
    @NotNull
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * Is valid handler boolean.
     *
     * @param plugin the plugin
     * @return the boolean
     */
    public boolean isValidHandler(JavaPlugin plugin) {
        return plugin.getName().equals(handlerName);
    }

    /**
     * Is use page boolean.
     *
     * @return the boolean
     */
    public boolean isUsePage() {
        return usePage;
    }

    /**
     * Is use page tools boolean.
     *
     * @return the boolean
     */
    public boolean isUsePageTools() {
        return usePageTools;
    }

    /**
     * Gets pages.
     *
     * @return the pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * Gets current page.
     *
     * @return the current page
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get page tools item stack [ ].
     *
     * @return the item stack [ ]
     */
    public ItemStack[] getPageTools() {
        return pageTools;
    }

    /**
     * Gets page items.
     *
     * @return the page items
     */
    public Map<Integer, ItemStack[]> getPageItems() {
        return pageItems;
    }

    /**
     * Sets use page.
     *
     * @param usePage the use page
     */
    public void setUsePage(boolean usePage) {
        this.usePage = usePage;
        usePageTools = true;
    }

    /**
     * Sets use page tools.
     *
     * @param usePageTools the use page tools
     */
    public void setUsePageTools(boolean usePageTools) {
        this.usePageTools = usePageTools;
    }

    /**
     * Sets pages.
     *
     * @param pages the pages
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * Sets current page.
     *
     * @param currentPage the current page
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Sets page tools.
     *
     * @param pageTools the page tools
     */
    public void setPageTools(ItemStack[] pageTools) {
        this.pageTools = pageTools;
    }

    /**
     * Sets page tool.
     *
     * @param index the index
     * @param item  the item
     */
    public void setPageTool(int index, ItemStack item) {
        pageTools[index] = item;
    }

    /**
     * Sets page items.
     *
     * @param pageItems the page items
     * @return the page items
     */
    public boolean setPageItems(Map<Integer, ItemStack[]> pageItems) {
        this.pageItems = pageItems;
        return true;
    }

    /**
     * Sets page item.
     *
     * @param slot the slot
     * @param item the item
     * @return the page item
     */
    public boolean setPageItem(int slot, ItemStack item) {
        if (slot < 0 || slot > 44) return false;
        pageItems.get(currentPage)[slot] = item;
        return true;
    }

    /**
     * Sets page item.
     *
     * @param page the page
     * @param slot the slot
     * @param item the item
     * @return the page item
     */
    public boolean setPageItem(int page, int slot, ItemStack item) {
        if (page < 0 || page > pages) return false;
        if (slot < 0 || slot > 44) return false;
        pageItems.get(page)[slot] = item;
        return true;
    }

    /**
     * Sets page content.
     *
     * @param page  the page
     * @param items the items
     * @return the page content
     */
    public boolean setPageContent(int page, ItemStack[] items) {
        if (page < 0 || page > pages) return false;
        pageItems.put(page, items);
        return true;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add page content.
     *
     * @param items the items
     */
    public void addPageContent(ItemStack[] items) {
        pageItems.put(pages, items);
        pages++;
    }

    /**
     * Update page tools.
     */
    public void updatePageTools() {
        int pt = 0;
        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
            if (pageTools[pt] != null) {
                NBT.setStringTag(pageTools[pt], "pageTools", "true");
                inventory.setItem(i, pageTools[pt]);
            }
            pt++;
        }
    }

    /**
     * Update.
     */
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

    /**
     * Next page boolean.
     *
     * @return the boolean
     */
    public boolean nextPage() {
        if (!usePage) return false;
        if (currentPage >= pages) return false;
        currentPage++;
        update();
        return true;
    }

    /**
     * Prev page boolean.
     *
     * @return the boolean
     */
    public boolean prevPage() {
        if (!usePage) return false;
        if (currentPage <= 0) return false;
        currentPage--;
        update();
        return true;
    }

    /**
     * Turn page boolean.
     *
     * @param page the page
     * @return the boolean
     */
    public boolean turnPage(int page) {
        if (!usePage) return false;
        if (page < 0 || page > pages) return false;
        currentPage = page;
        update();
        return true;
    }

    /**
     * Open inventory.
     *
     * @param p the p
     */
    public void openInventory(Player p) {
        p.openInventory(inventory);
    }

    /**
     * Gets channel.
     *
     * @return the channel
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Sets channel.
     *
     * @param channel the channel
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * Is valid channel boolean.
     *
     * @param channel the channel
     * @return the boolean
     */
    public boolean isValidChannel(int channel) {
        return this.channel == channel;
    }

    /**
     * Gets page items without tools.
     *
     * @return the page items without tools
     */
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

    /**
     * Deserialize DInventory.
     *
     * @param data the data
     * @return the DInventory
     */
    public DInventory deserialize(YamlConfiguration data) {
        update();
        this.data = data;
        this.handlerName = data.getString("handlerName");
        this.uuid = UUID.fromString(data.getString("uuid"));
        this.usePage = data.getBoolean("usePage");
        this.usePageTools = data.getBoolean("usePageTools");
        this.pages = data.getInt("pages");
        this.currentPage = data.getInt("currentPage");
        this.pageTools = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            String itemPath = "pageTools." + i;
            if (data.contains(itemPath)) {
                pageTools[i] = data.getItemStack(itemPath);
            } else {
                pageTools[i] = null;
            }
        }
        this.pageItems = new HashMap<>();
        for (String page : data.getConfigurationSection("pageItems").getKeys(false)) {
            for (String itemPath : data.getConfigurationSection("pageItems." + page).getKeys(false)) {
                if (!data.isItemStack("pageItems." + page + "." + itemPath)) {
                    continue;
                }
                ItemStack item = data.getItemStack("pageItems." + page + "." + itemPath);
                int slot = Integer.parseInt(itemPath);
                if (slot < 0 || slot > 44) continue;
                if (!pageItems.containsKey(Integer.parseInt(page))) {
                    pageItems.put(Integer.parseInt(page), new ItemStack[45]);
                }
                pageItems.get(Integer.parseInt(page))[slot] = item;
            }
        }
        if (data.contains("obj")) {
            this.obj = decodeObjectFromBase64(data.getString("obj"));
        } else {
            this.obj = null;
        }
        if (data.contains("channel")) {
            this.channel = data.getInt("channel");
        } else {
            this.channel = 0;
        }
        return null;
    }

    /**
     * Serialize yaml configuration.
     *
     * @return the yaml configuration
     */
    public YamlConfiguration serialize() {
        update();
        YamlConfiguration data = new YamlConfiguration();
        data.set("handlerName", handlerName);
        data.set("uuid", uuid.toString());
        data.set("usePage", usePage);
        data.set("usePageTools", usePageTools);
        data.set("pages", pages);
        data.set("currentPage", currentPage);
        for (int i = 0; i < pageTools.length; i++) {
            if (pageTools[i] != null) {
                data.set("pageTools." + i, pageTools[i]);
            }
        }
        for (Map.Entry<Integer, ItemStack[]> entry : pageItems.entrySet()) {
            int page = entry.getKey();
            ItemStack[] items = entry.getValue();
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    data.set("pageItems." + page + "." + i, items[i]);
                }
            }
        }
        if (obj != null) {
            data.set("obj", encodeObjectToBase64(obj));
        }
        data.set("channel", channel);
        return data;
    }

    /**
     * Encode object to base 64 string.
     *
     * @param input the input
     * @return the string
     */
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

    /**
     * Decode object from base 64 object.
     *
     * @param base64Input the base 64 input
     * @return the object
     */
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

    /**
     * Save.
     */
    public void save() {
        save(name);
    }

    /**
     * Save.
     *
     * @param name the name
     */
    public void save(String name) {
        update();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Inventory name cannot be null or empty");
        }
        if (data == null) {
            throw new IllegalStateException("Data configuration is not initialized");
        }
        ConfigUtils.saveCustomData(plugin, serialize(), name, "DInventory");
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public int getSize() {
        return inventory.getSize();
    }

    /**
     * Gets max stack size.
     *
     * @return the max stack size
     */
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    /**
     * Sets max stack size.
     *
     * @param size the size
     */
    public void setMaxStackSize(int size) {
        inventory.setMaxStackSize(size);
    }

    /**
     * Gets item.
     *
     * @param index the index
     * @return the item
     */
    public ItemStack getItem(int index) {
        return inventory.getItem(index);
    }

    /**
     * Sets item.
     *
     * @param index the index
     * @param item  the item
     */
    public void setItem(int index, ItemStack item) {
        inventory.setItem(index, item);
    }

    /**
     * Add item java . util . hash map.
     *
     * @param items the items
     * @return the java . util . hash map
     */
    public java.util.HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        return inventory.addItem(items);
    }

    /**
     * Remove item java . util . hash map.
     *
     * @param items the items
     * @return the java . util . hash map
     */
    public java.util.HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        return inventory.removeItem(items);
    }

    /**
     * Get contents item stack [ ].
     *
     * @return the item stack [ ]
     */
    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    /**
     * Sets contents.
     *
     * @param items the items
     */
    public void setContents(ItemStack[] items) {
        inventory.setContents(items);
    }

    /**
     * Get storage contents item stack [ ].
     *
     * @return the item stack [ ]
     */
    public ItemStack[] getStorageContents() {
        return inventory.getStorageContents();
    }

    /**
     * Sets storage contents.
     *
     * @param items the items
     */
    public void setStorageContents(ItemStack[] items) {
        inventory.setStorageContents(items);
    }

    /**
     * Contains boolean.
     *
     * @param material the material
     * @return the boolean
     */
    public boolean contains(org.bukkit.Material material) {
        return inventory.contains(material);
    }

    /**
     * Contains boolean.
     *
     * @param item the item
     * @return the boolean
     */
    public boolean contains(ItemStack item) {
        return inventory.contains(item);
    }

    /**
     * Contains boolean.
     *
     * @param material the material
     * @param amount   the amount
     * @return the boolean
     */
    public boolean contains(org.bukkit.Material material, int amount) {
        return inventory.contains(material, amount);
    }

    /**
     * Contains boolean.
     *
     * @param item   the item
     * @param amount the amount
     * @return the boolean
     */
    public boolean contains(ItemStack item, int amount) {
        return inventory.contains(item, amount);
    }

    /**
     * Contains at least boolean.
     *
     * @param item   the item
     * @param amount the amount
     * @return the boolean
     */
    public boolean containsAtLeast(ItemStack item, int amount) {
        return inventory.containsAtLeast(item, amount);
    }

    /**
     * All java . util . hash map.
     *
     * @param material the material
     * @return the java . util . hash map
     */
    public java.util.HashMap<Integer, ? extends ItemStack> all(org.bukkit.Material material) {
        return inventory.all(material);
    }

    /**
     * All java . util . hash map.
     *
     * @param item the item
     * @return the java . util . hash map
     */
    public java.util.HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return inventory.all(item);
    }

    /**
     * First int.
     *
     * @param material the material
     * @return the int
     */
    public int first(org.bukkit.Material material) {
        return inventory.first(material);
    }

    /**
     * First int.
     *
     * @param item the item
     * @return the int
     */
    public int first(ItemStack item) {
        return inventory.first(item);
    }

    /**
     * First empty int.
     *
     * @return the int
     */
    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    /**
     * Remove.
     *
     * @param material the material
     */
    public void remove(org.bukkit.Material material) {
        inventory.remove(material);
    }

    /**
     * Remove.
     *
     * @param item the item
     */
    public void remove(ItemStack item) {
        inventory.remove(item);
    }

    /**
     * Clear.
     *
     * @param index the index
     */
    public void clear(int index) {
        inventory.clear(index);
    }

    /**
     * Clear.
     */
    public void clear() {
        inventory.clear();
    }

    /**
     * Gets viewers.
     *
     * @return the viewers
     */
    public java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public org.bukkit.event.inventory.InventoryType getType() {
        return inventory.getType();
    }

    /**
     * Gets holder.
     *
     * @return the holder
     */
    public InventoryHolder getHolder() {
        return inventory.getHolder();
    }

    /**
     * Iterator java . util . list iterator.
     *
     * @return the java . util . list iterator
     */
    public java.util.ListIterator<ItemStack> iterator() {
        return inventory.iterator();
    }

    /**
     * Iterator java . util . list iterator.
     *
     * @param index the index
     * @return the java . util . list iterator
     */
    public java.util.ListIterator<ItemStack> iterator(int index) {
        return inventory.iterator(index);
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public org.bukkit.Location getLocation() {
        return inventory.getLocation();
    }
}


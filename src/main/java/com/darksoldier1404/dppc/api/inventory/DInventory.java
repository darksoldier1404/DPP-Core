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

    public DInventory(String title, int size, JavaPlugin plugin) {
        this.inventory = Bukkit.createInventory(this, size, title);
        usePage = false;
        handlerName = plugin.getName();
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

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
        int pt = 0;
        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
            if (pageTools[pt] != null) {
                NBT.setStringTag(pageTools[pt], "pageTools", "true");
                inventory.setItem(i, pageTools[pt]);
            }
            pt++;
        }
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

    public void save() {
        save(name);
    }

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

    public boolean isEmpty() {
        return inventory.isEmpty();
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
}


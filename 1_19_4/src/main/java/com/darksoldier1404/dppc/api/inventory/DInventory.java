package com.darksoldier1404.dppc.api.inventory;

import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.DInventoryManager;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class DInventory extends CraftInventoryCustom {
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

    public DInventory(InventoryHolder holder, String title, int size, JavaPlugin plugin) {
        super(holder, size, title);
        usePage = false;
        handlerName = plugin.getName();
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
    }

    public DInventory(InventoryHolder holder, String title, int size, boolean usePage, JavaPlugin plugin) {
        super(holder, size, title);
        this.handlerName = plugin.getName();
        this.usePage = usePage;
        usePageTools = true;
        this.plugin = plugin;
        uuid = UUID.randomUUID();
        DInventoryManager.addInventory(plugin, this);
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
        for (int i = getSize() - 9; i < getSize(); i++) {
            if (pageTools[pt] != null) {
                NBT.setStringTag(pageTools[pt], "pageTools", "true");
                setItem(i, pageTools[pt]);
            }
            pt++;
        }
    }

    public void update() {
        clear();
        if (pageItems.get(currentPage) != null) {
            for (int i = 0; i < pageItems.get(currentPage).length; i++) {
                if (pageItems.get(currentPage)[i] != null) {
                    setItem(i, pageItems.get(currentPage)[i]);
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
        p.openInventory(this);
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
        for (int page = 0; page < pageItems.size(); page++) {
            ItemStack[] items = new ItemStack[45];
            for (int i = 0; i < pageItems.get(page).length - 9; i++) {
                items[i] = pageItems.get(page)[i];
            }
            resultItems.put(page, items);
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
}

![](https://bstats.org/signatures/bukkit/DP-UniversalCore.svg)
[![Gradle Build by.Bruce0203](https://github.com/darksoldier1404/DPP-Core/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/darksoldier1404/DPP-Core/actions/workflows/gradle.yml)
# DPP-Core (old name - DP-UniversalCore)

## GRADLE

## 리포지토리 - Repository
```gradle
    maven {
        url "http://dpp.dpnw.site:8081/repository/maven-public/"
        allowInsecureProtocol = true
    }
```
## 디펜던시 - Dependencies
```gradle
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.19-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.18.2-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.18.1-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.17.1-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.16.5-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.15.2-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.14.4-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.13.2-SNAPSHOT'
compileOnly 'com.darksoldier1404.dppc:DPP-Core:1.12.2-SNAPSHOT'
```
## Spigot & CraftBukkit
```gradle
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.19'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.18.2'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.18.1'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.18'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.17.1'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.17'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.5'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.4'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.3'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.2'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.1'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.15.2'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.15.1'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.15'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.4'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.3'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.2'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.1'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.14'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.13.2'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.13.1'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.13'
compileOnly 'org.bukkit.craftbukkit:craftbukkit:R0.1:1.12.2'

compileOnly 'org.spigotmc:spigot-api:R0.1:1.19'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.18.2'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.18.1'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.18'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.17.1'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.17'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.16.5'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.16.4'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.16.3'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.16.2'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.16.1'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.15.2'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.15.1'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.15'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.14.4'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.14.3'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.14.2'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.14.1'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.14'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.13.2'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.13.1'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.13'
compileOnly 'org.spigotmc:spigot-api:R0.1:1.12.2'
```

## GRADLE kotlin DSL

## 리포지토리 - Repository
```gradle
    maven {
        url = uri('http://dpp.dpnw.site:8081/repository/maven-public/')
    }
```
## 디펜던시 - Dependencies
```gradle
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.19-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.18.2-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.18.1-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.17.1-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.16.5-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.15.2-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.14.4-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.13.2-SNAPSHOT")
compileOnly("com.darksoldier1404.dppc:DPP-Core:1.12.2-SNAPSHOT")
```
## Spigot & CraftBukkit
```gradle
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.19")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.18.2")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.18.1")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.18")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.17.1")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.17")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.5")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.4")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.3")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.2")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.16.1")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.15.2")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.15.1")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.15")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.4")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.3")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.2")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.14.1")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.14")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.13.2")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.13.1")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.13")
compileOnly("org.bukkit.craftbukkit:craftbukkit:R0.1:1.12.2")

compileOnly("org.spigotmc:spigot-api:R0.1:1.19")
compileOnly("org.spigotmc:spigot-api:R0.1:1.18.2")
compileOnly("org.spigotmc:spigot-api:R0.1:1.18.1")
compileOnly("org.spigotmc:spigot-api:R0.1:1.18")
compileOnly("org.spigotmc:spigot-api:R0.1:1.17.1")
compileOnly("org.spigotmc:spigot-api:R0.1:1.17")
compileOnly("org.spigotmc:spigot-api:R0.1:1.16.5")
compileOnly("org.spigotmc:spigot-api:R0.1:1.16.4")
compileOnly("org.spigotmc:spigot-api:R0.1:1.16.3")
compileOnly("org.spigotmc:spigot-api:R0.1:1.16.2")
compileOnly("org.spigotmc:spigot-api:R0.1:1.16.1")
compileOnly("org.spigotmc:spigot-api:R0.1:1.15.2")
compileOnly("org.spigotmc:spigot-api:R0.1:1.15.1")
compileOnly("org.spigotmc:spigot-api:R0.1:1.15")
compileOnly("org.spigotmc:spigot-api:R0.1:1.14.4")
compileOnly("org.spigotmc:spigot-api:R0.1:1.14.3")
compileOnly("org.spigotmc:spigot-api:R0.1:1.14.2")
compileOnly("org.spigotmc:spigot-api:R0.1:1.14.1")
compileOnly("org.spigotmc:spigot-api:R0.1:1.14")
compileOnly("org.spigotmc:spigot-api:R0.1:1.13.2")
compileOnly("org.spigotmc:spigot-api:R0.1:1.13.1")
compileOnly("org.spigotmc:spigot-api:R0.1:1.13")
compileOnly("org.spigotmc:spigot-api:R0.1:1.12.2")
```

## API
### ConfigUtils - Custom Config Utils
```java
public static YamlConfiguration config;
public static TestPlugin plugin;
public void onEnable() {
    plugin = this;
    config = ConfigUtils.loadDefaultPluginConfig(plugin);
}
```
### DInventory - Custom Inventory Class
```java
DInventory inv = new DInventory(null, "DInventory Test", 54, true, plugin);
ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
ItemStack prev = NBT.setStringTag(new ItemStack(Material.PINK_DYE), "prev", "true");
ItemMeta im = prev.getItemMeta();
im.setDisplayName("이전 페이지");
prev.setItemMeta(im);
ItemStack next = NBT.setStringTag(new ItemStack(Material.LIME_DYE), "next", "true");
im = next.getItemMeta();
im.setDisplayName("다음 페이지");
next.setItemMeta(im);
inv.setPageTools(new ItemStack[]{pane, pane, prev, pane, pane, pane, next, pane, pane});
inv.addPageContent(/*ItemStack[]*/);
inv.update();
Player#openInventory(inv);
```
### InventoryUtils - Simple Inventory Utility
```java
Player p = Bukkit.getPlayer("DEAD_POOLIO_");
ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 10);
if(InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), item)) {
    p.getInventory().addItem(item);
}else{
    p.sendMessage("Inventory has not enough space.");
}
```
### ColorUtils - Simplest Color Applier
```java
String s = "&aTest String"
s = ColorUtils.applyColor(s);
String s2 = "<#FFFFFF>TestString"
s2 = ColorUtils.applyColor(s2);
```
### DLang - Multi Language Support
```java
private static TestPlugin plugin;
public static DLang lang;
public static YamlConfiguration config;

public void onEnable() {
    plugin = this;
    config = ConfigUtils.loadDefaultPluginConfig(plugin);
    lang = new DLang(config.getString("Settings.Lang") == null ? "English" : config.getString("Settings.Lang"), plugin);
}

public void test() {
    String arg = "Test"
    lang.get("test_text");
    // Test Text
    lang.getWithArgs("test_text2", arg);
    // Test Text: Test
}
```
#### English.yml in /lang/English.yml
```yaml
Lang: English

test_text: "Test Text"
test_text2: "Test Text: {0}"
```

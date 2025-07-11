package com.darksoldier1404.dppc.api.luckperms;

import com.darksoldier1404.dppc.DPPCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * The type Luckperm api.
 */
public class LuckpermAPI {
    private static final DPPCore plugin = DPPCore.getInstance();
    private static final LuckPerms lp = (LuckPerms) plugin.lp;
    private static final String PREFIX = "[DPP-Core.LuckpermAPI] ";
    private static final String MSG_USER_NOT_FOUND = PREFIX + "User not found.";
    private static final Logger logger = plugin.getLogger();

    /**
     * Gets user.
     *
     * @param player the player
     * @return the user
     */
    @Nullable
    public static User getUser(OfflinePlayer player) {
        return getUserOrNotify(player);
    }

    /**
     * Gets user from uuid.
     *
     * @param uuid the uuid
     * @return the user from uuid
     */
    @Nullable
    public static User getUserFromUUID(UUID uuid) {
        return lp.getUserManager().getUser(uuid);
    }

    @Nullable
    private static User getUserOrNotify(OfflinePlayer player) {
        try {
            User user = lp.getUserManager().getUser(player.getUniqueId());
            return user;
        } catch (Exception e) {
            logger.severe("Failed to get user for " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    private static <T extends Node> void addNode(OfflinePlayer player, T node) {
        User user = getUserOrNotify(player);
        if (user != null) {
            user.data().add(node);
            lp.getUserManager().saveUser(user);
            logger.info("Added node for player " + player.getName());
        }
    }

    private static <T extends Node> void removeNode(OfflinePlayer player, Class<T> nodeType, Predicate<T> predicate) {
        User user = getUserOrNotify(player);
        if (user != null) {
            boolean modified = user.getNodes().stream()
                    .filter(nodeType::isInstance)
                    .map(nodeType::cast)
                    .filter(predicate)
                    .findFirst()
                    .map(node -> {
                        user.data().remove(node);
                        return true;
                    })
                    .orElse(false);
            if (modified) {
                lp.getUserManager().saveUser(user);
                logger.info("Removed node for player " + player.getName());
            }
        }
    }

    private static <T extends Node> boolean hasNode(OfflinePlayer player, Class<T> nodeType, Predicate<T> predicate) {
        User user = getUserOrNotify(player);
        return user != null && user.getNodes().stream()
                .filter(nodeType::isInstance)
                .map(nodeType::cast)
                .anyMatch(predicate);
    }

    /**
     * Sets prefix.
     *
     * @param player   the player
     * @param prefix   the prefix
     * @param priority the priority
     */
    public static void setPrefix(OfflinePlayer player, String prefix, int priority) {
        if (prefix == null || prefix.isEmpty()) {
            return;
        }
        addNode(player, PrefixNode.builder(prefix, priority).build());
    }

    /**
     * Sets suffix.
     *
     * @param player   the player
     * @param suffix   the suffix
     * @param priority the priority
     */
    public static void setSuffix(OfflinePlayer player, String suffix, int priority) {
        if (suffix == null || suffix.isEmpty()) {
            return;
        }
        addNode(player, SuffixNode.builder(suffix, priority).build());
    }

    /**
     * Del prefix.
     *
     * @param player the player
     * @param prefix the prefix
     */
    public static void delPrefix(OfflinePlayer player, String prefix) {
        removeNode(player, PrefixNode.class, node -> node.getKey().equals(prefix));
    }

    /**
     * Del suffix.
     *
     * @param player the player
     * @param suffix the suffix
     */
    public static void delSuffix(OfflinePlayer player, String suffix) {
        removeNode(player, SuffixNode.class, node -> node.getKey().equals(suffix));
    }

    /**
     * Del prefix.
     *
     * @param player   the player
     * @param priority the priority
     */
    public static void delPrefix(OfflinePlayer player, int priority) {
        removeNode(player, PrefixNode.class, node -> node.getPriority() == priority);
    }

    /**
     * Del suffix.
     *
     * @param player   the player
     * @param priority the priority
     */
    public static void delSuffix(OfflinePlayer player, int priority) {
        removeNode(player, SuffixNode.class, node -> node.getPriority() == priority);
    }

    /**
     * Is prefix exists boolean.
     *
     * @param player the player
     * @param prefix the prefix
     * @return the boolean
     */
    public static boolean isPrefixExists(OfflinePlayer player, String prefix) {
        return hasNode(player, PrefixNode.class, node -> node.getKey().equals(prefix));
    }

    /**
     * Is suffix exists boolean.
     *
     * @param player the player
     * @param suffix the suffix
     * @return the boolean
     */
    public static boolean isSuffixExists(OfflinePlayer player, String suffix) {
        return hasNode(player, SuffixNode.class, node -> node.getKey().equals(suffix));
    }

    /**
     * Is prefix exists boolean.
     *
     * @param player   the player
     * @param priority the priority
     * @return the boolean
     */
    public static boolean isPrefixExists(OfflinePlayer player, int priority) {
        return hasNode(player, PrefixNode.class, node -> node.getPriority() == priority);
    }

    /**
     * Is suffix exists boolean.
     *
     * @param player   the player
     * @param priority the priority
     * @return the boolean
     */
    public static boolean isSuffixExists(OfflinePlayer player, int priority) {
        return hasNode(player, SuffixNode.class, node -> node.getPriority() == priority);
    }

    /**
     * Remove node by key.
     *
     * @param <T>      the type parameter
     * @param player   the player
     * @param nodeType the node type
     * @param key      the key
     */
    public static <T extends Node> void removeNodeByKey(OfflinePlayer player, Class<T> nodeType, String key) {
        removeNode(player, nodeType, node -> node.getKey().equals(key));
    }

    /**
     * Remove node by priority.
     *
     * @param <T>               the type parameter
     * @param player            the player
     * @param nodeType          the node type
     * @param priority          the priority
     * @param priorityExtractor the priority extractor
     */
    public static <T extends Node> void removeNodeByPriority(OfflinePlayer player, Class<T> nodeType, int priority, Function<T, Integer> priorityExtractor) {
        removeNode(player, nodeType, node -> priorityExtractor.apply(node) == priority);
    }

    /**
     * Has node by key boolean.
     *
     * @param <T>      the type parameter
     * @param player   the player
     * @param nodeType the node type
     * @param key      the key
     * @return the boolean
     */
    public static <T extends Node> boolean hasNodeByKey(OfflinePlayer player, Class<T> nodeType, String key) {
        return hasNode(player, nodeType, node -> node.getKey().equals(key));
    }

    /**
     * Has node by priority boolean.
     *
     * @param <T>               the type parameter
     * @param player            the player
     * @param nodeType          the node type
     * @param priority          the priority
     * @param priorityExtractor the priority extractor
     * @return the boolean
     */
    public static <T extends Node> boolean hasNodeByPriority(OfflinePlayer player, Class<T> nodeType, int priority, Function<T, Integer> priorityExtractor) {
        return hasNode(player, nodeType, node -> priorityExtractor.apply(node) == priority);
    }
}
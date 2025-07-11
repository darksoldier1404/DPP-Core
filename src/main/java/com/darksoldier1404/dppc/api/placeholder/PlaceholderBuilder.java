package com.darksoldier1404.dppc.api.placeholder;

import com.darksoldier1404.dppc.DPPCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * The type Placeholder builder.
 */
public class PlaceholderBuilder {
    private final String identifier;
    private final String author;
    private final String version;
    private final BiFunction<Player, String, String> requestHandler;

    private PlaceholderBuilder(Builder builder) {
        this.identifier = builder.identifier;
        this.author = builder.author;
        this.version = builder.version;
        this.requestHandler = builder.requestHandler;
    }

    /**
     * The type Internal expansion.
     */
    public static class InternalExpansion extends PlaceholderExpansion {
        private final String identifier;
        private final String author;
        private final String version;
        private final BiFunction<Player, String, String> requestHandler;

        /**
         * Instantiates a new Internal expansion.
         *
         * @param identifier     the identifier
         * @param author         the author
         * @param version        the version
         * @param requestHandler the request handler
         */
        public InternalExpansion(String identifier, String author, String version, BiFunction<Player, String, String> requestHandler) {
            this.identifier = identifier;
            this.author = author;
            this.version = version;
            this.requestHandler = requestHandler;
        }

        @Override
        public @NotNull String getIdentifier() {
            return identifier;
        }

        @Override
        public @NotNull String getAuthor() {
            return author;
        }

        @Override
        public @NotNull String getVersion() {
            return version;
        }

        @Nullable
        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            return requestHandler != null ? requestHandler.apply(player, params) : null;
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private final JavaPlugin plugin;
        private String identifier;
        private String author;
        private String version;
        private BiFunction<Player, String, String> requestHandler;

        /**
         * Instantiates a new Builder.
         *
         * @param plugin the plugin
         */
        public Builder(JavaPlugin plugin) {
            this.plugin = plugin;
            this.author = String.join(", ", plugin.getDescription().getAuthors());
            this.version = plugin.getDescription().getVersion();
        }

        /**
         * Identifier builder.
         *
         * @param identifier the identifier
         * @return the builder
         */
        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * Author builder.
         *
         * @param author the author
         * @return the builder
         */
        public Builder author(String author) {
            this.author = author;
            return this;
        }

        /**
         * Version builder.
         *
         * @param version the version
         * @return the builder
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * On request builder.
         *
         * @param handler the handler
         * @return the builder
         */
        public Builder onRequest(BiFunction<Player, String, String> handler) {
            this.requestHandler = handler;
            return this;
        }

        /**
         * Build.
         */
        public void build() {
            if (identifier == null || identifier.isEmpty()) {
                throw new IllegalArgumentException("Identifier must not be null or empty");
            }
            InternalExpansion expansion = new InternalExpansion(identifier, author, version, requestHandler);
            DPPCore.placeholders.add(expansion);
        }
    }
}
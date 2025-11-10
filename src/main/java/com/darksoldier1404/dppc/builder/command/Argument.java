package com.darksoldier1404.dppc.builder.command;

import java.util.ArrayList;
import java.util.List;

public class Argument<T> {
    final String name;
    final ArgumentType type;
    final boolean required;
    final List<T> suggestions = new ArrayList<>();

    public Argument(String name, ArgumentType type, boolean required, List<T> suggestions) {
        this.name = name;
        this.type = type;
        this.required = required;
        if (suggestions != null) {
            this.suggestions.addAll(suggestions);
        }
    }

    public List<String> getSuggestionsAsStringList() {
        List<String> stringSuggestions = new ArrayList<>();
        for (T suggestion : suggestions) {
            switch (type) {
                case STRING:
                    stringSuggestions.add((String) suggestion);
                    break;
                case INTEGER:
                    stringSuggestions.add(String.valueOf((Integer) suggestion));
                    break;
                case DOUBLE:
                    stringSuggestions.add(String.valueOf((Double) suggestion));
                    break;
                case BOOLEAN:
                    stringSuggestions.add(String.valueOf((Boolean) suggestion));
                    break;
                case MATERIAL:
                    stringSuggestions.add(((org.bukkit.Material) suggestion).name());
                    break;
                case ENTITY_TYPE:
                    stringSuggestions.add(((org.bukkit.entity.EntityType) suggestion).name());
                    break;
                case OFFLINE_PLAYER:
                    String name = ((org.bukkit.OfflinePlayer) suggestion).getName();
                    if (name != null) {
                        stringSuggestions.add(name);
                    }
                    break;
                default:
                    stringSuggestions.add(suggestion.toString());
            }
        }
        return stringSuggestions;
    }
}
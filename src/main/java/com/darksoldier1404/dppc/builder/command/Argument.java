package com.darksoldier1404.dppc.builder.command;

import java.util.ArrayList;
import java.util.List;

public class Argument {
    final String name;
    final ArgumentType type;
    final boolean required;
    final List<String> suggestions = new ArrayList<>();

    public Argument(String name, ArgumentType type, boolean required, List<String> suggestions) {
        this.name = name;
        this.type = type;
        this.required = required;
        if (suggestions != null) {
            this.suggestions.addAll(suggestions);
        }
    }
}
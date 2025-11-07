package com.darksoldier1404.dppc.builder.command;

public class Argument {
    final String name;
    final ArgumentType type;
    final boolean required;

    public Argument(String name, ArgumentType type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }
}
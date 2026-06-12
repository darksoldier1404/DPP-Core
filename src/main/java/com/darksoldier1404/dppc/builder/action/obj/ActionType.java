package com.darksoldier1404.dppc.builder.action.obj;

public enum ActionType {
    // --- Timing ---
    DELAY,

    // --- Messages ---
    SEND_MESSAGE,
    SEND_TITLE,
    SEND_ACTIONBAR,
    BROADCAST,
    BROADCAST_WORLD,

    // --- Commands ---
    EXECUTE_AS_ADMIN,
    EXECUTE_AS_PLAYER,

    // --- Player Movement ---
    TELEPORT,

    // --- Player State ---
    CLOSE_INVENTORY,
    SET_GAMEMODE,
    GIVE_EXP,
    TAKE_EXP,
    SET_HEALTH,
    SET_HUNGER,
    KICK,

    // --- Effects ---
    PLAY_SOUND,
    PLAY_PARTICLE,
    ADD_POTION_EFFECT,
    REMOVE_POTION_EFFECT,
    CLEAR_EFFECTS,

    // --- Inventory ---
    GIVE_ITEM,
    TAKE_ITEM,

    // --- Variables ---
    SET_VARIABLE,
    ADD_VARIABLE,
    RANDOM_NUMBER,

    // --- Conditions ---
    IF_HAS_PERMISSION,
    IF_NOT_PERMISSION,
    IF_VARIABLE_EQUALS,
    IF_VARIABLE_NOT_EQUALS,
    IF_VARIABLE_GREATER,
    IF_VARIABLE_LESS,
    ELSE,
    END_IF,

    // --- Flow Control ---
    CANCEL,
    CALL_ACTION,
}

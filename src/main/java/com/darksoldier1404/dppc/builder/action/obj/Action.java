package com.darksoldier1404.dppc.builder.action.obj;

public interface Action {
    void execute(ActionContext context);

    ActionType getActionType();

    String serialize();

    default String getDisplayText() {
        return serialize();
    }

    /**
     * Flow control actions (IF/ELSE/END_IF/CANCEL) always execute
     * regardless of current condition state.
     */
    default boolean isFlowControl() {
        return false;
    }
}

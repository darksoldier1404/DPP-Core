package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link AddVariableAction#execute} only touches the context variable store,
 * so no player is required.
 */
class AddVariableActionTest {

    @Test
    void addsToExistingNumericVariableAsInteger() {
        ActionContext ctx = new ActionContext(null);
        ctx.setVariable("n", "5");
        new AddVariableAction("n", 3).execute(ctx);
        // 8.0 collapses to integer string "8".
        assertEquals("8", ctx.getVariable("n"));
    }

    @Test
    void keepsFractionalResult() {
        ActionContext ctx = new ActionContext(null);
        ctx.setVariable("n", "1");
        new AddVariableAction("n", 0.5).execute(ctx);
        assertEquals("1.5", ctx.getVariable("n"));
    }

    @Test
    void nonNumericVariableResetsToAmount() {
        ActionContext ctx = new ActionContext(null);
        // default variable value is "" -> NumberFormatException -> set to amount
        new AddVariableAction("fresh", 4).execute(ctx);
        assertEquals("4.0", ctx.getVariable("fresh"));
    }

    @Test
    void getActionTypeAndSerialize() {
        AddVariableAction a = new AddVariableAction("score", 2);
        assertEquals(ActionType.ADD_VARIABLE, a.getActionType());
        assertEquals("add_variable score 2.0", a.serialize());
    }

    @Test
    void parseValidAndInvalid() {
        assertEquals(ActionType.ADD_VARIABLE, AddVariableAction.parse("add_variable x 5").getActionType());
        assertNull(AddVariableAction.parse("add_variable x notanumber"));
        assertNull(AddVariableAction.parse("add_variable x"));
    }
}

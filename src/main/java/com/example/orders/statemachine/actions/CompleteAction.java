package com.example.orders.statemachine.actions;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class CompleteAction implements Action<OrderState, OrderEvent> {
    @Override
    public void execute(StateContext<OrderState, OrderEvent> stateContext) {
        final String orderId = stateContext.getExtendedState().get("ORDER_ID", String.class);
        System.out.println("Order with id " + orderId + " successfully completed");
    }
}

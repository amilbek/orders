package com.example.orders.statemachine.actions;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ErrorAction implements Action<OrderState, OrderEvent> {
    @Override
    public void execute(final StateContext<OrderState, OrderEvent> context) {
        System.out.println("Error during status transition " + context.getTarget().getId());
    }
}

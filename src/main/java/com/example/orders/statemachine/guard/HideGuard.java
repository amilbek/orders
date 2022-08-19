package com.example.orders.statemachine.guard;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class HideGuard implements Guard<OrderState, OrderEvent> {
    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> stateContext) {
        return false;
    }
}

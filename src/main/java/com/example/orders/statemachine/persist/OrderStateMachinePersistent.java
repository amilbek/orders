package com.example.orders.statemachine.persist;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;

public class OrderStateMachinePersistent implements StateMachinePersist<OrderState, OrderEvent, String> {

    private final HashMap<String, StateMachineContext<OrderState, OrderEvent>> contexts = new HashMap<>();

    @Override
    public void write(StateMachineContext<OrderState, OrderEvent> stateMachineContext, String s) {
        contexts.put(s, stateMachineContext);
    }

    @Override
    public StateMachineContext<OrderState, OrderEvent> read(String s) {
        return contexts.get(s);
    }
}

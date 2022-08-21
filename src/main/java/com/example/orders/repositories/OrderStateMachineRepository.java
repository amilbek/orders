package com.example.orders.repositories;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import org.springframework.statemachine.StateMachine;

public interface OrderStateMachineRepository {

    StateMachine<OrderState, OrderEvent> cancelOrder(Long id);

    StateMachine<OrderState, OrderEvent> startOrder(Long id);

    StateMachine<OrderState, OrderEvent> completeOrder(Long id);
}

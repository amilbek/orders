package com.example.orders.statemachine;

import com.example.orders.exceptions.OrderNotFoundException;
import com.example.orders.models.Order;
import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import com.example.orders.repositories.OrderRepository;
import com.example.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {

    private final OrderRepository orderRepository;

    public void preStateChange(State<OrderState, OrderEvent> state,
                               Message<OrderEvent> message,
                               Transition<OrderState, OrderEvent> transition,
                               StateMachine<OrderState, OrderEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders().
                            getOrDefault(OrderService.ORDER_ID_HEADER, -1L)))
                    .ifPresent(orderId -> {
                        Order order = orderRepository.findById(orderId).orElseThrow(
                                () -> new OrderNotFoundException("Not found id " + orderId)
                        );
                        order.setOrderState(state.getId());
                        orderRepository.save(order);
                    });
        });
    }
}

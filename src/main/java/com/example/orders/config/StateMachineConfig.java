package com.example.orders.config;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import com.example.orders.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static com.example.orders.models.enums.OrderEvent.*;
import static com.example.orders.models.enums.OrderState.*;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(final StateMachineStateConfigurer<OrderState, OrderEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(NEW)
                .states(EnumSet.allOf(OrderState.class))
                .end(COMPLETED)
                .end(CANCELED);
    }

    public Guard<OrderState, OrderEvent> orderIdGuard() {
        return context -> context.getMessageHeader(OrderService.ORDER_ID_HEADER) != null;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(NEW)
                .target(IN_PROGRESS)
                .event(START)
                .action(startAction())
                .guard(orderIdGuard())

                .and()
                .withExternal()
                .source(NEW)
                .target(CANCELED)
                .action(cancelAction())
                .event(CANCEL)

                .and()
                .withExternal()
                .source(IN_PROGRESS)
                .target(CANCELED)
                .action(cancelAction())
                .event(CANCEL)

                .and()
                .withExternal()
                .source(IN_PROGRESS)
                .target(COMPLETED)
                .action(completeAction())
                .event(COMPLETE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config)
            throws Exception {
        StateMachineListenerAdapter<OrderState, OrderEvent> adapter =
                new StateMachineListenerAdapter<OrderState, OrderEvent>(){
            @Override
            public void stateChanged(State from, State to) {
                log.info(String.format("State Changed from : %s, to: %s", from.getId(), to.getId()));
            }
        };

        config.withConfiguration().listener(adapter);
    }

    public Action<OrderState, OrderEvent> startAction(){
        return context -> {
            System.out.println("Event: Start action!!!");
            System.out.println("State: In progress!!!");
        };
    }

    public Action<OrderState, OrderEvent> cancelAction(){
        return context -> {
            System.out.println("Event: Cancel action!!!");
            System.out.println("State: Canceled!!!");
        };
    }

    public Action<OrderState, OrderEvent> completeAction(){
        return context -> {
            System.out.println("Event: Complete action!!!");
            System.out.println("State: Completed!!!");
        };
    }
}

package com.example.orders.config;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import com.example.orders.statemachine.actions.CancelAction;
import com.example.orders.statemachine.actions.CompleteAction;
import com.example.orders.statemachine.actions.ErrorAction;
import com.example.orders.statemachine.actions.StartAction;
import com.example.orders.statemachine.guard.HideGuard;
import com.example.orders.statemachine.listener.OrderStateMachineApplicationListener;
import com.example.orders.statemachine.persist.OrderStateMachinePersistent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

import static com.example.orders.models.enums.OrderEvent.*;
import static com.example.orders.models.enums.OrderState.*;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(final StateMachineConfigurationConfigurer<OrderState, OrderEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(new OrderStateMachineApplicationListener());
    }

    @Override
    public void configure(final StateMachineStateConfigurer<OrderState, OrderEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(NEW)
                .end(COMPLETED)
                .states(EnumSet.allOf(OrderState.class));
    }

    @Override
    public void configure(final StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(NEW)
                .target(IN_PROGRESS)
                .event(START)
                .action(startAction(), errorAction())

                .and()
                .withExternal()
                .source(NEW)
                .target(CANCELED)
                .event(CANCEL)
                .action(cancelAction(), errorAction())

                .and()
                .withExternal()
                .source(IN_PROGRESS)
                .target(COMPLETED)
                .event(COMPLETE)
                .action(completeAction(), errorAction())

                .and()
                .withExternal()
                .source(IN_PROGRESS)
                .target(CANCELED)
                .event(CANCEL)
                .action(cancelAction(), errorAction());
    }

    @Bean
    public Action<OrderState, OrderEvent> startAction() {
        return new StartAction();
    }

    @Bean
    public Action<OrderState, OrderEvent> cancelAction() {
        return new CancelAction();
    }

    @Bean
    public Action<OrderState, OrderEvent> completeAction() {
        return new CompleteAction();
    }

    @Bean
    public Action<OrderState, OrderEvent> errorAction() {
        return new ErrorAction();
    }

    @Bean
    public Guard<OrderState, OrderEvent> hideGuard() {
        return new HideGuard();
    }

    @Bean
    public StateMachinePersister<OrderState, OrderEvent, String> persistent() {
        return new DefaultStateMachinePersister<>(new OrderStateMachinePersistent());
    }
}

package com.example.orders.statemachine.listener;

import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

public class OrderStateMachineApplicationListener implements StateMachineListener<OrderState, OrderEvent> {
    @Override
    public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
        if (from.getId() != null) {
            System.out.println("Transition from status of " + from.getId() +
                    " to status " + to.getId());
        }
    }

    @Override
    public void stateEntered(State<OrderState, OrderEvent> state) {

    }

    @Override
    public void stateExited(State<OrderState, OrderEvent> state) {

    }

    @Override
    public void eventNotAccepted(Message<OrderEvent> message) {

    }

    @Override
    public void transition(Transition<OrderState, OrderEvent> transition) {

    }

    @Override
    public void transitionStarted(Transition<OrderState, OrderEvent> transition) {

    }

    @Override
    public void transitionEnded(Transition<OrderState, OrderEvent> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<OrderState, OrderEvent> stateMachine) {

    }

    @Override
    public void stateMachineStopped(StateMachine<OrderState, OrderEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(StateMachine<OrderState, OrderEvent> stateMachine, Exception e) {

    }

    @Override
    public void extendedStateChanged(Object o, Object o1) {

    }

    @Override
    public void stateContext(StateContext<OrderState, OrderEvent> stateContext) {

    }
}

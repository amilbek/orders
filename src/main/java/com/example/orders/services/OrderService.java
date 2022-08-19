package com.example.orders.services;

import com.example.orders.db.filter.requests.SearchRequest;
import com.example.orders.db.filter.SearchSpecification;
import com.example.orders.dto.OrderDTO;
import com.example.orders.exceptions.OrderNotFoundException;
import com.example.orders.models.Order;
import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import com.example.orders.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class OrderService {

    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    private final OrderRepository orderRepository;

    private static final String ORDER_ID_HEADER = "orderId";

    @Autowired
    public OrderService(StateMachineFactory<OrderState, OrderEvent> stateMachineFactory,
                        OrderRepository orderRepository) {
        this.stateMachineFactory = stateMachineFactory;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setName(orderDTO.getName());
        order.setAddressTo(orderDTO.getAddressTo());
        order.setCosting(orderDTO.getCosting());
        order.setCustomer(orderDTO.getCustomer());
        order.setOrderState(OrderState.NEW);
        return orderRepository.save(order);
    }

    public Order editOrder(Long id, OrderDTO orderDTO) {
        Order order = getOrder(id);
        order.setName(orderDTO.getName());
        order.setAddressTo(orderDTO.getAddressTo());
        order.setCosting(orderDTO.getCosting());
        order.setCustomer(orderDTO.getCustomer());
        order.setOrderState(OrderState.NEW);
        return orderRepository.save(order);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException("Order not found with given id " + id + "\n"));
    }

    public Order cancelOrder(Long id) {
        final StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("ORDER_ID", id);
        Order order = getOrder(id);
        try {
            stateMachine.sendEvent(OrderEvent.CANCEL);
            order.setOrderState(OrderState.CANCELED);
            return orderRepository.save(order);
        } catch (NullPointerException ex) {
            ex.getMessage();
        }
        return order;
    }

    public Order startOrder(Long id) {
        final StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("ORDER_ID", id);
        Order order = getOrder(id);
        try {
            stateMachine.sendEvent(OrderEvent.START);
            order.setOrderState(OrderState.IN_PROGRESS);
            return orderRepository.save(order);
        } catch (NullPointerException ex) {
            ex.getMessage();
        }
        return order;
    }

    public Order completeOrder(Long id) {
        final StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("ORDER_ID", id);
        Order order = getOrder(id);
        try {
            stateMachine.sendEvent(OrderEvent.COMPLETE);
            order.setOrderState(OrderState.COMPLETED);
            return orderRepository.save(order);
        } catch (NullPointerException ex) {
            ex.getMessage();
        }
        return order;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public Page<Order> searchOrder(SearchRequest request) {
        SearchSpecification<Order> specification = new SearchSpecification<>(request);
        Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
        return orderRepository.findAll(specification, pageable);
    }
}

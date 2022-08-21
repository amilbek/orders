package com.example.orders.services;

import com.example.orders.db.filter.requests.SearchRequest;
import com.example.orders.db.filter.SearchSpecification;
import com.example.orders.dto.OrderDTO;
import com.example.orders.exceptions.OrderNotFoundException;
import com.example.orders.models.Order;
import com.example.orders.models.enums.OrderEvent;
import com.example.orders.models.enums.OrderState;
import com.example.orders.repositories.OrderRepository;
import com.example.orders.repositories.OrderStateMachineRepository;
import com.example.orders.statemachine.OrderStateChangeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@SuppressWarnings("all")
public class OrderService implements OrderStateMachineRepository {

    public static final String ORDER_ID_HEADER = "orderId";

    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(StateMachineFactory<OrderState, OrderEvent> stateMachineFactory,
                        OrderStateChangeInterceptor orderStateChangeInterceptor,
                        OrderRepository orderRepository) {
        this.stateMachineFactory = stateMachineFactory;
        this.orderStateChangeInterceptor = orderStateChangeInterceptor;
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

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public Page<Order> searchOrder(SearchRequest request) {
        SearchSpecification<Order> specification = new SearchSpecification<>(request);
        Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
        return orderRepository.findAll(specification, pageable);
    }

    @Transactional
    @Override
    public StateMachine<OrderState, OrderEvent> cancelOrder(Long id) {
        StateMachine<OrderState, OrderEvent> sm = build(id);
        sendEvent(id, sm, OrderEvent.CANCEL);
        Order order = getOrder(id);
        order.setOrderState(OrderState.CANCELED);
        orderRepository.save(order);
        return sm;
    }

    public StateMachine<OrderState, OrderEvent> startOrder(Long id) {
        StateMachine<OrderState, OrderEvent> sm = build(id);
        sendEvent(id, sm, OrderEvent.START);
        Order order = getOrder(id);
        order.setOrderState(OrderState.IN_PROGRESS);
        orderRepository.save(order);
        return sm;
    }

    public StateMachine<OrderState, OrderEvent> completeOrder(Long id) {
        StateMachine<OrderState, OrderEvent> sm = build(id);
        sendEvent(id, sm, OrderEvent.COMPLETE);
        Order order = getOrder(id);
        order.setOrderState(OrderState.COMPLETED);
        orderRepository.save(order);
        return sm;
    }

    private void sendEvent(Long id, StateMachine<OrderState, OrderEvent> sm, OrderEvent event){
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, id)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<OrderState, OrderEvent> build(Long id){
        Order order = getOrder(id);

        StateMachine<OrderState, OrderEvent> sm =
                stateMachineFactory.getStateMachine(Long.toString(order.getId()));

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderState(),
                            null,
                            null,
                            null));
                });

        sm.start();

        return sm;
    }
}

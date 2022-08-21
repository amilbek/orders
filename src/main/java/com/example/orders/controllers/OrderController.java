package com.example.orders.controllers;

import com.example.orders.db.filter.requests.SearchRequest;
import com.example.orders.dto.OrderDTO;
import com.example.orders.models.Order;
import com.example.orders.models.enums.OrderState;
import com.example.orders.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/orders")
@SuppressWarnings("unused")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> saveOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getOrder(@PathVariable("id") Long id) {
        Order order = orderService.getOrder(id);

        EntityModel<Order> resource = EntityModel.of(order);
        List<OrderState> allowedActions = allowedActions(order);
        resource.add(linkTo(methodOn(this.getClass()).getOrder(id)).withSelfRel());
        resource.add(linkTo(methodOn(this.getClass()).editOrder(id, new OrderDTO())).withRel("edit"));
        resource.add(linkTo(methodOn(this.getClass()).deleteOrder(id)).withRel("delete"));
        allowedActions.forEach(action -> {
            if (action.equals(OrderState.CANCELED)) {
                resource.add(linkTo(methodOn(this.getClass()).cancelOrder(id)).withRel("cancel"));
            }
            if (action.equals(OrderState.IN_PROGRESS)) {
                resource.add(linkTo(methodOn(this.getClass()).startOrder(id)).withRel("start"));
            }
            if (action.equals(OrderState.COMPLETED)) {
                resource.add(linkTo(methodOn(this.getClass()).completeOrder(id)).withRel("complete"));
            }
        });
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable("id") Long id) {
        orderService.cancelOrder(id);
        Order order = orderService.getOrder(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/start")
    public ResponseEntity<Object> startOrder(@PathVariable("id") Long id) {
        orderService.startOrder(id);
        Order order = orderService.getOrder(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/complete")
    public ResponseEntity<Object> completeOrder(@PathVariable("id") Long id) {
        orderService.completeOrder(id);
        Order order = orderService.getOrder(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> editOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO) {
        Order order = orderService.editOrder(id, orderDTO);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>("Order is deleted", HttpStatus.OK);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<Page<Order>> search(@RequestBody SearchRequest request) {
        return new ResponseEntity<>(orderService.searchOrder(request), HttpStatus.OK);
    }

    private List<OrderState> allowedActions(Order order) {
        List<OrderState> actions = new ArrayList<>();
        if (order.getOrderState() == OrderState.NEW) {
            actions.add(OrderState.CANCELED);
            actions.add(OrderState.IN_PROGRESS);
        }
        if (order.getOrderState() == OrderState.IN_PROGRESS) {
            actions.add(OrderState.CANCELED);
            actions.add(OrderState.COMPLETED);
        }
        return actions;
    }
}

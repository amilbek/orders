package com.example.orders.dto;

import com.example.orders.models.enums.OrderState;
import lombok.Data;

import java.math.BigInteger;

@Data
public class OrderDTO {

    private String name;
    private String addressTo;
    private BigInteger costing;
    private String customer;
    private OrderState orderState;
}

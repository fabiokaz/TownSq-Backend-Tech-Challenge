package com.kaz.townsq.controller;

import com.kaz.townsq.dto.mapper.OrderMapper;
import com.kaz.townsq.dto.request.OrderRequest;
import com.kaz.townsq.dto.response.OrderResponse;
import com.kaz.townsq.exception.OrderNotFoundException;
import com.kaz.townsq.model.Order;
import com.kaz.townsq.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrdersController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest, Authentication authentication) {
        String username = authentication.getName();
        Order newOrder = orderService.createOrder(orderRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toOrderResponse(newOrder, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long id, Authentication authentication) {
        try {
            Order order = orderService.findOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
            return ResponseEntity.status(HttpStatus.FOUND).body(orderMapper.toOrderResponse(order, authentication));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAllUserOrders(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Order> orders = orderService.findByUser(username);
            return ResponseEntity.status(HttpStatus.FOUND).body(Collections.singletonList(orderMapper.toOrderResponseList(orders, authentication)));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(e.getMessage()));
        }
    }
}
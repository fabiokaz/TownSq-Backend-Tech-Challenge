package com.kaz.townsq.service;

import com.kaz.townsq.dto.request.OrderItemRequest;
import com.kaz.townsq.dto.request.OrderRequest;
import com.kaz.townsq.exception.OrderNotFoundException;
import com.kaz.townsq.model.Order;
import com.kaz.townsq.model.OrderItem;
import com.kaz.townsq.model.User;
import com.kaz.townsq.repository.OrderRepository;
import com.kaz.townsq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(OrderRequest orderRequest, String username) {
        User user = getUserByUsername(username);

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> createOrderItem(itemRequest, order))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalValue(calculateTotalValue(orderItems));

        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(OrderItemRequest itemRequest, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(itemRequest.getProduct());
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setUnitPrice(itemRequest.getUnitPrice());
        orderItem.setTotalPrice(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

        return orderItem;
    }

    private BigDecimal calculateTotalValue(List<OrderItem> items) {
        BigDecimal totalValue = BigDecimal.ZERO;
        totalValue = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalValue;
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUser(String username) {
        User user = getUserByUsername(username);
        return Optional.of(orderRepository.findByUser(user))
                .filter(orders -> !orders.isEmpty())
                .orElseThrow(() -> new OrderNotFoundException("No orders found for user: " + user.getUsername()));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
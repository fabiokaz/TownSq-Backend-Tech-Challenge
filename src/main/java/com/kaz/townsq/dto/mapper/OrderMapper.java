package com.kaz.townsq.dto.mapper;

import com.kaz.townsq.controller.OrdersController;
import com.kaz.townsq.dto.response.OrderItemResponse;
import com.kaz.townsq.dto.response.OrderResponse;
import com.kaz.townsq.model.Order;
import com.kaz.townsq.model.OrderItem;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order, Authentication authentication) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getUserId())
                .username(order.getUser().getUsername())
                .items(itemResponses)
                .totalPrice(order.getTotalValue().setScale(2, RoundingMode.HALF_EVEN))
                .createdAt(order.getCreatedAt())
                .build();

        // Adiciona o link HATEOAS
        Link selfLink = linkTo(methodOn(OrdersController.class).getOrderById(order.getId(), authentication)).withSelfRel();
        response.add(selfLink);

        return response;
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return OrderItemResponse.builder()
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public List<OrderResponse> toOrderResponseList(List<Order> orders, Authentication authentication) {
        if (orders == null) {
            return Collections.emptyList();
        }

        return orders.stream()
                .map(order -> toOrderResponse(order, authentication))
                .collect(Collectors.toList());
    }
}

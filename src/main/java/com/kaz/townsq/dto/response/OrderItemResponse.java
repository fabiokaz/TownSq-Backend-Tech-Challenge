package com.kaz.townsq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private String product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
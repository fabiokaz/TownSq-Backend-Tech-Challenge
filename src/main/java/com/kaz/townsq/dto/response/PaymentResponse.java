package com.kaz.townsq.dto.response;

import com.kaz.townsq.model.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentResponse(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal amount,
        LocalDateTime paymentDate,
        PaymentStatus status,
        UUID transactionId
) {}


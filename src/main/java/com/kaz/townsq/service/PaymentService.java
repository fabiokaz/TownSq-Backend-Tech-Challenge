package com.kaz.townsq.service;

import com.kaz.townsq.dto.request.PaymentRequest;
import com.kaz.townsq.exception.*;
import com.kaz.townsq.model.Order;
import com.kaz.townsq.model.Payment;
import com.kaz.townsq.model.PaymentStatus;
import com.kaz.townsq.model.User;
import com.kaz.townsq.repository.OrderRepository;
import com.kaz.townsq.repository.PaymentRepository;
import com.kaz.townsq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public Payment processPayment(PaymentRequest paymentRequest, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));
        Order order = orderRepository.findById(paymentRequest.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + paymentRequest.orderId()));

        if (order.getTotalValue().compareTo(paymentRequest.amount()) != 0) {
            throw new InvalidPaymentAmountException("Payment amount does not match order total");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(user);
        payment.setAmount(paymentRequest.amount());
        simulatePaymentProcessing(payment);

        return paymentRepository.save(payment);
    }


    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));
    }

    private UUID generateTransactionId() {
        return UUID.randomUUID();
    }

    private void simulatePaymentProcessing(Payment payment) {
        try {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(generateTransactionId());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("Payment processing failed");
        }
    }
}

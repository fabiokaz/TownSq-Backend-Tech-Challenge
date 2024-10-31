package com.kaz.townsq.controller;

import com.kaz.townsq.dto.mapper.PaymentMapper;
import com.kaz.townsq.dto.request.PaymentRequest;
import com.kaz.townsq.dto.response.PaymentResponse;
import com.kaz.townsq.exception.InvalidPaymentAmountException;
import com.kaz.townsq.exception.OrderNotFoundException;
import com.kaz.townsq.exception.PaymentNotFoundException;
import com.kaz.townsq.service.PaymentService;
import com.kaz.townsq.validation.RoleValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private final RoleValidationService roleValidationService;

    @PostMapping
    public ResponseEntity<Object> processPayment(@RequestBody @Valid PaymentRequest paymentRequest, Authentication authentication) {
        try {
            roleValidationService.validateRoleToProcessPayments(authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toPaymentResponse(paymentService.processPayment(paymentRequest, authentication)));
        } catch (InvalidPaymentAmountException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPaymentById(@PathVariable Long id, Authentication authentication) {
        try{
        return ResponseEntity.ok(paymentMapper.toPaymentResponse(paymentService.getPaymentById(id)));
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
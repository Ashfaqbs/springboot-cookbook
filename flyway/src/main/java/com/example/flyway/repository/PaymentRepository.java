package com.example.flyway.repository;

import com.example.flyway.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByStatus(String status);
    
    Optional<Payment> findByTransactionId(String transactionId);
}

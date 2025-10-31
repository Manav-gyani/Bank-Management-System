package com.bank.repository;

import com.bank.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByAccountIdAndTimestampBetween(
            String accountId, LocalDateTime start, LocalDateTime end);
    List<Transaction> findByTransactionId(String transactionId);
}
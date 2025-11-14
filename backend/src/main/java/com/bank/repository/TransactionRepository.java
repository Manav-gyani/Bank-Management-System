package com.bank.repository;

import com.bank.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByAccountIdAndTimestampBetween(
            String accountId, LocalDateTime start, LocalDateTime end);
    List<Transaction> findByTransactionId(String transactionId);


    //New

    // NEW METHODS - ADD THESE FOR ADMIN PANEL:
    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "{ 'status': ?0 }", count = false)
    @Aggregation(pipeline = {
            "{ '$match': { 'status': '?0' } }",
            "{ '$group': { '_id': null, 'total': { '$sum': '$amount' } } }"
    })
    Double sumAmountByStatus(String status);

    @Query(value = "{ 'timestamp': { '$gte': ?0, '$lte': ?1 } }", count = false)
    @Aggregation(pipeline = {
            "{ '$match': { 'timestamp': { '$gte': ?0, '$lte': ?1 } } }",
            "{ '$group': { '_id': null, 'total': { '$sum': '$amount' } } }"
    })
    Double sumAmountByDateRange(LocalDateTime start, LocalDateTime end);

    Page<Transaction> findByFromAccountInOrToAccountIn(
            List<String> fromAccounts,
            List<String> toAccounts,
            Pageable pageable
    );

}
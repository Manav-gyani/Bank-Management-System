package com.bank.repository;

import com.bank.model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends MongoRepository<Card, String> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByCustomerId(String customerId);
    List<Card> findByAccountId(String accountId);
    List<Card> findByStatus(Card.CardStatus status);
    Optional<Card> findByCardNumberAndCvv(String cardNumber, String cvv);
}
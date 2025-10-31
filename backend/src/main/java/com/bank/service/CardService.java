package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Card;
import com.bank.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card createCard(Card card) {
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setStatus(Card.CardStatus.ACTIVE);

        if (card.getCardType() == Card.CardType.CREDIT_CARD) {
            card.setAvailableLimit(card.getCreditLimit());
        }

        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        return cardRepository.save(card);
    }

    public Card getCardById(String id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", id));
    }

    public List<Card> getCustomerCards(String customerId) {
        return cardRepository.findByCustomerId(customerId);
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Card blockCard(String id) {
        Card card = getCardById(id);
        card.setStatus(Card.CardStatus.BLOCKED);
        card.setUpdatedAt(LocalDateTime.now());
        return cardRepository.save(card);
    }

    public Card activateCard(String id) {
        Card card = getCardById(id);
        card.setStatus(Card.CardStatus.ACTIVE);
        card.setUpdatedAt(LocalDateTime.now());
        return cardRepository.save(card);
    }

    public void deleteCard(String id) {
        Card card = getCardById(id);
        cardRepository.delete(card);
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4"); // Visa starts with 4
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}
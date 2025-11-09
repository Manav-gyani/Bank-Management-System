package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Card;
import com.bank.model.Customer;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.CardRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.UserRepository;
import com.bank.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Card createCard(Card card) {
        // Get authenticated user's email from security context
        String customerId = getCurrentCustomerId();
        String accountId = getCurrentAccountId(customerId);
        
        // Get customer details to set card holder name
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        card.setCustomerId(customerId);
        card.setAccountId(accountId);
        card.setCardHolderName(customer.getFirstName() + " " + customer.getLastName());
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setStatus(Card.CardStatus.ACTIVE);

        // Get account to set available limit for debit cards
        Account account = null;
        if (accountId != null) {
            Optional<Account> accountOpt = accountRepository.findById(accountId);
            if (accountOpt.isPresent()) {
                account = accountOpt.get();
            }
        }

        if (card.getCardType() == Card.CardType.CREDIT_CARD) {
            card.setAvailableLimit(card.getCreditLimit());
        }
        if (card.getCardType() == Card.CardType.DEBIT_CARD && account != null) {
            card.setAvailableLimit(account.getBalance());
        }

        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        return cardRepository.save(card);
    }

    public Card createCardForAccount(String accountNumber, Card.CardType cardType) {
        // Find account by account number
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        
        // Get customer details
        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", account.getCustomerId()));
        
        // Create new card
        Card card = new Card();
        card.setCustomerId(account.getCustomerId());
        card.setAccountId(account.getId());
        card.setCardType(cardType);
        card.setCardHolderName(customer.getFirstName() + " " + customer.getLastName());
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setStatus(Card.CardStatus.ACTIVE);

        if (cardType == Card.CardType.CREDIT_CARD) {
            card.setCreditLimit(java.math.BigDecimal.valueOf(50000)); // Default credit limit
            card.setAvailableLimit(java.math.BigDecimal.valueOf(50000));
        } else if (cardType == Card.CardType.DEBIT_CARD) {
            card.setAvailableLimit(account.getBalance());
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

    private String getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String email = userPrincipal.getEmail();
            
            // If email is null in UserPrincipal, try to get it from the User record
            if (email == null || email.isEmpty()) {
                String userId = userPrincipal.getId();
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    email = userOpt.get().getEmail();
                }
            }
            
            // Find customer by email
            if (email != null && !email.isEmpty()) {
                Optional<Customer> customerOpt = customerRepository.findByEmail(email);
                if (customerOpt.isPresent()) {
                    return customerOpt.get().getId();
                } else {
                    throw new ResourceNotFoundException("Customer", "email", email);
                }
            } else {
                throw new ResourceNotFoundException("Customer", "email", "Email is null or empty");
            }
        }
        throw new ResourceNotFoundException("Customer", "authentication", "No authentication found");
    }
    
    private String getCurrentAccountId(String customerId) {
        if (customerId != null) {
            // Get the first active account for the customer
            List<Account> accounts = accountRepository.findByCustomerId(customerId);
            Optional<Account> activeAccount = accounts.stream()
                    .filter(account -> account.getStatus() == Account.AccountStatus.ACTIVE)
                    .findFirst();
            
            if (activeAccount.isPresent()) {
                return activeAccount.get().getId();
            }
            
            // If no active account, return the first account
            if (!accounts.isEmpty()) {
                return accounts.get(0).getId();
            }
        }
        throw new ResourceNotFoundException("Account", "customerId", customerId);
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}
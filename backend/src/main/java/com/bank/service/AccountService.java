package com.bank.service;

import com.bank.dto.response.AccountWithCustomerDTO;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
import com.bank.util.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountNumberGenerator accountNumberGenerator;

    public Account createAccount(String customerId, Account.AccountType accountType) {
        // Validate customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        // Generate unique account number
        String accountNumber;
        int maxAttempts = 10;
        int attempts = 0;
        do {
            accountNumber = accountNumberGenerator.generateAccountNumber();
            attempts++;
            if (attempts >= maxAttempts) {
                throw new RuntimeException("Failed to generate unique account number after " + maxAttempts + " attempts");
            }
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        
        // Create and save account
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCustomerId(customerId);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setStatus(Account.AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        
        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }



    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
    }
    public AccountWithCustomerDTO getAccountWithCustomer(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));

        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", account.getCustomerId()));

        return new AccountWithCustomerDTO(account, customer);
    }

    public List<Account> getCustomerAccounts(String customerId) {
        // Validate customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        return accountRepository.findByCustomerId(customerId);
    }

    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        Account account = getAccountByNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return createTransaction(account.getId(), Transaction.TransactionType.DEPOSIT,
                amount, account.getBalance(), description);
    }

    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) {
        Account account = getAccountByNumber(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return createTransaction(account.getId(), Transaction.TransactionType.WITHDRAWAL,
                amount, account.getBalance(), description);
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber,
                         BigDecimal amount, String description) {
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        // Validate source and destination accounts exist BEFORE any transaction
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Source Account", "accountNumber", fromAccountNumber));
        
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Destination Account", "accountNumber", toAccountNumber));

        // Validate source account has sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        // Validate accounts are active
        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account is not active");
        }
        if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Destination account is not active");
        }

        // Perform the transfer (both operations will rollback if either fails)
        String transferDescription = description != null && !description.isEmpty() ? description : "Money Transfer";
        
        // Withdraw from source account
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        fromAccount.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(fromAccount);

        // Create withdrawal transaction
        createTransaction(fromAccount.getId(), Transaction.TransactionType.WITHDRAWAL,
                amount, fromAccount.getBalance(), "Transfer to " + toAccountNumber + " - " + transferDescription);

        // Deposit to destination account
        toAccount.setBalance(toAccount.getBalance().add(amount));
        toAccount.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(toAccount);

        // Create deposit transaction
        createTransaction(toAccount.getId(), Transaction.TransactionType.DEPOSIT,
                amount, toAccount.getBalance(), "Transfer from " + fromAccountNumber + " - " + transferDescription);

        System.out.println("✅ Transfer completed: ₹" + amount + " from " + fromAccountNumber + " to " + toAccountNumber);
    }

    public BigDecimal getBalance(String accountNumber) {
        return getAccountByNumber(accountNumber).getBalance();
    }

    private Transaction createTransaction(String accountId, Transaction.TransactionType type,
                                          BigDecimal amount, BigDecimal balanceAfter,
                                          String description) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAccountId(accountId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }


    public Customer getCustomerByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        return customerRepository.findById(account.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", account.getCustomerId()));
    }
}

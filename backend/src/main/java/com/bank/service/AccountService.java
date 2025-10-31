package com.bank.service;

import com.bank.dto.response.AccountWithCustomerDTO;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
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

    public Account createAccount(String customerId,Account.AccountType accountType) {
        Customer customer=customerRepository.findById(customerId).orElseThrow();
        Account account=new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setStatus(Account.AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }



    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
    public AccountWithCustomerDTO getAccountWithCustomer(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow();

        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow();

        return new AccountWithCustomerDTO(account, customer);
    }

    public List<Account> getCustomerAccounts(String customerId) {
        customerRepository.findById(customerId);
        return accountRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        Account account = getAccountByNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return createTransaction(account.getId(), Transaction.TransactionType.DEPOSIT,
                amount, account.getBalance(), description);
    }

    @Transactional
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
        withdraw(fromAccountNumber, amount, "Transfer to " + toAccountNumber);
        deposit(toAccountNumber, amount, "Transfer from " + fromAccountNumber);
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

    private String generateAccountNumber() {
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }

    public Customer getCustomerByAccountNumber(String accountNumber) {
        Account account=accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        return customerRepository.findById(account.getCustomerId()).orElseThrow();
    }
}

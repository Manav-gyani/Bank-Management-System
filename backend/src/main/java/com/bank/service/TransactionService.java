package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Component
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    Transaction transaction;

    public Transaction transferAmount(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {

        // ✅ Get logged-in user details
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String em=loggedInUser.getEmail();
        Customer customer= customerRepository.findByEmail(em).orElseThrow(() -> new ResourceNotFoundException("No User Exists"));
        // 1️⃣ Fetch both accounts
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // ✅ Check if logged-in user owns the fromAccount
        if (!fromAccount.getCustomerId().equals(customer.getId())) {
            throw new RuntimeException("❌ You are not the account holder of this account.");
        }

        // ✅ Check sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("❌ Insufficient balance.");
        }


        // 2️⃣ Check balance
        // Deduct from sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        // Add to receiver
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);

        Transaction senderTxn = new Transaction();
        senderTxn.setAccountId(fromAccount.getId());
        senderTxn.setFromAccount(fromAccountNumber);
        senderTxn.setToAccount(toAccountNumber);
        senderTxn.setAmount(amount);
        senderTxn.setType(Transaction.TransactionType.TRANSFER);
        senderTxn.setBalanceAfter(fromAccount.getBalance());
        senderTxn.setStatus(Transaction.TransactionStatus.COMPLETED);
        senderTxn.setDescription("Transfer to " + toAccountNumber);

        transactionRepository.save(senderTxn);

        //Receiver Transaction
        Transaction receiverTxn = new Transaction();
        receiverTxn.setAccountId(toAccount.getId());
        receiverTxn.setFromAccount(fromAccountNumber);
        receiverTxn.setToAccount(toAccountNumber);
        receiverTxn.setAmount(amount);
        receiverTxn.setType(Transaction.TransactionType.TRANSFER);
        receiverTxn.setBalanceAfter(toAccount.getBalance());
        receiverTxn.setStatus(Transaction.TransactionStatus.COMPLETED);
        receiverTxn.setDescription("Received from " + fromAccountNumber);

        transactionRepository.save(receiverTxn);

//        if (fromAccount.getBalance().compareTo(amount) < 0) {
//            transaction.setStatus(Transaction.TransactionStatus.FAILED);
//            transactionRepository.save(transaction);
//            throw new RuntimeException("Insufficient balance in source account");
//        }


//        // 5️⃣ Create and save transaction record
//        transaction.setFromAccount(fromAccountNumber);
//        transaction.setToAccount(toAccountNumber);
//        transaction.setAmount(amount);
//        transaction.setTimestamp(LocalDateTime.now());
//        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
//
//        transactionRepository.save(transaction);
//
//        return transaction;
        return senderTxn;
    }

    public List<Transaction> getTransactionByAccountId(String accountNumber){
        return transactionRepository.findByAccountId(accountNumber);
    }
    public Transaction getTransactionById(String id){
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }
    public List<Transaction> getTransactionByDate(String accountNumber, LocalDateTime startDate,LocalDateTime endDate){
        return transactionRepository.findByAccountIdAndTimestampBetween(accountNumber,startDate,endDate);
    }
    
    public List<Transaction> getAccountTransactions(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    
    public List<Transaction> getAccountTransactionsByDateRange(String accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndTimestampBetween(accountId, startDate, endDate);
    }
    
    public List<Transaction> getTransactionByTransactionId(String transactionId){
        return transactionRepository.findByTransactionId(transactionId);
    }
    
    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public void deleteTransaction(String transactionId){
        Transaction transaction = getTransactionById(transactionId);
        transactionRepository.delete(transaction);
    }
}

package com.bank.service;

import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

import java.sql.DatabaseMetaData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> getTransactionByAccountId(String accountNumber){
        return transactionRepository.findByAccountId(accountNumber);
    }
    public Transaction getTransactionById(String id){
        return transactionRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
    public List<Transaction> getTransactionByDate(String accountNumber, LocalDateTime startDate,LocalDateTime endDate){
        return transactionRepository.findByAccountIdAndTimestampBetween(accountNumber,startDate,endDate);
    }
    public List<Transaction> getTransactionByTransactionId(String transactionId){
        return transactionRepository.findByTransactionId(transactionId);
    }
    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public void deleteTransaction(String transactionId){
        Transaction transaction= getTransactionById(transactionId);
        transactionRepository.delete(transaction);
    }
}

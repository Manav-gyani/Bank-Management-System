package com.bank.dto;

import lombok.Data;
import com.bank.model.Account;
import com.bank.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDetailsDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private List<Account> accounts;
    private List<Transaction> recentTransactions;
}
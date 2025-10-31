package com.bank.controller;

import com.bank.dto.response.AccountWithCustomerDTO;
//import com.bank.dto.response.ApiResponse;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create account for a customer
    @PostMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Account> createAccountForCustomer(
            @PathVariable String customerId,
            @RequestBody Map<String, String> request) {
        Account.AccountType accountType = Account.AccountType.valueOf(
                request.get("accountType").toUpperCase()
        );
        Account account = accountService.createAccount(customerId, accountType);
        return ResponseEntity.ok(account);
    }

    // Get account with customer details
    @GetMapping("/{accountNumber}/with-customer")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<AccountWithCustomerDTO> getAccountWithCustomer(
            @PathVariable String accountNumber) {
        AccountWithCustomerDTO response = accountService.getAccountWithCustomer(accountNumber);
        return ResponseEntity.ok(response);
    }

    // Get customer details by account number
    @GetMapping("/{accountNumber}/customer")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Customer> getCustomerByAccount(
            @PathVariable String accountNumber) {
        Customer customer = accountService.getCustomerByAccountNumber(accountNumber);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Account>> getCustomerAccounts(@PathVariable String customerId) {
        return ResponseEntity.ok(accountService.getCustomerAccounts(customerId));
    }

    @GetMapping("/{accountNumber}/balance")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, BigDecimal>> getBalance(@PathVariable String accountNumber) {
        BigDecimal balance = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @PostMapping("/{accountNumber}/deposit")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Transaction> deposit(
            @PathVariable String accountNumber,
            @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");
        return ResponseEntity.ok(accountService.deposit(accountNumber, amount, description));
    }

    @PostMapping("/{accountNumber}/withdraw")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Transaction> withdraw(
            @PathVariable String accountNumber,
            @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");
        return ResponseEntity.ok(accountService.withdraw(accountNumber, amount, description));
    }

//    @PostMapping("/transfer")
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
//    public ResponseEntity<ApiResponse> transfer(@RequestBody Map<String, Object> request) {
//        String fromAccount = (String) request.get("fromAccount");
//        String toAccount = (String) request.get("toAccount");
//        BigDecimal amount = new BigDecimal(request.get("amount").toString());
//        String description = (String) request.get("description");
//
//        accountService.transfer(fromAccount, toAccount, amount, description);
//        return ResponseEntity.ok(new ApiResponse(true, "Transfer successful"));
//    }
}
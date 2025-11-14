package com.bank.config;

import com.bank.model.*;
import com.bank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedDemoData();
    }

    private void seedAdminUser() {
        // Check if admin already exists
        if (adminRepository.findByUsername("admin").isPresent()) {
            System.out.println("✅ Admin user already exists");
            return;
        }

        // Create default admin user
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setEmail("admin@bank.com");
        admin.setPassword(passwordEncoder.encode("admin123")); // Default password
        admin.setCreatedAt(LocalDateTime.now());
        
        adminRepository.save(admin);
        
        System.out.println("✅ Default admin user created successfully!");
        System.out.println("   Username: admin");
        System.out.println("   Password: admin123");
        System.out.println("   ⚠️  Please change the password after first login!");
    }

    private void seedDemoData() {
        // Check if demo data already exists
        if (userRepository.findByUsername("johndoe").isPresent()) {
            System.out.println("✅ Demo data already exists");
            return;
        }

        System.out.println("🔄 Seeding demo data...");

        // Create demo customer
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+919876543210");
        customer.setAddress("123 Main Street, Vadodara, Gujarat, India");
        customer.setAadharNumber("1234-5678-9012");
        customer.setPanNumber("ABCDE1234F");
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        // Create user for customer
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setPassword(passwordEncoder.encode("admin123"));
        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.CUSTOMER);
        user.setRoles(roles);
        user.setEnabled(true);
        userRepository.save(user);

        // Create savings account
        Account savingsAccount = new Account();
        savingsAccount.setAccountNumber("1000000001");
        savingsAccount.setCustomerId(customer.getId());
        savingsAccount.setAccountType(Account.AccountType.SAVINGS);
        savingsAccount.setBalance(new BigDecimal("45000.50"));
        savingsAccount.setCurrency("INR");
        savingsAccount.setStatus(Account.AccountStatus.ACTIVE);
        savingsAccount.setCreatedAt(LocalDateTime.now());
        savingsAccount.setUpdatedAt(LocalDateTime.now());
        savingsAccount = accountRepository.save(savingsAccount);

        // Create current account
        Account currentAccount = new Account();
        currentAccount.setAccountNumber("2000000001");
        currentAccount.setCustomerId(customer.getId());
        currentAccount.setAccountType(Account.AccountType.CURRENT);
        currentAccount.setBalance(new BigDecimal("120000.00"));
        currentAccount.setCurrency("INR");
        currentAccount.setStatus(Account.AccountStatus.ACTIVE);
        currentAccount.setCreatedAt(LocalDateTime.now());
        currentAccount.setUpdatedAt(LocalDateTime.now());
        currentAccount = accountRepository.save(currentAccount);

        // Create demo transactions
        Transaction t1 = new Transaction();
        t1.setAccountId(savingsAccount.getId());
        t1.setType(Transaction.TransactionType.DEPOSIT);
        t1.setAmount(new BigDecimal("45000.50"));
        t1.setBalanceAfter(new BigDecimal("45000.50"));
        t1.setDescription("Initial Deposit");
        t1.setStatus(Transaction.TransactionStatus.COMPLETED);
        t1.setTimestamp(LocalDateTime.now().minusDays(30));
        transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setAccountId(currentAccount.getId());
        t2.setType(Transaction.TransactionType.DEPOSIT);
        t2.setAmount(new BigDecimal("120000.00"));
        t2.setBalanceAfter(new BigDecimal("120000.00"));
        t2.setDescription("Initial Deposit");
        t2.setStatus(Transaction.TransactionStatus.COMPLETED);
        t2.setTimestamp(LocalDateTime.now().minusDays(30));
        transactionRepository.save(t2);

        Transaction t3 = new Transaction();
        t3.setAccountId(savingsAccount.getId());
        t3.setType(Transaction.TransactionType.WITHDRAWAL);
        t3.setAmount(new BigDecimal("1000.00"));
        t3.setBalanceAfter(new BigDecimal("44000.50"));
        t3.setDescription("ATM Withdrawal");
        t3.setStatus(Transaction.TransactionStatus.COMPLETED);
        t3.setTimestamp(LocalDateTime.now().minusDays(15));
        transactionRepository.save(t3);

        Transaction t4 = new Transaction();
        t4.setAccountId(savingsAccount.getId());
        t4.setType(Transaction.TransactionType.DEPOSIT);
        t4.setAmount(new BigDecimal("1000.00"));
        t4.setBalanceAfter(new BigDecimal("45000.50"));
        t4.setDescription("Salary Credit");
        t4.setStatus(Transaction.TransactionStatus.COMPLETED);
        t4.setTimestamp(LocalDateTime.now().minusDays(5));
        transactionRepository.save(t4);

        System.out.println("✅ Demo data seeded successfully!");
        System.out.println("   Customer: John Doe (john.doe@example.com)");
        System.out.println("   Username: johndoe");
        System.out.println("   Password: admin123");
        System.out.println("   Accounts: 2 (Savings + Current)");
        System.out.println("   Transactions: 4");
    }
}

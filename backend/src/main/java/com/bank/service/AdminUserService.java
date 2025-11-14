package com.bank.service;
import com.bank.dto.UserDetailsDto;
import com.bank.model.User;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.UserRepository;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Page<User> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUsernameContainingOrEmailContaining(searchTerm, searchTerm, pageable);
    }

    public UserDetailsDto getUserDetails(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsDto dto = new UserDetailsDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
//        dto.setFirstName(user.());
//        dto.setLastName(user.getLastName());
//        dto.setPhoneNumber(user.getPhoneNumber());
//        dto.setStatus(user.getStatus());
//        dto.setCreatedAt(user.getCreatedAt());
//        dto.setLastLogin(user.getLastLogin());

        // Get accounts
        List<Account> accounts = accountRepository.findByCustomerId(user.getId());
        dto.setAccounts(accounts);

        // Get recent transactions
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<String> accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());

        if (!accountIds.isEmpty()) {
            List<Transaction> transactions = transactionRepository
                    .findByFromAccountInOrToAccountIn(accountIds, accountIds, pageable)
                    .getContent();
            dto.setRecentTransactions(transactions);
        }

        return dto;
    }

    public void suspendUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void activateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }
}

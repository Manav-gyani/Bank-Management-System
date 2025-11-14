package com.bank.service;

import com.bank.dto.DashboardStatsDto;
import com.bank.repository.UserRepository;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.findAll().stream().filter(user -> user.isEnabled()).count());
        stats.setSuspendedUsers(userRepository.findAll().stream().filter(user -> !user.isEnabled()).count());
        stats.setTotalAccounts(accountRepository.count());
        stats.setTotalTransactions(transactionRepository.count());

        // Calculate volumes
        Double totalVolume = transactionRepository.sumAmountByStatus("COMPLETED");
        stats.setTotalTransactionVolume(totalVolume != null ? totalVolume : 0.0);

        stats.setTodayTransactions(
                transactionRepository.countByTimestampBetween(todayStart, todayEnd)
        );

        Double todayVolume = transactionRepository.sumAmountByDateRange(todayStart, todayEnd);
        stats.setTodayVolume(todayVolume != null ? todayVolume : 0.0);

        return stats;
    }
}
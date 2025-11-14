package com.bank.dto;
import lombok.Data;

@Data
public class DashboardStatsDto {
    private long totalUsers;
    private long activeUsers;
    private long suspendedUsers;
    private long totalAccounts;
    private long totalTransactions;
    private double totalTransactionVolume;
    private long todayTransactions;
    private double todayVolume;
}

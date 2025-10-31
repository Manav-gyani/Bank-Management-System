package com.bank.util;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    public String formatDateForDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_FORMATTER) : null;
    }

    public LocalDate parseDate(String dateString) {
        return dateString != null ? LocalDate.parse(dateString, DATE_FORMATTER) : null;
    }

    public LocalDateTime parseDateTime(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER) : null;
    }

    public Date toDate(LocalDateTime localDateTime) {
        return localDateTime != null ?
                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public LocalDateTime toLocalDateTime(Date date) {
        return date != null ?
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    public long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public long monthsBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    public LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    public LocalDate addYears(LocalDate date, int years) {
        return date.plusYears(years);
    }

    public boolean isExpired(LocalDate expiryDate) {
        return expiryDate.isBefore(LocalDate.now());
    }

    public boolean isToday(LocalDateTime dateTime) {
        return dateTime.toLocalDate().isEqual(LocalDate.now());
    }

    public LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    public String getFinancialYear() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        if (today.getMonthValue() >= 4) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
}

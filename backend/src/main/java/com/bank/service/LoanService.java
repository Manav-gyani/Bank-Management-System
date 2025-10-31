package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Loan;
import com.bank.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public Loan createLoan(Loan loan) {
        loan.setLoanNumber("LN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        loan.setOutstandingAmount(loan.getPrincipalAmount());
        loan.setStatus(Loan.LoanStatus.PENDING);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());

        // Calculate EMI
        BigDecimal monthlyRate = loan.getInterestRate().divide(BigDecimal.valueOf(1200));
        BigDecimal emi = loan.getPrincipalAmount()
                .multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(Math.pow(1 + monthlyRate.doubleValue(), loan.getTenureMonths())))
                .divide(BigDecimal.valueOf(Math.pow(1 + monthlyRate.doubleValue(), loan.getTenureMonths()) - 1), 2, BigDecimal.ROUND_HALF_UP);
        loan.setMonthlyEmi(emi);

        return loanRepository.save(loan);
    }

    public Loan getLoanById(String id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", id));
    }

    public List<Loan> getCustomerLoans(String customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan updateLoanStatus(String id, Loan.LoanStatus status) {
        Loan loan = getLoanById(id);
        loan.setStatus(status);
        loan.setUpdatedAt(LocalDateTime.now());

        if (status == Loan.LoanStatus.DISBURSED) {
            loan.setDisbursementDate(LocalDateTime.now());
            loan.setNextDueDate(LocalDateTime.now().plusMonths(1));
        }

        return loanRepository.save(loan);
    }

    public void deleteLoan(String id) {
        Loan loan = getLoanById(id);
        loanRepository.delete(loan);
    }
}
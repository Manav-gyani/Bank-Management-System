package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.UserRepository;
import com.bank.security.UserPrincipal;
import com.bank.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    public Loan createLoan(Loan loan) {
        String customerId=getCurrentCustomerId();
        String accountId=getCurrentAccountId(customerId);
        loan.setCustomerId(customerId);
        loan.setAccountId(accountId);
        loan.setLoanNumber("LN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        loan.setOutstandingAmount(loan.getPrincipalAmount());
        loan.setStatus(Loan.LoanStatus.PENDING);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());

        // Set interest rate based on loan type if not provided
        if (loan.getInterestRate() == null) {
            loan.setInterestRate(getInterestRateForLoanType(loan.getLoanType()));
        }

        // Calculate EMI
        BigDecimal monthlyRate = loan.getInterestRate().divide(BigDecimal.valueOf(1200), RoundingMode.HALF_UP);
        BigDecimal emi = loan.getPrincipalAmount()
                .multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(Math.pow(1 + monthlyRate.doubleValue(), loan.getTenureMonths())))
                .divide(BigDecimal.valueOf(Math.pow(1 + monthlyRate.doubleValue(), loan.getTenureMonths()) - 1), 2, java.math.RoundingMode.HALF_UP);
        loan.setMonthlyEmi(emi);

        Loan saveLoan = loanRepository.save(loan);
        new Thread(()->{
           try{
               Thread.sleep(60000); // wait for 60Seconds
                approveLoan(saveLoan.getId());
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
        }).start();

        return saveLoan;
    }

    private BigDecimal getInterestRateForLoanType(Loan.LoanType loanType) {
        if (loanType == null) {
            throw new IllegalArgumentException("Loan type cannot be null");
        }
        
        return switch (loanType) {
            case HOME_LOAN -> Constants.LOAN_INTEREST_RATE_HOME;
            case PERSONAL_LOAN -> Constants.LOAN_INTEREST_RATE_PERSONAL;
            case CAR_LOAN -> Constants.LOAN_INTEREST_RATE_CAR;
            case EDUCATION_LOAN -> Constants.LOAN_INTEREST_RATE_EDUCATION;
            case BUSINESS_LOAN -> Constants.LOAN_INTEREST_RATE_BUSINESS;
        };
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
    private String getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String email = userPrincipal.getEmail();

            // If email is null in UserPrincipal, try to get it from the User record
            if (email == null || email.isEmpty()) {
                String userId = userPrincipal.getId();
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    email = userOpt.get().getEmail();
                }
            }

            // Find customer by email
            if (email != null && !email.isEmpty()) {
                Optional<Customer> customerOpt = customerRepository.findByEmail(email);
                if (customerOpt.isPresent()) {
                    return customerOpt.get().getId();
                } else {
                    throw new ResourceNotFoundException("Customer", "email", email);
                }
            } else {
                throw new ResourceNotFoundException("Customer", "email", "Email is null or empty");
            }
        }
        throw new ResourceNotFoundException("Customer", "authentication", "No authentication found");
    }
    private String getCurrentAccountId(String customerId) {
        if (customerId != null) {
            // Get the first active account for the customer
            List<Account> accounts = accountRepository.findByCustomerId(customerId);
            Optional<Account> activeAccount = accounts.stream()
                    .filter(account -> account.getStatus() == Account.AccountStatus.ACTIVE)
                    .findFirst();

            if (activeAccount.isPresent()) {
                return activeAccount.get().getId();
            }

            // If no active account, return the first account
            if (!accounts.isEmpty()) {
                return accounts.get(0).getId();
            }
        }
        throw new ResourceNotFoundException("Account", "customerId", customerId);
    }

    public Loan approveLoan(String id){
        Loan loan =loanRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No Loan id found"));

        // Simulate credit score (for demo)
//        if (loan.getCreditScore() == 0) {
            Integer simulatedScore = 600 + new Random().nextInt(150); // 600–750
            loan.setCreditScore(simulatedScore);
//        }

        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        // EMI = [P * R * (1+R)^N] / [(1+R)^N - 1]
        BigDecimal onePlusRPowerN = BigDecimal.valueOf(
                Math.pow(1 + monthlyRate.doubleValue(), loan.getTenureMonths())
        );

        BigDecimal numerator = loan.getPrincipalAmount()
                .multiply(monthlyRate)
                .multiply(onePlusRPowerN);

        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);

        BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        boolean creditScoreOk=loan.getCreditScore()>=600;
        boolean incomeOk=loan.getMonthlyIncome().compareTo(emi.multiply(BigDecimal.valueOf(3))) >=0;
        boolean lowDebtOk=loan.getExistingDebt().divide(loan.getMonthlyIncome(),2,RoundingMode.HALF_UP)
                .compareTo(BigDecimal.valueOf(0.4)) <= 0;
        boolean collateralOk=loan.getCollateralValue().compareTo(loan.getPrincipalAmount()) >= 0;

        // ✅ Flexible logic for new users
        if (!creditScoreOk && loan.getCreditScore() < 650) {
            // If credit is low, allow loan only if collateral + income are strong
            if (collateralOk && loan.getMonthlyIncome().compareTo(emi.multiply(BigDecimal.valueOf(4))) >= 0) {
                creditScoreOk = true; // treat as acceptable
            }
        }

        // ✅ Decision
        if (creditScoreOk && incomeOk && lowDebtOk && collateralOk) {
//            Loan.LoanStatus approved = Loan.LoanStatus.APPROVED;
            loan.setStatus(Loan.LoanStatus.APPROVED);
            loan.setApprovalDate(LocalDateTime.now());
        } else {
//            loan.setStatus("REJECTED");
            loan.setStatus(Loan.LoanStatus.REJECTED);
        }
        return loanRepository.save(loan);
    }
}
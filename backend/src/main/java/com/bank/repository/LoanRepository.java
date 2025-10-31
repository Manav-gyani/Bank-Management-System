package com.bank.repository;

import com.bank.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {
    Optional<Loan> findByLoanNumber(String loanNumber);
    List<Loan> findByCustomerId(String customerId);
    List<Loan> findByAccountId(String accountId);
    List<Loan> findByStatus(Loan.LoanStatus status);
    List<Loan> findByCustomerIdAndStatus(String customerId, Loan.LoanStatus status);
}

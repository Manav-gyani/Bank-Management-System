package com.bank.repository;

import com.bank.model.Beneficiary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends MongoRepository<Beneficiary, String> {
    List<Beneficiary> findByCustomerId(String customerId);
    Optional<Beneficiary> findByCustomerIdAndAccountNumber(String customerId, String accountNumber);
    List<Beneficiary> findByCustomerIdAndIsVerified(String customerId, Boolean isVerified);
}
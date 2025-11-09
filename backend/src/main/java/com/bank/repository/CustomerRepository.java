package com.bank.repository;

import com.bank.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findAllByEmail(String email); // Add method to find all customers with same email
    Optional<Customer> findByAadharNumber(String aadharNumber);
    Optional<Customer> findByPanNumber(String panNumber);
}
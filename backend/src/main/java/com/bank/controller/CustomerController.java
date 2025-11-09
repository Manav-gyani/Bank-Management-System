package com.bank.controller;

import com.bank.model.Customer;
import com.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String id,
            @RequestBody Customer customer) {
        Customer customer1=customerService.getCustomerById(id);
        if (customer1!=null){
            customer1.setFirstName(customer.getFirstName()!=null&&!customer.getFirstName().equals("")?customer.getFirstName(): customer1.getFirstName());
            customer1.setLastName(customer.getLastName()!=null&&!customer.getLastName().equals("")?customer.getLastName(): customer1.getLastName());
            customer1.setEmail(customer.getEmail()!=null&&!customer.getEmail().equals("")?customer.getEmail(): customer1.getEmail());
            customer1.setPhone(customer.getPhone()!=null&&!customer.getPhone().equals("")?customer.getPhone(): customer1.getPhone());
            customer1.setAddress(customer.getAddress()!=null&&!customer.getAddress().equals("")?customer.getAddress(): customer1.getAddress());
            customer1.setAadharNumber(customer.getAadharNumber()!=null&&!customer.getAadharNumber().equals("")?customer.getAadharNumber(): customer1.getAadharNumber());
            customer1.setPanNumber(customer.getPanNumber()!=null&&!customer.getPanNumber().equals("")?customer.getPanNumber(): customer1.getPanNumber());
            customerService.createCustomer(customer1);
            return new ResponseEntity<>(HttpStatus.OK);
        }
//        customerService.createCustomer(customer);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
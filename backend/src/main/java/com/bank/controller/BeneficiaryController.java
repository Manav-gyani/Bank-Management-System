package com.bank.controller;

import com.bank.dto.response.ApiResponse;
import com.bank.model.Beneficiary;
import com.bank.service.BeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@CrossOrigin(origins = "*")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Beneficiary> addBeneficiary(@Valid @RequestBody Beneficiary beneficiary) {
        return ResponseEntity.ok(beneficiaryService.addBeneficiary(beneficiary));
    }

    @PostMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Beneficiary> addBeneficiaryForCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody Beneficiary beneficiary) {
        beneficiary.setCustomerId(customerId);
        return ResponseEntity.ok(beneficiaryService.addBeneficiary(beneficiary));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Beneficiary>> getCustomerBeneficiaries(@PathVariable String customerId) {
        return ResponseEntity.ok(beneficiaryService.getCustomerBeneficiaries(customerId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Beneficiary> getBeneficiaryById(@PathVariable String id) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiaryById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Beneficiary> updateBeneficiary(
            @PathVariable String id,
            @Valid @RequestBody Beneficiary beneficiary) {
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, beneficiary));
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Beneficiary> verifyBeneficiary(@PathVariable String id) {
        return ResponseEntity.ok(beneficiaryService.verifyBeneficiary(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteBeneficiary(@PathVariable String id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok(new ApiResponse(true, "Beneficiary deleted successfully"));
    }
}

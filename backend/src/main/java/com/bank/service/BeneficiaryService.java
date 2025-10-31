package com.bank.service;

import com.bank.exception.BadRequestException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Beneficiary;
import com.bank.repository.BeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    public Beneficiary addBeneficiary(Beneficiary beneficiary) {
        // Check if beneficiary already exists
        beneficiaryRepository.findByCustomerIdAndAccountNumber(
                beneficiary.getCustomerId(),
                beneficiary.getAccountNumber()
        ).ifPresent(b -> {
            throw new BadRequestException("Beneficiary with this account number already exists");
        });

        beneficiary.setIsVerified(false);
        beneficiary.setCreatedAt(LocalDateTime.now());
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getCustomerBeneficiaries(String customerId) {
        return beneficiaryRepository.findByCustomerId(customerId);
    }

    public Beneficiary getBeneficiaryById(String id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", id));
    }

    public Beneficiary updateBeneficiary(String id, Beneficiary beneficiaryDetails) {
        Beneficiary beneficiary = getBeneficiaryById(id);
        beneficiary.setBeneficiaryName(beneficiaryDetails.getBeneficiaryName());
        beneficiary.setNickname(beneficiaryDetails.getNickname());
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(String id) {
        Beneficiary beneficiary = getBeneficiaryById(id);
        beneficiaryRepository.delete(beneficiary);
    }

    public Beneficiary verifyBeneficiary(String id) {
        Beneficiary beneficiary = getBeneficiaryById(id);
        beneficiary.setIsVerified(true);
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return beneficiaryRepository.save(beneficiary);
    }
}
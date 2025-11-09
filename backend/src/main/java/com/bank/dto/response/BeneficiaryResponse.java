package com.bank.dto.response;

import com.bank.model.Beneficiary;
import lombok.Data;

@Data
public class BeneficiaryResponse {
    private String id;
    private String customerId;
    private String beneficiaryName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String nickname;
    private Boolean isVerified;

    public BeneficiaryResponse(Beneficiary beneficiary) {
        this.id = beneficiary.getId();
        this.customerId = beneficiary.getCustomerId();
        this.beneficiaryName = beneficiary.getBeneficiaryName();
        this.accountNumber = beneficiary.getAccountNumber();
        this.ifscCode = beneficiary.getIfscCode();
        this.bankName = beneficiary.getBankName();
        this.nickname = beneficiary.getNickname();
        this.isVerified = beneficiary.getIsVerified();
    }
}

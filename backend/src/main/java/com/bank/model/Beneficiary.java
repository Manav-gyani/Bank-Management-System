package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Document(collection = "beneficiaries")
public class Beneficiary {
    @Id
    private String id;
    private String customerId;
    private String beneficiaryName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String nickname;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

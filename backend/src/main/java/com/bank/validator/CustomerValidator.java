package com.bank.validator;

import com.bank.exception.BadRequestException;
import com.bank.model.Customer;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class CustomerValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,13}$"
    );

    private static final Pattern AADHAR_PATTERN = Pattern.compile(
            "^[0-9]{4}-[0-9]{4}-[0-9]{4}$"
    );

    private static final Pattern PAN_PATTERN = Pattern.compile(
            "^[A-Z]{5}[0-9]{4}[A-Z]{1}$"
    );

    public void validateCustomerCreation(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First name is required");
        }

        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Last name is required");
        }

        validateEmail(customer.getEmail());
        validatePhone(customer.getPhone());
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new BadRequestException("Phone number is required");
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BadRequestException("Invalid phone number format");
        }
    }

    public void validateAadhar(String aadhar) {
        if (aadhar != null && !aadhar.trim().isEmpty()) {
            if (!AADHAR_PATTERN.matcher(aadhar).matches()) {
                throw new BadRequestException("Invalid Aadhar format. Expected: XXXX-XXXX-XXXX");
            }
        }
    }

    public void validatePan(String pan) {
        if (pan != null && !pan.trim().isEmpty()) {
            if (!PAN_PATTERN.matcher(pan).matches()) {
                throw new BadRequestException("Invalid PAN format. Expected: ABCDE1234F");
            }
        }
    }

    public void validateCustomerUpdate(Customer customer) {
        if (customer.getEmail() != null) {
            validateEmail(customer.getEmail());
        }

        if (customer.getPhone() != null) {
            validatePhone(customer.getPhone());
        }

        if (customer.getAadharNumber() != null) {
            validateAadhar(customer.getAadharNumber());
        }

        if (customer.getPanNumber() != null) {
            validatePan(customer.getPanNumber());
        }
    }
}
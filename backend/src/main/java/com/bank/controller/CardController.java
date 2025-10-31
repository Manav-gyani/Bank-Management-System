package com.bank.controller;

import com.bank.dto.response.ApiResponse;
import com.bank.model.Card;
import com.bank.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Card> createCard(@Valid @RequestBody Card card) {
        return ResponseEntity.ok(cardService.createCard(card));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Card> getCardById(@PathVariable String id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Card>> getCustomerCards(@PathVariable String customerId) {
        return ResponseEntity.ok(cardService.getCustomerCards(customerId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.getAllCards());
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Card> blockCard(@PathVariable String id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Card> activateCard(@PathVariable String id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCard(@PathVariable String id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(new ApiResponse(true, "Card deleted successfully"));
    }
}
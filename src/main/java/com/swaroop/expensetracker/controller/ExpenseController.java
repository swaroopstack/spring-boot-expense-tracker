package com.swaroop.expensetracker.controller;

import com.swaroop.expensetracker.dto.ExpenseRequest;
import com.swaroop.expensetracker.dto.ExpenseResponse;
import com.swaroop.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints of the expense tracker.
 *
 * <p>The controller only translates HTTP to service calls. All validation and
 * business rules live in the DTO and service layers.</p>
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /** POST /api/expenses - creates an expense and answers with 201 Created. */
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse created = expenseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

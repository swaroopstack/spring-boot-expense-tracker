package com.swaroop.expensetracker.dto;

import com.swaroop.expensetracker.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data returned to the client. Keeping entities out of the API response means
 * we can change the database model without breaking the API contract.
 */
public record ExpenseResponse(
        Long id,
        String title,
        BigDecimal amount,
        String category,
        String description,
        LocalDate date
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getDate()
        );
    }
}

package com.swaroop.expensetracker.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data sent by the client when creating or updating an expense.
 *
 * <p>A record is used on purpose: the payload is immutable and the constructor
 * makes every field explicit. Jakarta Bean Validation annotations are checked
 * by {@code @Valid} in the controller, so invalid requests never reach the
 * service layer.</p>
 */
public record ExpenseRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be at most 100 characters")
        String title,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimals")
        BigDecimal amount,

        @NotBlank(message = "Category is required")
        @Size(max = 50, message = "Category must be at most 50 characters")
        String category,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @NotNull(message = "Date is required (format yyyy-MM-dd)")
        LocalDate date
) {
}

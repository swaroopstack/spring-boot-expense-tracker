package com.swaroop.expensetracker.dto;

import java.math.BigDecimal;

/**
 * Response of {@code GET /api/expenses/total}.
 *
 * @param totalAmount        sum of all expense amounts
 * @param numberOfExpenses   how many expenses were added together
 */
public record TotalExpenseResponse(BigDecimal totalAmount, long numberOfExpenses) {
}

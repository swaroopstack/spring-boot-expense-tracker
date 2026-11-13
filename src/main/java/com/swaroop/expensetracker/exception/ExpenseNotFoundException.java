package com.swaroop.expensetracker.exception;

/**
 * Thrown when an expense with the requested id does not exist.
 *
 * <p>The {@link GlobalExceptionHandler} converts it into an HTTP 404 response.</p>
 */
public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense not found with id: " + id);
    }
}

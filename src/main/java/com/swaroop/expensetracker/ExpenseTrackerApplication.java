package com.swaroop.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Personal Expense Tracker REST API.
 *
 * <p>Run this class (or {@code mvn spring-boot:run}) and the API starts on
 * http://localhost:8080. All endpoints are available under /api/expenses.</p>
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}

package com.swaroop.expensetracker.repository;

import com.swaroop.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Database access for {@link Expense}.
 *
 * <p>Spring Data JPA generates the implementation at runtime, so no SQL is
 * needed for the standard CRUD operations.</p>
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Returns the sum of every expense amount.
     *
     * <p>COALESCE keeps the result at 0 instead of null when the table is
     * empty, which keeps the /total endpoint simple for callers.</p>
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal sumOfAmounts();
}

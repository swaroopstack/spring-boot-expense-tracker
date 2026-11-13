package com.swaroop.expensetracker.service;

import com.swaroop.expensetracker.dto.ExpenseRequest;
import com.swaroop.expensetracker.dto.ExpenseResponse;
import com.swaroop.expensetracker.dto.TotalExpenseResponse;
import com.swaroop.expensetracker.entity.Expense;
import com.swaroop.expensetracker.exception.ExpenseNotFoundException;
import com.swaroop.expensetracker.repository.ExpenseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for expenses.
 *
 * <p>The service is the only layer that knows how an {@link Expense} is built
 * or updated. It never deals with HTTP and never returns entities, which keeps
 * controller and repository independent from each other.</p>
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /** Stores a new expense and returns it with the generated id. */
    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = new Expense();
        applyRequest(expense, request);
        Expense saved = expenseRepository.save(expense);
        return ExpenseResponse.from(saved);
    }

    /** Returns every expense, newest date first. */
    @Transactional(readOnly = true)
    public List<ExpenseResponse> findAll() {
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id"));
        return expenseRepository.findAll(newestFirst)
                .stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    /** Returns one expense or throws a 404 exception when it does not exist. */
    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return ExpenseResponse.from(getExpenseOrThrow(id));
    }

    /** Replaces every field of an existing expense. */
    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = getExpenseOrThrow(id);
        applyRequest(expense, request);
        Expense updated = expenseRepository.save(expense);
        return ExpenseResponse.from(updated);
    }

    /** Deletes an expense and fails with 404 when the id is unknown. */
    @Transactional
    public void delete(Long id) {
        Expense expense = getExpenseOrThrow(id);
        expenseRepository.delete(expense);
    }

    /** Sum and count of all expenses. */
    @Transactional(readOnly = true)
    public TotalExpenseResponse getTotal() {
        return new TotalExpenseResponse(expenseRepository.sumOfAmounts(), expenseRepository.count());
    }

    private Expense getExpenseOrThrow(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    private void applyRequest(Expense expense, ExpenseRequest request) {
        expense.setTitle(request.title().trim());
        expense.setAmount(request.amount());
        expense.setCategory(request.category().trim());
        expense.setDescription(request.description() == null ? null : request.description().trim());
        expense.setDate(request.date());
    }
}

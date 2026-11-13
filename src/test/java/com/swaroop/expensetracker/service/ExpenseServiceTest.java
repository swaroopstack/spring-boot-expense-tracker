package com.swaroop.expensetracker.service;

import com.swaroop.expensetracker.dto.ExpenseRequest;
import com.swaroop.expensetracker.dto.ExpenseResponse;
import com.swaroop.expensetracker.dto.TotalExpenseResponse;
import com.swaroop.expensetracker.entity.Expense;
import com.swaroop.expensetracker.exception.ExpenseNotFoundException;
import com.swaroop.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExpenseService}. The repository is mocked, so these
 * tests are fast and do not need MySQL.
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    private static final LocalDate EXPENSE_DATE = LocalDate.of(2026, 11, 10);

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void createShouldSaveExpenseAndReturnGeneratedId() {
        ExpenseRequest request = new ExpenseRequest(
                "Lunch", new BigDecimal("250.00"), "Food", "Team lunch", EXPENSE_DATE);

        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            expense.setId(1L);
            return expense;
        });

        ExpenseResponse response = expenseService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Lunch");
        assertThat(response.amount()).isEqualByComparingTo("250.00");
        assertThat(response.category()).isEqualTo("Food");
        assertThat(response.description()).isEqualTo("Team lunch");
        assertThat(response.date()).isEqualTo(EXPENSE_DATE);
    }

    @Test
    void createShouldTrimTextFields() {
        ExpenseRequest request = new ExpenseRequest(
                "  Bus ticket  ", new BigDecimal("30.00"), "  Travel  ", null, EXPENSE_DATE);

        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        expenseService.create(request);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Bus ticket");
        assertThat(captor.getValue().getCategory()).isEqualTo("Travel");
        assertThat(captor.getValue().getDescription()).isNull();
    }

    @Test
    void findAllShouldReturnEveryExpense() {
        when(expenseRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(expense(1L, "Lunch"), expense(2L, "Taxi")));

        List<ExpenseResponse> expenses = expenseService.findAll();

        assertThat(expenses).hasSize(2);
        assertThat(expenses).extracting(ExpenseResponse::title).containsExactly("Lunch", "Taxi");
    }

    @Test
    void findByIdShouldReturnExpenseWhenItExists() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense(1L, "Lunch")));

        ExpenseResponse response = expenseService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Lunch");
    }

    @Test
    void findByIdShouldThrowWhenExpenseDoesNotExist() {
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.findById(99L))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: 99");
    }

    @Test
    void updateShouldReplaceAllFields() {
        Expense existing = expense(1L, "Lunch");
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseRequest request = new ExpenseRequest(
                "Dinner", new BigDecimal("400.00"), "Food", "Family dinner", EXPENSE_DATE.plusDays(1));

        ExpenseResponse response = expenseService.update(1L, request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Dinner");
        assertThat(response.amount()).isEqualByComparingTo("400.00");
        assertThat(response.description()).isEqualTo("Family dinner");
        assertThat(response.date()).isEqualTo(EXPENSE_DATE.plusDays(1));
    }

    @Test
    void updateShouldThrowWhenExpenseDoesNotExist() {
        when(expenseRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.update(42L, new ExpenseRequest(
                "Dinner", new BigDecimal("400.00"), "Food", null, EXPENSE_DATE)))
                .isInstanceOf(ExpenseNotFoundException.class);

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void deleteShouldRemoveExistingExpense() {
        Expense existing = expense(1L, "Lunch");
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(existing));

        expenseService.delete(1L);

        verify(expenseRepository).delete(existing);
    }

    @Test
    void deleteShouldThrowWhenExpenseDoesNotExist() {
        when(expenseRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.delete(7L))
                .isInstanceOf(ExpenseNotFoundException.class);

        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void getTotalShouldReturnSumAndCount() {
        when(expenseRepository.sumOfAmounts()).thenReturn(new BigDecimal("680.00"));
        when(expenseRepository.count()).thenReturn(2L);

        TotalExpenseResponse total = expenseService.getTotal();

        assertThat(total.totalAmount()).isEqualByComparingTo("680.00");
        assertThat(total.numberOfExpenses()).isEqualTo(2L);
    }

    private Expense expense(Long id, String title) {
        Expense expense = new Expense(title, new BigDecimal("250.00"), "Food", "Sample expense", EXPENSE_DATE);
        expense.setId(id);
        return expense;
    }
}

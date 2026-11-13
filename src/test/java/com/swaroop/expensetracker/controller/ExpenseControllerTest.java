package com.swaroop.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swaroop.expensetracker.dto.ExpenseRequest;
import com.swaroop.expensetracker.dto.ExpenseResponse;
import com.swaroop.expensetracker.dto.TotalExpenseResponse;
import com.swaroop.expensetracker.exception.ExpenseNotFoundException;
import com.swaroop.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for {@link ExpenseController}.
 *
 * <p>Only the controller and the exception handler are started; the service is
 * replaced by a Mockito mock, so no database is required.</p>
 */
@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    private static final LocalDate EXPENSE_DATE = LocalDate.of(2026, 11, 10);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void postShouldReturn201AndCreatedExpense() throws Exception {
        ExpenseRequest request = new ExpenseRequest(
                "Lunch", new BigDecimal("250.00"), "Food", "Team lunch", EXPENSE_DATE);
        ExpenseResponse response = new ExpenseResponse(
                1L, "Lunch", new BigDecimal("250.00"), "Food", "Team lunch", EXPENSE_DATE);

        when(expenseService.create(any(ExpenseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.date").value("2026-11-10"));
    }

    @Test
    void postShouldReturn400WhenValidationFails() throws Exception {
        String invalidBody = """
                {
                  "title": "  ",
                  "amount": -10,
                  "category": "",
                  "date": null
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.title").value("Title is required"))
                .andExpect(jsonPath("$.fieldErrors.amount").value("Amount must be greater than zero"))
                .andExpect(jsonPath("$.fieldErrors.category").value("Category is required"))
                .andExpect(jsonPath("$.fieldErrors.date").exists());
    }

    @Test
    void postShouldReturn400WhenDateIsMalformed() throws Exception {
        String bodyWithBadDate = """
                {
                  "title": "Lunch",
                  "amount": 250.00,
                  "category": "Food",
                  "date": "10-11-2026"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithBadDate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Request body is missing or contains an invalid value. "
                                + "Dates must use the format yyyy-MM-dd."));
    }

    @Test
    void getAllShouldReturn200AndList() throws Exception {
        when(expenseService.findAll()).thenReturn(List.of(
                new ExpenseResponse(1L, "Lunch", new BigDecimal("250.00"), "Food", null, EXPENSE_DATE),
                new ExpenseResponse(2L, "Taxi", new BigDecimal("120.00"), "Travel", null, EXPENSE_DATE)
        ));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Lunch"))
                .andExpect(jsonPath("$[1].title").value("Taxi"));
    }

    @Test
    void getByIdShouldReturn200WhenExpenseExists() throws Exception {
        when(expenseService.findById(1L)).thenReturn(
                new ExpenseResponse(1L, "Lunch", new BigDecimal("250.00"), "Food", "Team lunch", EXPENSE_DATE));

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lunch"));
    }

    @Test
    void getByIdShouldReturn404WhenExpenseDoesNotExist() throws Exception {
        when(expenseService.findById(99L)).thenThrow(new ExpenseNotFoundException(99L));

        mockMvc.perform(get("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Expense not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/api/expenses/99"));
    }

    @Test
    void getByIdShouldReturn400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/expenses/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void putShouldReturn200AndUpdatedExpense() throws Exception {
        ExpenseRequest request = new ExpenseRequest(
                "Dinner", new BigDecimal("400.00"), "Food", "Family dinner", EXPENSE_DATE);
        ExpenseResponse response = new ExpenseResponse(
                1L, "Dinner", new BigDecimal("400.00"), "Food", "Family dinner", EXPENSE_DATE);

        when(expenseService.update(eq(1L), any(ExpenseRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dinner"))
                .andExpect(jsonPath("$.amount").value(400.00));
    }

    @Test
    void putShouldReturn404WhenExpenseDoesNotExist() throws Exception {
        ExpenseRequest request = new ExpenseRequest(
                "Dinner", new BigDecimal("400.00"), "Food", null, EXPENSE_DATE);
        when(expenseService.update(eq(99L), any(ExpenseRequest.class)))
                .thenThrow(new ExpenseNotFoundException(99L));

        mockMvc.perform(put("/api/expenses/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn204WhenExpenseExists() throws Exception {
        doNothing().when(expenseService).delete(1L);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404WhenExpenseDoesNotExist() throws Exception {
        doThrow(new ExpenseNotFoundException(99L)).when(expenseService).delete(99L);

        mockMvc.perform(delete("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense not found with id: 99"));
    }

    @Test
    void totalShouldReturnSumAndCount() throws Exception {
        when(expenseService.getTotal()).thenReturn(new TotalExpenseResponse(new BigDecimal("370.00"), 2L));

        mockMvc.perform(get("/api/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(370.00))
                .andExpect(jsonPath("$.numberOfExpenses").value(2));
    }
}

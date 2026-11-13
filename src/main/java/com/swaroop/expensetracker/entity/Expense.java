package com.swaroop.expensetracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single expense recorded by the user.
 *
 * <p>JPA maps this class to the {@code expenses} table. Validation annotations
 * are placed here as a second line of defence: even if a caller bypasses the
 * controller DTO, the entity itself can never be persisted in an invalid state.</p>
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimals")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be at most 50 characters")
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Size(max = 255, message = "Description must be at most 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /** Required by JPA. */
    public Expense() {
    }

    public Expense(String title, BigDecimal amount, String category, String description, LocalDate date) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

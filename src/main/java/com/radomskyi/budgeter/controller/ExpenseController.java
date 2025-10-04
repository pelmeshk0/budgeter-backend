package com.radomskyi.budgeter.controller;

import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Expenses", description = "API for managing expenses")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @PostMapping
    @Operation(summary = "Create a new expense", description = "Creates a new expense with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        log.info("Received request to create expense: {}", request);
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID", description = "Retrieves a specific expense by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense found"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @Parameter(description = "ID of the expense to retrieve") @PathVariable Long id) {
        log.info("Received request to get expense with id: {}", id);
        ExpenseResponse response = expenseService.getExpenseById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all expenses", description = "Retrieves all expenses with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully")
    })
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to get all expenses with pagination: {}", pageable);
        Page<ExpenseResponse> response = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update expense", description = "Updates an existing expense with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ExpenseResponse> updateExpense(
            @Parameter(description = "ID of the expense to update") @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        log.info("Received request to update expense with id: {} and data: {}", id, request);
        ExpenseResponse response = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense", description = "Deletes an expense by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<Void> deleteExpense(
            @Parameter(description = "ID of the expense to delete") @PathVariable Long id) {
        log.info("Received request to delete expense with id: {}", id);
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

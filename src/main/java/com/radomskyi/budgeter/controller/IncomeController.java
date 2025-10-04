package com.radomskyi.budgeter.controller;

import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import com.radomskyi.budgeter.service.IncomeService;
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
@RequestMapping("/api/income")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Incomes", description = "API for managing incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    @Operation(summary = "Create a new income", description = "Creates a new income with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Income created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<IncomeResponse> createIncome(@Valid @RequestBody IncomeRequest request) {
        log.info("Received request to create income: {}", request);
        IncomeResponse response = incomeService.createIncome(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get income by ID", description = "Retrieves a specific income by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Income found"),
            @ApiResponse(responseCode = "404", description = "Income not found")
    })
    public ResponseEntity<IncomeResponse> getIncomeById(
            @Parameter(description = "ID of the income to retrieve") @PathVariable Long id) {
        log.info("Received request to get income with id: {}", id);
        IncomeResponse response = incomeService.getIncomeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all incomes", description = "Retrieves all incomes with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incomes retrieved successfully")
    })
    public ResponseEntity<Page<IncomeResponse>> getAllIncomes(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to get all incomes with pagination: {}", pageable);
        Page<IncomeResponse> response = incomeService.getAllIncomes(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update income", description = "Updates an existing income with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Income updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Income not found")
    })
    public ResponseEntity<IncomeResponse> updateIncome(
            @Parameter(description = "ID of the income to update") @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        log.info("Received request to update income with id: {} and data: {}", id, request);
        IncomeResponse response = incomeService.updateIncome(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete income", description = "Deletes an income by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Income deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Income not found")
    })
    public ResponseEntity<Void> deleteIncome(
            @Parameter(description = "ID of the income to delete") @PathVariable Long id) {
        log.info("Received request to delete income with id: {}", id);
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}

package com.radomskyi.budgeter.controller;

import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Expense Management", description = "APIs for managing personal expenses")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @PostMapping("/expense")
    @Operation(
        summary = "Create a new expense",
        description = "Creates a new expense record with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Expense created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExpenseResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": "507f1f77bcf86cd799439011",
                          "value": 25.50,
                          "category": "FOOD",
                          "description": "Lunch at restaurant",
                          "tags": ["lunch", "restaurant"],
                          "createdAt": "2024-01-15T12:00:00",
                          "updatedAt": "2024-01-15T12:00:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.radomskyi.budgeter.exception.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ExpenseResponse> createExpense(
            @Parameter(description = "Expense details", required = true)
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/expenses")
    @Operation(
        summary = "Get all expenses",
        description = "Retrieves a list of all expenses in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of expenses retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ExpenseResponse.class),
            examples = @ExampleObject(
                name = "Success Response",
                value = """
                    [
                      {
                        "id": "507f1f77bcf86cd799439011",
                        "value": 25.50,
                        "category": "FOOD",
                        "description": "Lunch",
                        "tags": ["lunch", "restaurant"],
                        "createdAt": "2024-01-15T12:00:00",
                        "updatedAt": "2024-01-15T12:00:00"
                      }
                    ]
                    """
            )
        )
    )
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        List<ExpenseResponse> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/expense/{id}")
    @Operation(
        summary = "Get expense by ID",
        description = "Retrieves a specific expense by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense found and retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExpenseResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.radomskyi.budgeter.exception.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @Parameter(description = "Unique identifier of the expense", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        ExpenseResponse expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }
    
    @PutMapping("/expense/{id}")
    @Operation(
        summary = "Update expense by ID",
        description = "Updates an existing expense with new information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExpenseResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.radomskyi.budgeter.exception.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.radomskyi.budgeter.exception.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ExpenseResponse> updateExpense(
            @Parameter(description = "Unique identifier of the expense to update", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @Parameter(description = "Updated expense details", required = true)
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/expense/{id}")
    @Operation(
        summary = "Delete expense by ID",
        description = "Removes an expense from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Expense deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.radomskyi.budgeter.exception.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Void> deleteExpense(
            @Parameter(description = "Unique identifier of the expense to delete", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

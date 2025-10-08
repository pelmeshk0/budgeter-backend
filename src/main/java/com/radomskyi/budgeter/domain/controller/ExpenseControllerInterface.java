package com.radomskyi.budgeter.domain.controller;

import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Interface defining operations for Expense management. Provides REST API endpoints for creating,
 * reading, updating, and deleting expenses.
 */
@Tag(name = "Expenses", description = "API for managing expenses")
public interface ExpenseControllerInterface extends BaseController<ExpenseRequest, ExpenseResponse> {

    // This interface inherits all CRUD operations from BaseController
    // with ExpenseRequest and ExpenseResponse as type parameters
    // The @Tag annotation provides specific documentation for expense operations
}

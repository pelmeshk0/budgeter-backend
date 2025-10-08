package com.radomskyi.budgeter.domain.service;

import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;

/**
 * Interface defining operations for Expense management. Provides business logic methods for
 * creating, reading, updating, and deleting expenses.
 */
public interface ExpenseServiceInterface extends BaseService<ExpenseRequest, ExpenseResponse> {

    // This interface inherits all CRUD operations from BaseService
    // with ExpenseRequest and ExpenseResponse as type parameters
}

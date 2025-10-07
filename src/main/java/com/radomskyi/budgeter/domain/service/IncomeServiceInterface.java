package com.radomskyi.budgeter.domain.service;

import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;

/**
 * Interface defining operations for Income management.
 * Provides business logic methods for creating, reading, updating, and deleting incomes.
 */
public interface IncomeServiceInterface extends BaseService<IncomeRequest, IncomeResponse> {

    // This interface inherits all CRUD operations from BaseService
    // with IncomeRequest and IncomeResponse as type parameters
}

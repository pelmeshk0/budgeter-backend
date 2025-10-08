package com.radomskyi.budgeter.domain.controller;

import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Interface defining operations for Income management. Provides REST API endpoints for creating,
 * reading, updating, and deleting incomes.
 */
@Tag(name = "Incomes", description = "API for managing incomes")
public interface IncomeControllerInterface extends BaseController<IncomeRequest, IncomeResponse> {

    // This interface inherits all CRUD operations from BaseController
    // with IncomeRequest and IncomeResponse as type parameters
    // The @Tag annotation provides specific documentation for income operations
}

package com.radomskyi.budgeter.domain.service;

import com.radomskyi.budgeter.dto.InvestmentTransactionRequest;
import com.radomskyi.budgeter.dto.InvestmentTransactionResponse;

/**
 * Interface defining operations for Investment Transaction management.
 * Provides business logic methods for creating, reading, updating, and deleting investment transactions.
 */
public interface InvestmentServiceInterface extends BaseService<InvestmentTransactionRequest, InvestmentTransactionResponse> {

    // This interface inherits all CRUD operations from BaseService
    // with InvestmentTransactionRequest and InvestmentTransactionResponse as type parameters
}

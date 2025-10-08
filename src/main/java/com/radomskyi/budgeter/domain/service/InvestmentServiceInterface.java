package com.radomskyi.budgeter.domain.service;

import com.radomskyi.budgeter.domain.entity.investment.InvestmentTransaction;
import com.radomskyi.budgeter.dto.InvestmentTransactionRequest;

/**
 * Interface defining operations for Investment Transaction management. Provides business logic
 * methods for creating, reading, updating, and deleting investment transactions.
 */
public interface InvestmentServiceInterface extends BaseService<InvestmentTransactionRequest, InvestmentTransaction> {

    // This interface inherits all CRUD operations from BaseService
    // with InvestmentTransactionRequest and InvestmentTransaction as type parameters
}

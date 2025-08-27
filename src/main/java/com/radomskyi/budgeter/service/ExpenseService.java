package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import java.util.List;

public interface ExpenseService {
    
    ExpenseResponse createExpense(ExpenseRequest request);
    
    List<ExpenseResponse> getAllExpenses();
    
    ExpenseResponse getExpenseById(String id);
    
    ExpenseResponse updateExpense(String id, ExpenseRequest request);
    
    void deleteExpense(String id);
}

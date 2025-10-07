package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.Expense;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.exception.ExpenseNotFoundException;
import com.radomskyi.budgeter.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    /**
     * Create a new expense
     */
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        log.info("Creating new expense with amount: {} and category: {}", request.getAmount(), request.getCategory());
        
        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .tags(request.getTags())
                .build();
        
        Expense savedExpense = expenseRepository.save(expense);
        log.info("Successfully created expense with id: {}", savedExpense.getId());
        
        return mapToResponse(savedExpense);
    }
    
    /**
     * Get expense by ID
     */
    public ExpenseResponse getExpenseById(Long id) {
        log.info("Fetching expense with id: {}", id);
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
        
        return mapToResponse(expense);
    }
    
    /**
     * Get all expenses with pagination
     */
    public Page<ExpenseResponse> getAllExpenses(Pageable pageable) {
        log.info("Fetching all expenses with pagination: {}", pageable);
        
        Page<Expense> expenses = expenseRepository.findAll(pageable);
        return expenses.map(this::mapToResponse);
    }
    
    /**
     * Update an existing expense
     */
    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        log.info("Updating expense with id: {}", id);
        
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
        
        existingExpense.setAmount(request.getAmount());
        existingExpense.setName(request.getName());
        existingExpense.setCategory(request.getCategory());
        existingExpense.setDescription(request.getDescription());
        existingExpense.setTags(request.getTags());
        
        Expense updatedExpense = expenseRepository.save(existingExpense);
        log.info("Successfully updated expense with id: {}", updatedExpense.getId());
        
        return mapToResponse(updatedExpense);
    }
    
    /**
     * Delete an expense by ID
     */
    @Transactional
    public void deleteExpense(Long id) {
        log.info("Deleting expense with id: {}", id);
        
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException("Expense not found with id: " + id);
        }
        
        expenseRepository.deleteById(id);
        log.info("Successfully deleted expense with id: {}", id);
    }
    
    /**
     * Map Expense entity to ExpenseResponse DTO
     */
    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .name(expense.getName())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .tags(expense.getTags())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}

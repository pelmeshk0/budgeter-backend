package com.radomskyi.budgeter.service.impl;

import com.radomskyi.budgeter.domain.Expense;
import com.radomskyi.budgeter.domain.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.repository.ExpenseRepository;
import com.radomskyi.budgeter.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = new Expense(request.getValue(), request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setTags(request.getTags());
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        
        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    @Override
    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseResponse getExpenseById(String id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        return mapToResponse(expense);
    }

    @Override
    public ExpenseResponse updateExpense(String id, ExpenseRequest request) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        
        existingExpense.setValue(request.getValue());
        existingExpense.setCategory(request.getCategory());
        existingExpense.setDescription(request.getDescription());
        existingExpense.setTags(request.getTags());
        existingExpense.preUpdate();
        
        Expense updatedExpense = expenseRepository.save(existingExpense);
        return mapToResponse(updatedExpense);
    }

    @Override
    public void deleteExpense(String id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getValue(),
            expense.getCategory(),
            expense.getDescription(),
            expense.getTags(),
            expense.getCreatedAt(),
            expense.getUpdatedAt()
        );
    }
}

package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Expense;
import com.radomskyi.budgeter.domain.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.repository.ExpenseRepository;
import com.radomskyi.budgeter.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private ExpenseRequest expenseRequest;
    private Expense expense;
    private ExpenseResponse expectedResponse;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequest();
        expenseRequest.setValue(new BigDecimal("25.50"));
        expenseRequest.setCategory(Category.NEEDS);
        expenseRequest.setDescription("Lunch at restaurant");
        expenseRequest.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS));

        expense = new Expense();
        expense.setId("123");
        expense.setValue(new BigDecimal("25.50"));
        expense.setCategory(Category.NEEDS);
        expense.setDescription("Lunch at restaurant");
        expense.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS));
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());

        expectedResponse = new ExpenseResponse();
        expectedResponse.setId("123");
        expectedResponse.setValue(new BigDecimal("25.50"));
        expectedResponse.setCategory(Category.NEEDS);
        expectedResponse.setDescription("Lunch at restaurant");
        expectedResponse.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS));
        expectedResponse.setCreatedAt(expense.getCreatedAt());
        expectedResponse.setUpdatedAt(expense.getUpdatedAt());
    }

    @Test
    void testCreateExpense() {
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        ExpenseResponse result = expenseService.createExpense(expenseRequest);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getValue(), result.getValue());
        assertEquals(expectedResponse.getCategory(), result.getCategory());
        assertEquals(expectedResponse.getDescription(), result.getDescription());
        assertEquals(expectedResponse.getTags(), result.getTags());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void testGetAllExpenses() {
        List<Expense> expenses = Arrays.asList(expense);
        when(expenseRepository.findAll()).thenReturn(expenses);

        List<ExpenseResponse> result = expenseService.getAllExpenses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse.getId(), result.get(0).getId());
        verify(expenseRepository).findAll();
    }

    @Test
    void testGetExpenseById() {
        when(expenseRepository.findById("123")).thenReturn(Optional.of(expense));

        ExpenseResponse result = expenseService.getExpenseById("123");

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        verify(expenseRepository).findById("123");
    }

    @Test
    void testGetExpenseByIdNotFound() {
        when(expenseRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> expenseService.getExpenseById("999"));
        verify(expenseRepository).findById("999");
    }

    @Test
    void testUpdateExpense() {
        when(expenseRepository.findById("123")).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        ExpenseResponse result = expenseService.updateExpense("123", expenseRequest);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        verify(expenseRepository).findById("123");
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void testUpdateExpenseNotFound() {
        when(expenseRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> expenseService.updateExpense("999", expenseRequest));
        verify(expenseRepository).findById("999");
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void testDeleteExpense() {
        when(expenseRepository.existsById("123")).thenReturn(true);
        doNothing().when(expenseRepository).deleteById("123");

        assertDoesNotThrow(() -> expenseService.deleteExpense("123"));
        verify(expenseRepository).existsById("123");
        verify(expenseRepository).deleteById("123");
    }

    @Test
    void testDeleteExpenseNotFound() {
        when(expenseRepository.existsById("999")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> expenseService.deleteExpense("999"));
        verify(expenseRepository).existsById("999");
        verify(expenseRepository, never()).deleteById(anyString());
    }
}

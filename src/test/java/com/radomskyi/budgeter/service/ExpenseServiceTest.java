package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.entity.budgeting.ExpenseCategory;
import com.radomskyi.budgeter.domain.entity.budgeting.Expense;
import com.radomskyi.budgeter.domain.entity.budgeting.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    
    @Mock
    private ExpenseRepository expenseRepository;
    
    @InjectMocks
    private ExpenseService expenseService;
    
    private Expense testExpense;
    private ExpenseRequest testExpenseRequest;
    
    @BeforeEach
    void setUp() {
        testExpense = Expense.builder()
                .id(1L)
                .amount(new BigDecimal("25.50"))
                .name("Test Expense")
                .category(ExpenseCategory.WANTS)
                .description("Test expense")
                .tags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testExpenseRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("25.50"))
                .name("Test Expense")
                .category(ExpenseCategory.WANTS)
                .description("Test expense")
                .tags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS))
                .build();
    }
    
    @Test
    void createExpense_ShouldReturnExpenseResponse_WhenValidRequest() {
        // Given
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // When
        ExpenseResponse result = expenseService.createExpense(testExpenseRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Expense");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("25.50"));
        assertThat(result.getCategory()).isEqualTo(ExpenseCategory.WANTS);
        assertThat(result.getDescription()).isEqualTo("Test expense");
        assertThat(result.getTags()).containsExactly(Tag.FOOD, Tag.BARS_AND_RESTAURANTS);

        verify(expenseRepository).save(any(Expense.class));
    }
    
    @Test
    void getExpenseById_ShouldReturnExpenseResponse_WhenExpenseExists() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        // When
        ExpenseResponse result = expenseService.getExpenseById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Expense");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("25.50"));
        assertThat(result.getCategory()).isEqualTo(ExpenseCategory.WANTS);
        assertThat(result.getDescription()).isEqualTo("Test expense");
        assertThat(result.getTags()).containsExactly(Tag.FOOD, Tag.BARS_AND_RESTAURANTS);

        verify(expenseRepository).findById(1L);
    }
    
    @Test
    void getExpenseById_ShouldThrowException_WhenExpenseNotFound() {
        // Given
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> expenseService.getExpenseById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expense not found with id: 999");
        
        verify(expenseRepository).findById(999L);
    }
    
    @Test
    void getAllExpenses_ShouldReturnPageOfExpenseResponses_WhenExpensesExist() {
        // Given
        List<Expense> expenses = Arrays.asList(testExpense);
        Page<Expense> expensePage = new PageImpl<>(expenses);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(expenseRepository.findAll(pageable)).thenReturn(expensePage);
        
        // When
        Page<ExpenseResponse> result = expenseService.getAllExpenses(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getAmount()).isEqualTo(new BigDecimal("25.50"));
        
        verify(expenseRepository).findAll(pageable);
    }
    

    
    @Test
    void updateExpense_ShouldReturnUpdatedExpenseResponse_WhenExpenseExists() {
        // Given
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("30.00"))
                .name("Updated Expense")
                .category(ExpenseCategory.NEEDS)
                .description("Updated expense")
                .tags(Arrays.asList(Tag.TRANSPORT))
                .build();

        Expense updatedExpense = Expense.builder()
                .id(1L)
                .amount(new BigDecimal("30.00"))
                .name("Updated Expense")
                .category(ExpenseCategory.NEEDS)
                .description("Updated expense")
                .tags(Arrays.asList(Tag.TRANSPORT))
                .createdAt(testExpense.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);
        
        // When
        ExpenseResponse result = expenseService.updateExpense(1L, updateRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Expense");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("30.00"));
        assertThat(result.getCategory()).isEqualTo(ExpenseCategory.NEEDS);
        assertThat(result.getDescription()).isEqualTo("Updated expense");
        assertThat(result.getTags()).containsExactly(Tag.TRANSPORT);
        
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }
    
    @Test
    void updateExpense_ShouldThrowException_WhenExpenseNotFound() {
        // Given
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> expenseService.updateExpense(999L, testExpenseRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expense not found with id: 999");
        
        verify(expenseRepository).findById(999L);
        verify(expenseRepository, never()).save(any(Expense.class));
    }
    
    @Test
    void deleteExpense_ShouldDeleteExpense_WhenExpenseExists() {
        // Given
        when(expenseRepository.existsById(1L)).thenReturn(true);
        
        // When
        expenseService.deleteExpense(1L);
        
        // Then
        verify(expenseRepository).existsById(1L);
        verify(expenseRepository).deleteById(1L);
    }
    
    @Test
    void deleteExpense_ShouldThrowException_WhenExpenseNotFound() {
        // Given
        when(expenseRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> expenseService.deleteExpense(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expense not found with id: 999");
        
        verify(expenseRepository).existsById(999L);
        verify(expenseRepository, never()).deleteById(anyLong());
    }
    

}

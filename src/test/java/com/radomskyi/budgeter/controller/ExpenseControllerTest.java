package com.radomskyi.budgeter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radomskyi.budgeter.domain.entity.budgeting.ExpenseCategory;
import com.radomskyi.budgeter.domain.entity.budgeting.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private ExpenseService expenseService;
    
    @InjectMocks
    private ExpenseController expenseController;
    
    private ObjectMapper objectMapper;
    
    private ExpenseRequest testExpenseRequest;
    private ExpenseResponse testExpenseResponse;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        
        testExpenseRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("25.50"))
                .name("Test Expense")
                .category(ExpenseCategory.WANTS)
                .description("Test expense")
                .tags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS))
                .build();
        
        testExpenseResponse = ExpenseResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.50"))
                .name("Test Expense")
                .category(ExpenseCategory.WANTS)
                .description("Test expense")
                .tags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void create_ShouldReturnCreatedExpense_WhenValidRequest() throws Exception {
        // Given
        when(expenseService.create(any(ExpenseRequest.class))).thenReturn(testExpenseResponse);
        
        // When & Then
        mockMvc.perform(post("/api/expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("WANTS"))
                .andExpect(jsonPath("$.description").value("Test expense"))
                .andExpect(jsonPath("$.tags[0]").value("FOOD"))
                .andExpect(jsonPath("$.tags[1]").value("BARS_AND_RESTAURANTS"));
    }
    
    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        ExpenseRequest invalidRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("-10.00")) // Invalid negative amount
                .name("Invalid Expense")
                .category(ExpenseCategory.WANTS)
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void create_ShouldReturnBadRequest_WhenRequiredFieldsMissing() throws Exception {
        // Given
        ExpenseRequest invalidRequest = ExpenseRequest.builder()
                .name("Missing Required Fields")
                .description("Missing required fields")
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getById_ShouldReturnExpense_WhenExpenseExists() throws Exception {
        // Given
        when(expenseService.getById(1L)).thenReturn(testExpenseResponse);
        
        // When & Then
        mockMvc.perform(get("/api/expense/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("WANTS"))
                .andExpect(jsonPath("$.description").value("Test expense"));
    }
    
    @Test
    void getAll_ShouldReturnPageOfExpenses_WhenExpensesExist() throws Exception {
        // Given
        List<ExpenseResponse> expenses = Arrays.asList(testExpenseResponse);
        Page<ExpenseResponse> expensePage = new PageImpl<>(expenses, PageRequest.of(0, 20), 1);
        when(expenseService.getAll(any())).thenReturn(expensePage);
        
        // When & Then
        mockMvc.perform(get("/api/expense"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(25.50))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
    
    @Test
    void update_ShouldReturnUpdatedExpense_WhenExpenseExists() throws Exception {
        // Given
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("30.00"))
                .name("Updated Expense")
                .category(ExpenseCategory.NEEDS)
                .description("Updated expense")
                .tags(Arrays.asList(Tag.TRANSPORT))
                .build();
        
        ExpenseResponse updatedResponse = ExpenseResponse.builder()
                .id(1L)
                .amount(new BigDecimal("30.00"))
                .name("Updated Expense")
                .category(ExpenseCategory.NEEDS)
                .description("Updated expense")
                .tags(Arrays.asList(Tag.TRANSPORT))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(expenseService.update(1L, updateRequest)).thenReturn(updatedResponse);
        
        // When & Then
        mockMvc.perform(put("/api/expense/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Expense"))
                .andExpect(jsonPath("$.amount").value(30.00))
                .andExpect(jsonPath("$.category").value("NEEDS"))
                .andExpect(jsonPath("$.description").value("Updated expense"))
                .andExpect(jsonPath("$.tags[0]").value("TRANSPORT"));
    }
    
    @Test
    void update_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        ExpenseRequest invalidRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("-10.00")) // Invalid negative amount
                .name("Invalid Update")
                .category(ExpenseCategory.WANTS)
                .build();
        
        // When & Then
        mockMvc.perform(put("/api/expense/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void deleteExpense_ShouldReturnNoContent_WhenExpenseExists() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/expense/1"))
                .andExpect(status().isNoContent());
    }
}

package com.radomskyi.budgeter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExpenseRequest expenseRequest;
    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequest();
        expenseRequest.setValue(new BigDecimal("25.50"));
        expenseRequest.setCategory(Category.NEEDS);
        expenseRequest.setDescription("Lunch at restaurant");
        expenseRequest.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS));

        expenseResponse = new ExpenseResponse();
        expenseResponse.setId("123");
        expenseResponse.setValue(new BigDecimal("25.50"));
        expenseResponse.setCategory(Category.NEEDS);
        expenseResponse.setDescription("Lunch at restaurant");
        expenseResponse.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS));
        expenseResponse.setCreatedAt(LocalDateTime.now());
        expenseResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateExpense() throws Exception {
        when(expenseService.createExpense(any(ExpenseRequest.class))).thenReturn(expenseResponse);

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.value").value("25.50"))
                .andExpect(jsonPath("$.category").value("NEEDS"))
                .andExpect(jsonPath("$.description").value("Lunch at restaurant"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("FOOD"))
                .andExpect(jsonPath("$.tags[1]").value("BARS_AND_RESTAURANTS"));

        verify(expenseService).createExpense(any(ExpenseRequest.class));
    }

    @Test
    void testCreateExpenseWithInvalidData() throws Exception {
        ExpenseRequest invalidRequest = new ExpenseRequest();
        invalidRequest.setValue(new BigDecimal("-10.00")); // Invalid negative value
        invalidRequest.setCategory(Category.NEEDS);

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(ExpenseRequest.class));
    }

    @Test
    void testGetAllExpenses() throws Exception {
        List<ExpenseResponse> expenses = Arrays.asList(expenseResponse);
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("123"))
                .andExpect(jsonPath("$[0].category").value("NEEDS"));

        verify(expenseService).getAllExpenses();
    }

    @Test
    void testGetExpenseById() throws Exception {
        when(expenseService.getExpenseById("123")).thenReturn(expenseResponse);

        mockMvc.perform(get("/api/expense/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.category").value("NEEDS"));

        verify(expenseService).getExpenseById("123");
    }

    @Test
    void testGetExpenseByIdNotFound() throws Exception {
        when(expenseService.getExpenseById("999")).thenThrow(new RuntimeException("Expense not found"));

        mockMvc.perform(get("/api/expense/999"))
                .andExpect(status().isNotFound());

        verify(expenseService).getExpenseById("999");
    }

    @Test
    void testUpdateExpense() throws Exception {
        when(expenseService.updateExpense(eq("123"), any(ExpenseRequest.class))).thenReturn(expenseResponse);

        mockMvc.perform(put("/api/expense/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.category").value("NEEDS"));

        verify(expenseService).updateExpense(eq("123"), any(ExpenseRequest.class));
    }

    @Test
    void testUpdateExpenseNotFound() throws Exception {
        when(expenseService.updateExpense(eq("999"), any(ExpenseRequest.class)))
                .thenThrow(new RuntimeException("Expense not found"));

        mockMvc.perform(put("/api/expense/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isNotFound());

        verify(expenseService).updateExpense(eq("999"), any(ExpenseRequest.class));
    }

    @Test
    void testDeleteExpense() throws Exception {
        doNothing().when(expenseService).deleteExpense("123");

        mockMvc.perform(delete("/api/expense/123"))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense("123");
    }

    @Test
    void testDeleteExpenseNotFound() throws Exception {
        doThrow(new RuntimeException("Expense not found")).when(expenseService).deleteExpense("999");

        mockMvc.perform(delete("/api/expense/999"))
                .andExpect(status().isNotFound());

        verify(expenseService).deleteExpense("999");
    }
}

package com.radomskyi.budgeter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radomskyi.budgeter.domain.entity.budgeting.IncomeCategory;
import com.radomskyi.budgeter.domain.entity.budgeting.Tag;
import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import com.radomskyi.budgeter.service.IncomeService;
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
class IncomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IncomeService incomeService;

    @InjectMocks
    private IncomeController incomeController;

    private ObjectMapper objectMapper;

    private IncomeRequest testIncomeRequest;
    private IncomeResponse testIncomeResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization

        testIncomeRequest = IncomeRequest.builder()
                .amount(new BigDecimal("3500.00"))
                .name("Monthly Salary")
                .category(IncomeCategory.SALARY)
                .description("Monthly salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        testIncomeResponse = IncomeResponse.builder()
                .id(1L)
                .amount(new BigDecimal("3500.00"))
                .name("Monthly Salary")
                .category(IncomeCategory.SALARY)
                .description("Monthly salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(incomeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void createIncome_ShouldReturnCreatedIncome() throws Exception {
        // Given
        when(incomeService.createIncome(any(IncomeRequest.class))).thenReturn(testIncomeResponse);

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIncomeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(3500.00))
                .andExpect(jsonPath("$.category").value("SALARY"))
                .andExpect(jsonPath("$.description").value("Monthly salary"));
    }

    @Test
    void createIncome_WithMissingAmount_ShouldReturnBadRequest() throws Exception {
        // Given
        IncomeRequest invalidRequest = IncomeRequest.builder()
                .amount(null) // Invalid: amount is required
                .name("Missing Amount")
                .category(IncomeCategory.SALARY) // Category provided
                .description("Missing amount")
                .build();

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIncome_WithMissingCategory_ShouldReturnBadRequest() throws Exception {
        // Given
        IncomeRequest invalidRequest = IncomeRequest.builder()
                .amount(new BigDecimal("3500.00")) // Amount provided
                .category(null) // Invalid: category is required
                .description("Missing category")
                .build();

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getIncomeById_ShouldReturnIncome() throws Exception {
        // Given
        when(incomeService.getIncomeById(1L)).thenReturn(testIncomeResponse);

        // When & Then
        mockMvc.perform(get("/api/income/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(3500.00))
                .andExpect(jsonPath("$.category").value("SALARY"));
    }

    @Test
    void getAllIncomes_ShouldReturnPageOfIncomes() throws Exception {
        // Given
        List<IncomeResponse> incomes = Arrays.asList(testIncomeResponse);
        Page<IncomeResponse> incomePage = new PageImpl<>(incomes, PageRequest.of(0, 20), 1);
        when(incomeService.getAllIncomes(any())).thenReturn(incomePage);

        // When & Then
        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(3500.00));
    }

    @Test
    void updateIncome_ShouldReturnUpdatedIncome() throws Exception {
        // Given
        IncomeRequest updateRequest = IncomeRequest.builder()
                .amount(new BigDecimal("4000.00"))
                .name("Updated Salary")
                .category(IncomeCategory.SALARY)
                .description("Updated salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        IncomeResponse updatedResponse = IncomeResponse.builder()
                .id(1L)
                .amount(new BigDecimal("4000.00"))
                .name("Updated Salary")
                .category(IncomeCategory.SALARY)
                .description("Updated salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(incomeService.updateIncome(any(Long.class), any(IncomeRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/income/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(4000.00))
                .andExpect(jsonPath("$.description").value("Updated salary"));
    }

    @Test
    void updateIncome_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        IncomeRequest invalidRequest = IncomeRequest.builder()
                .amount(new BigDecimal("-100.00")) // Invalid: amount must be positive
                .category(IncomeCategory.SALARY)
                .build();

        // When & Then
        mockMvc.perform(put("/api/income/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteIncome_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/income/1"))
                .andExpect(status().isNoContent());
    }
}

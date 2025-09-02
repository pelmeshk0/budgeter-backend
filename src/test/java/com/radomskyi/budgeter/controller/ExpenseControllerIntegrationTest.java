package com.radomskyi.budgeter.controller;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.repository.ExpenseRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        expenseRepository.deleteAll();
    }

    @Test
    void createExpense_ShouldCreateAndReturnExpense_WhenValidRequest() throws Exception {
        // Given
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("25.50"))
                .category(Category.WANTS)
                .description("Test expense")
                .tags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS))
                .build();

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("WANTS"))
                .andExpect(jsonPath("$.description").value("Test expense"))
                .andExpect(jsonPath("$.tags[0]").value("FOOD"))
                .andExpect(jsonPath("$.tags[1]").value("BARS_AND_RESTAURANTS"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andReturn();

        // Verify the expense was actually saved to the database
        assertThat(expenseRepository.count()).isEqualTo(1);
    }

    @Test
    void createExpense_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        ExpenseRequest invalidRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("-10.00")) // Invalid negative amount
                .category(Category.WANTS)
                .build();

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify no expense was saved to the database
        assertThat(expenseRepository.count()).isEqualTo(0);
    }

    @Test
    void getExpenseById_ShouldReturnExpense_WhenExpenseExists() throws Exception {
        // Given - Create an expense first
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("15.75"))
                .category(Category.NEEDS)
                .description("Integration test expense")
                .tags(Arrays.asList(Tag.TRANSPORT))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        ExpenseResponse createdExpense = objectMapper.readValue(responseContent, ExpenseResponse.class);
        Long expenseId = createdExpense.getId();

        // When & Then
        mockMvc.perform(get("/api/expenses/" + expenseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expenseId))
                .andExpect(jsonPath("$.amount").value(15.75))
                .andExpect(jsonPath("$.category").value("NEEDS"))
                .andExpect(jsonPath("$.description").value("Integration test expense"))
                .andExpect(jsonPath("$.tags[0]").value("TRANSPORT"));
    }

    @Test
    void getExpenseById_ShouldReturnNotFound_WhenExpenseDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllExpenses_ShouldReturnPageOfExpenses_WhenExpensesExist() throws Exception {
        // Given - Create multiple expenses
        createTestExpense("Expense 1", new BigDecimal("10.00"), Category.WANTS);
        createTestExpense("Expense 2", new BigDecimal("20.00"), Category.NEEDS);
        createTestExpense("Expense 3", new BigDecimal("30.00"), Category.FIXED);

        // When & Then
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(20)) // Default page size
                .andExpect(jsonPath("$.number").value(0)); // First page
    }

    @Test
    void getAllExpenses_ShouldReturnEmptyPage_WhenNoExpensesExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void getAllExpenses_ShouldSupportPagination_WhenRequested() throws Exception {
        // Given - Create 5 expenses
        for (int i = 1; i <= 5; i++) {
            createTestExpense("Expense " + i, new BigDecimal(i * 10), Category.WANTS);
        }

        // When & Then - Request first page with size 2
        mockMvc.perform(get("/api/expenses?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0));

        // When & Then - Request second page
        mockMvc.perform(get("/api/expenses?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.number").value(1));

        // When & Then - Request third page
        mockMvc.perform(get("/api/expenses?page=2&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.number").value(2));
    }

    @Test
    void updateExpense_ShouldUpdateAndReturnExpense_WhenExpenseExists() throws Exception {
        // Given - Create an expense first
        ExpenseRequest createRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("25.00"))
                .category(Category.WANTS)
                .description("Original expense")
                .tags(Arrays.asList(Tag.FOOD))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        ExpenseResponse createdExpense = objectMapper.readValue(responseContent, ExpenseResponse.class);
        Long expenseId = createdExpense.getId();

        // Given - Update request
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("35.00"))
                .category(Category.NEEDS)
                .description("Updated expense")
                .tags(Arrays.asList(Tag.TRANSPORT, Tag.HEALTH))
                .build();

        // When & Then
        mockMvc.perform(put("/api/expenses/" + expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expenseId))
                .andExpect(jsonPath("$.amount").value(35.00))
                .andExpect(jsonPath("$.category").value("NEEDS"))
                .andExpect(jsonPath("$.description").value("Updated expense"))
                .andExpect(jsonPath("$.tags[0]").value("TRANSPORT"))
                .andExpect(jsonPath("$.tags[1]").value("HEALTH"))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        // Verify the expense was actually updated in the database
        assertThat(expenseRepository.count()).isEqualTo(1);
    }

    @Test
    void updateExpense_ShouldReturnNotFound_WhenExpenseDoesNotExist() throws Exception {
        // Given
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("35.00"))
                .category(Category.NEEDS)
                .description("Updated expense")
                .build();

        // When & Then
        mockMvc.perform(put("/api/expenses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExpense_ShouldDeleteExpense_WhenExpenseExists() throws Exception {
        // Given - Create an expense first
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("15.00"))
                .category(Category.WANTS)
                .description("To be deleted")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        ExpenseResponse createdExpense = objectMapper.readValue(responseContent, ExpenseResponse.class);
        Long expenseId = createdExpense.getId();

        // Verify expense exists
        assertThat(expenseRepository.count()).isEqualTo(1);

        // When & Then
        mockMvc.perform(delete("/api/expenses/" + expenseId))
                .andExpect(status().isNoContent());

        // Verify the expense was actually deleted from the database
        assertThat(expenseRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteExpense_ShouldReturnNotFound_WhenExpenseDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void fullCrudWorkflow_ShouldWorkCorrectly() throws Exception {
        // 1. Create an expense
        ExpenseRequest createRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("50.00"))
                .category(Category.WANTS)
                .description("Full workflow test")
                .tags(Arrays.asList(Tag.ENTERTAINMENT))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        ExpenseResponse createdExpense = objectMapper.readValue(responseContent, ExpenseResponse.class);
        Long expenseId = createdExpense.getId();

        // 2. Read the expense
        mockMvc.perform(get("/api/expenses/" + expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.description").value("Full workflow test"));

        // 3. Update the expense
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .amount(new BigDecimal("75.00"))
                .category(Category.NEEDS)
                .description("Updated workflow test")
                .tags(Arrays.asList(Tag.HEALTH, Tag.EDUCATION))
                .build();

        mockMvc.perform(put("/api/expenses/" + expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(75.00))
                .andExpect(jsonPath("$.description").value("Updated workflow test"));

        // 4. Verify in list
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(75.00));

        // 5. Delete the expense
        mockMvc.perform(delete("/api/expenses/" + expenseId))
                .andExpect(status().isNoContent());

        // 6. Verify deletion
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    private void createTestExpense(String description, BigDecimal amount, Category category) throws Exception {
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(amount)
                .category(category)
                .description(description)
                .tags(Arrays.asList(Tag.OTHER))
                .build();

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

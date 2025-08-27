package com.radomskyi.budgeter.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Tag;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ExpenseIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void testExpenseLifecycle() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create expense
        ExpenseRequest createRequest = new ExpenseRequest();
        createRequest.setValue(new BigDecimal("25.50"));
        createRequest.setCategory(Category.NEEDS);
        createRequest.setDescription("Lunch at restaurant");
        createRequest.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS));

        String createResponse = mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value("25.50"))
                .andExpect(jsonPath("$.category").value("NEEDS"))
                .andExpect(jsonPath("$.description").value("Lunch at restaurant"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("FOOD"))
                .andExpect(jsonPath("$.tags[1]").value("BARS_AND_RESTAURANTS"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExpenseResponse createdExpense = objectMapper.readValue(createResponse, ExpenseResponse.class);
        String expenseId = createdExpense.getId();

        // Get expense by ID
        mockMvc.perform(get("/api/expense/" + expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenseId))
                .andExpect(jsonPath("$.value").value("25.50"))
                .andExpect(jsonPath("$.category").value("NEEDS"));

        // Update expense
        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setValue(new BigDecimal("30.00"));
        updateRequest.setCategory(Category.WANTS);
        updateRequest.setDescription("Updated lunch with dessert");
        updateRequest.setTags(Arrays.asList(Tag.FOOD, Tag.BARS_AND_RESTAURANTS, Tag.ENTERTAINMENT));

        mockMvc.perform(put("/api/expense/" + expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("30.00"))
                .andExpect(jsonPath("$.category").value("WANTS"))
                .andExpect(jsonPath("$.description").value("Updated lunch with dessert"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").value(Arrays.asList("FOOD", "BARS_AND_RESTAURANTS", "ENTERTAINMENT")));

        // Get all expenses
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(expenseId))
                .andExpect(jsonPath("$[0].value").value("30.00"))
                .andExpect(jsonPath("$[0].category").value("WANTS"));

        // Delete expense
        mockMvc.perform(delete("/api/expense/" + expenseId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/expense/" + expenseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateExpenseWithInvalidData() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Test with negative value
        ExpenseRequest invalidRequest = new ExpenseRequest();
        invalidRequest.setValue(new BigDecimal("-10.00"));
        invalidRequest.setCategory(Category.NEEDS);

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Test with null category
        ExpenseRequest nullCategoryRequest = new ExpenseRequest();
        nullCategoryRequest.setValue(new BigDecimal("25.50"));
        // category is null

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullCategoryRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNonExistentExpense() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/expense/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateNonExistentExpense() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setValue(new BigDecimal("25.50"));
        updateRequest.setCategory(Category.NEEDS);

        mockMvc.perform(put("/api/expense/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistentExpense() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(delete("/api/expense/nonexistent"))
                .andExpect(status().isNotFound());
    }
}

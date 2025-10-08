package com.radomskyi.budgeter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radomskyi.budgeter.domain.entity.budgeting.IncomeCategory;
import com.radomskyi.budgeter.domain.entity.budgeting.Tag;
import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import com.radomskyi.budgeter.repository.IncomeRepository;
import java.math.BigDecimal;
import java.util.Arrays;
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

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class IncomeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        incomeRepository.deleteAll();
    }

    @Test
    void create_ShouldCreateAndReturnIncome_WhenValidRequest() throws Exception {
        // Given
        IncomeRequest request = IncomeRequest.builder()
                .amount(new BigDecimal("3500.00"))
                .name("Monthly Salary")
                .category(IncomeCategory.SALARY)
                .description("Monthly salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Monthly Salary"))
                .andExpect(jsonPath("$.amount").value(3500.00))
                .andExpect(jsonPath("$.category").value("SALARY"))
                .andExpect(jsonPath("$.description").value("Monthly salary"))
                .andExpect(jsonPath("$.tags[0]").value("BANKING_AND_TAXES"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andReturn();

        // Verify the income was actually saved to the database
        assertThat(incomeRepository.count()).isEqualTo(1);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        IncomeRequest invalidRequest = IncomeRequest.builder()
                .amount(new BigDecimal("-100.00")) // Invalid negative amount
                .category(IncomeCategory.SALARY)
                .build();

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify no income was saved to the database
        assertThat(incomeRepository.count()).isEqualTo(0);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMissingAmount() throws Exception {
        // Given
        IncomeRequest invalidRequest = IncomeRequest.builder()
                .amount(null)
                .name("Test Income")
                .category(IncomeCategory.SALARY)
                .description("Test income")
                .build();

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify no income was saved to the database
        assertThat(incomeRepository.count()).isEqualTo(0);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMissingCategory() throws Exception {
        // Given
        IncomeRequest invalidRequest = IncomeRequest.builder()
                .amount(new BigDecimal("3500.00"))
                .category(null)
                .description("Test income")
                .build();

        // When & Then
        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify no income was saved to the database
        assertThat(incomeRepository.count()).isEqualTo(0);
    }

    @Test
    void getById_ShouldReturnIncome_WhenIncomeExists() throws Exception {
        // Given
        IncomeRequest request = IncomeRequest.builder()
                .amount(new BigDecimal("2500.00"))
                .name("Freelance Income")
                .category(IncomeCategory.FREELANCE)
                .description("Freelance project")
                .tags(Arrays.asList(Tag.OTHER))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        IncomeResponse createdIncome = objectMapper.readValue(responseContent, IncomeResponse.class);
        Long incomeId = createdIncome.getId();

        // When & Then
        mockMvc.perform(get("/api/income/" + incomeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incomeId))
                .andExpect(jsonPath("$.amount").value(2500.00))
                .andExpect(jsonPath("$.name").value("Freelance Income"))
                .andExpect(jsonPath("$.category").value("FREELANCE"))
                .andExpect(jsonPath("$.description").value("Freelance project"))
                .andExpect(jsonPath("$.tags[0]").value("OTHER"));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenIncomeDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/income/999")).andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnEmptyPage_WhenNoIncomesExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getAll_ShouldReturnAllIncomes_WithPagination() throws Exception {
        // Given - Create multiple incomes
        createTestIncome("Income 1", new BigDecimal("1000.00"), IncomeCategory.SALARY);
        createTestIncome("Income 2", new BigDecimal("2000.00"), IncomeCategory.FREELANCE);
        createTestIncome("Income 3", new BigDecimal("3000.00"), IncomeCategory.BUSINESS);

        // When & Then - Get first page
        mockMvc.perform(get("/api/income?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        // When & Then - Get second page
        mockMvc.perform(get("/api/income?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void update_ShouldUpdateAndReturnIncome_WhenIncomeExists() throws Exception {
        // Given
        IncomeRequest createRequest = IncomeRequest.builder()
                .amount(new BigDecimal("1500.00"))
                .name("Original Salary")
                .category(IncomeCategory.SALARY)
                .description("Original salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        IncomeResponse createdIncome = objectMapper.readValue(responseContent, IncomeResponse.class);
        Long incomeId = createdIncome.getId();

        // Update request
        IncomeRequest updateRequest = IncomeRequest.builder()
                .amount(new BigDecimal("1800.00"))
                .name("Salary Income")
                .category(IncomeCategory.SALARY)
                .description("Updated salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES, Tag.OTHER))
                .build();

        // When & Then
        mockMvc.perform(put("/api/income/" + incomeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incomeId))
                .andExpect(jsonPath("$.name").value("Salary Income"))
                .andExpect(jsonPath("$.amount").value(1800.00))
                .andExpect(jsonPath("$.description").value("Updated salary"))
                .andExpect(jsonPath("$.tags.length()").value(2));

        // Verify the income was updated in the database
        assertThat(incomeRepository.count()).isEqualTo(1);
    }

    @Test
    void update_ShouldReturnNotFound_WhenIncomeDoesNotExist() throws Exception {
        // Given
        IncomeRequest updateRequest = IncomeRequest.builder()
                .amount(new BigDecimal("2000.00"))
                .name("Updated Income")
                .category(IncomeCategory.SALARY)
                .description("Updated income")
                .build();

        // When & Then
        mockMvc.perform(put("/api/income/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIncome_ShouldDeleteIncome_WhenIncomeExists() throws Exception {
        // Given
        IncomeRequest request = IncomeRequest.builder()
                .amount(new BigDecimal("1200.00"))
                .name("Income To Be Deleted")
                .category(IncomeCategory.SALARY)
                .description("Income to be deleted")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        IncomeResponse createdIncome = objectMapper.readValue(responseContent, IncomeResponse.class);
        Long incomeId = createdIncome.getId();

        // Verify income exists before deletion
        assertThat(incomeRepository.count()).isEqualTo(1);

        // When & Then
        mockMvc.perform(delete("/api/income/" + incomeId)).andExpect(status().isNoContent());

        // Verify income was deleted from database
        assertThat(incomeRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteIncome_ShouldReturnNotFound_WhenIncomeDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/income/999")).andExpect(status().isNotFound());
    }

    @Test
    void fullWorkflow_ShouldCreateGetUpdateAndDeleteIncome() throws Exception {
        // Create
        IncomeRequest createRequest = IncomeRequest.builder()
                .amount(new BigDecimal("2200.00"))
                .name("Full Workflow Test")
                .category(IncomeCategory.SALARY)
                .description("Full workflow test")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        IncomeResponse createdIncome = objectMapper.readValue(responseContent, IncomeResponse.class);
        Long incomeId = createdIncome.getId();

        // Get by ID
        mockMvc.perform(get("/api/income/" + incomeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incomeId))
                .andExpect(jsonPath("$.name").value("Full Workflow Test"))
                .andExpect(jsonPath("$.amount").value(2200.00));

        // Update
        IncomeRequest updateRequest = IncomeRequest.builder()
                .amount(new BigDecimal("2500.00"))
                .name("Updated Workflow Test")
                .category(IncomeCategory.SALARY)
                .description("Updated workflow test")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES, Tag.OTHER))
                .build();

        mockMvc.perform(put("/api/income/" + incomeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow Test"))
                .andExpect(jsonPath("$.amount").value(2500.00))
                .andExpect(jsonPath("$.description").value("Updated workflow test"));

        // Get all (should have 1 income)
        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        // Delete
        mockMvc.perform(delete("/api/income/" + incomeId)).andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    private void createTestIncome(String description, BigDecimal amount, IncomeCategory category) throws Exception {
        IncomeRequest request = IncomeRequest.builder()
                .amount(amount)
                .name("Test Income")
                .category(category)
                .description(description)
                .tags(Arrays.asList(Tag.OTHER))
                .build();

        mockMvc.perform(post("/api/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

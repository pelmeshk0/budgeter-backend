package com.radomskyi.budgeter.dto;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Request object for creating or updating an expense")
public class ExpenseRequest {

    @Schema(description = "The monetary value of the expense", example = "25.50", required = true)
    @NotNull
    @Positive
    private BigDecimal value;

    @Schema(description = "Category classification of the expense", example = "NEEDS", required = true)
    @NotNull
    private Category category;

    @Schema(description = "Optional description of the expense", example = "Lunch at restaurant", maxLength = 500)
    private String description;

    @Schema(description = "Optional tags for categorizing the expense", example = "[\"FOOD\", \"BARS_AND_RESTAURANTS\"]")
    private List<Tag> tags;

    // Constructors
    public ExpenseRequest() {}

    public ExpenseRequest(BigDecimal value, Category category) {
        this.value = value;
        this.category = category;
    }

    // Getters and Setters
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}

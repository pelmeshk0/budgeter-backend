package com.radomskyi.budgeter.dto;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response object for expense data")
public class ExpenseResponse {

    @Schema(description = "Unique identifier of the expense", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "The monetary value of the expense", example = "25.50")
    private BigDecimal value;

    @Schema(description = "Category classification of the expense", example = "NEEDS")
    private Category category;

    @Schema(description = "Description of the expense", example = "Lunch at restaurant")
    private String description;

    @Schema(description = "Tags for categorizing the expense", example = "[\"FOOD\", \"BARS_AND_RESTAURANTS\"]")
    private List<Tag> tags;

    @Schema(description = "Timestamp when the expense was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the expense was last updated", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    // Constructors
    public ExpenseResponse() {}

    public ExpenseResponse(String id, BigDecimal value, Category category, String description, List<Tag> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.value = value;
        this.category = category;
        this.description = description;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

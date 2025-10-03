package com.radomskyi.budgeter.dto;

import com.radomskyi.budgeter.domain.ExpenseCategory;
import com.radomskyi.budgeter.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating an expense")
public class ExpenseRequest {
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Expense amount", example = "25.50")
    private BigDecimal amount;
    
    @NotNull(message = "Category is required")
    @Schema(description = "Expense category")
    private ExpenseCategory category;
    
    @Schema(description = "Expense description", example = "Lunch at restaurant", maxLength = 333)
    private String description;
    
    @Schema(description = "List of tags associated with the expense")
    private List<Tag> tags;
}

package com.radomskyi.budgeter.dto;

import com.radomskyi.budgeter.domain.IncomeCategory;
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
@Schema(description = "Request DTO for creating or updating an income")
public class IncomeRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Income amount", example = "3500.00")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    @Schema(description = "Income category")
    private IncomeCategory category;

    @Schema(description = "Income description", example = "Monthly salary", maxLength = 333)
    private String description;

    @Schema(description = "List of tags associated with the income")
    private List<Tag> tags;
}

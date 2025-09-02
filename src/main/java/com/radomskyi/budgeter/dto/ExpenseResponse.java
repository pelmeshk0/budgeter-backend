package com.radomskyi.budgeter.dto;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for expense data")
public class ExpenseResponse {
    
    @Schema(description = "Unique identifier of the expense", example = "1")
    private Long id;
    
    @Schema(description = "Expense amount", example = "25.50")
    private BigDecimal amount;
    
    @Schema(description = "Expense category")
    private Category category;
    
    @Schema(description = "Expense description", example = "Lunch at restaurant")
    private String description;
    
    @Schema(description = "List of tags associated with the expense")
    private List<Tag> tags;
    
    @Schema(description = "Date and time when the expense was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date and time when the expense was last updated")
    private LocalDateTime updatedAt;
}

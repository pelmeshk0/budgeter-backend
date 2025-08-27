package com.radomskyi.budgeter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    
    @Id
    private String id;
    
    @NotNull
    @Positive
    private BigDecimal value;
    
    @NotNull
    @Indexed
    private Category category;
    
    private String description;
    
    private List<Tag> tags;
    
    @Indexed
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Custom constructor for required fields
    public Expense(BigDecimal value, Category category) {
        this.value = value;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Pre-save hook
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
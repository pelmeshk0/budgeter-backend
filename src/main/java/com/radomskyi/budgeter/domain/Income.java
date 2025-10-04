package com.radomskyi.budgeter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "income")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Income extends Transaction {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private IncomeCategory category;

    @ElementCollection(targetClass = Tag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "income_tags", joinColumns = @JoinColumn(name = "income_id"))
    @Column(name = "tag", length = 30)
    private List<Tag> tags;

    // Custom constructor for required fields
    public Income(BigDecimal amount, IncomeCategory category) {
        super.amount = amount;
        this.category = category;
    }

    // Constructor with description
    public Income(BigDecimal amount, IncomeCategory category, String description) {
        super.amount = amount;
        super.description = description;
        this.category = category;
    }
}

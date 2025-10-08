package com.radomskyi.budgeter.domain.entity.budgeting;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "income")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Income extends Transaction {

    // todo review if I want to allow categories to be empty
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private IncomeCategory category;

    // todo review I want to split tags too
    @ElementCollection(targetClass = Tag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "income_tags", joinColumns = @JoinColumn(name = "income_id"))
    @Column(name = "tag", length = 30)
    private List<Tag> tags;

    // Custom constructor for required fields
    public Income(BigDecimal amount, IncomeCategory category, String name) {
        super.amount = amount;
        super.name = name;
        this.category = category;
    }

    // Constructor with description
    public Income(BigDecimal amount, IncomeCategory category, String name, String description) {
        super.amount = amount;
        super.name = name;
        super.description = description;
        this.category = category;
    }
}

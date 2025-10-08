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
@Table(name = "expense")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Expense extends Transaction {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ExpenseCategory category;

    @ElementCollection(targetClass = Tag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "expense_tags", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "tag", length = 30)
    private List<Tag> tags;

    // Custom constructor for required fields
    public Expense(BigDecimal amount, ExpenseCategory category, String name) {
        super.amount = amount;
        super.name = name;
        this.category = category;
    }

    // Constructor with description
    public Expense(BigDecimal amount, ExpenseCategory category, String name, String description) {
        super.amount = amount;
        super.name = name;
        super.description = description;
        this.category = category;
    }
}

package com.radomskyi.budgeter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.radomskyi.budgeter.domain.entity.budgeting.Expense;
import com.radomskyi.budgeter.domain.entity.budgeting.ExpenseCategory;
import com.radomskyi.budgeter.domain.entity.budgeting.Tag;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ExpenseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExpenseRepository expenseRepository;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    @BeforeEach
    void setUp() {
        // Create test expenses
        expense1 = Expense.builder()
                .amount(new BigDecimal("100.00"))
                .category(ExpenseCategory.FIXED)
                .description("Rent payment")
                .tags(Arrays.asList(Tag.HOUSING))
                .build();

        expense2 = Expense.builder()
                .amount(new BigDecimal("50.00"))
                .category(ExpenseCategory.NEEDS)
                .description("Grocery shopping")
                .tags(Arrays.asList(Tag.FOOD))
                .build();

        expense3 = Expense.builder()
                .amount(new BigDecimal("25.00"))
                .category(ExpenseCategory.WANTS)
                .description("Movie tickets")
                .tags(Arrays.asList(Tag.ENTERTAINMENT))
                .build();

        // Persist test data
        entityManager.persistAndFlush(expense1);
        entityManager.persistAndFlush(expense2);
        entityManager.persistAndFlush(expense3);
    }

    @Test
    void testFindByCategory() {
        List<Expense> fixedExpenses = expenseRepository.findByCategory(ExpenseCategory.FIXED);
        assertThat(fixedExpenses).hasSize(1);
        assertThat(fixedExpenses.get(0).getDescription()).isEqualTo("Rent payment");

        List<Expense> needsExpenses = expenseRepository.findByCategory(ExpenseCategory.NEEDS);
        assertThat(needsExpenses).hasSize(1);
        assertThat(needsExpenses.get(0).getDescription()).isEqualTo("Grocery shopping");
    }

    @Test
    void testFindByAmountGreaterThan() {
        List<Expense> expensiveExpenses = expenseRepository.findByAmountGreaterThan(new BigDecimal("75.00"));
        assertThat(expensiveExpenses).hasSize(1);
        assertThat(expensiveExpenses.get(0).getDescription()).isEqualTo("Rent payment");
    }

    @Test
    void testFindByAmountBetween() {
        List<Expense> mediumExpenses =
                expenseRepository.findByAmountBetween(new BigDecimal("20.00"), new BigDecimal("60.00"));
        assertThat(mediumExpenses).hasSize(2);
    }

    @Test
    void testFindByDescriptionContainingIgnoreCase() {
        List<Expense> rentExpenses = expenseRepository.findByDescriptionContainingIgnoreCase("rent");
        assertThat(rentExpenses).hasSize(1);
        assertThat(rentExpenses.get(0).getDescription()).isEqualTo("Rent payment");
    }

    @Test
    void testFindByTag() {
        List<Expense> housingExpenses = expenseRepository.findByTag(Tag.HOUSING);
        assertThat(housingExpenses).hasSize(1);
        assertThat(housingExpenses.get(0).getDescription()).isEqualTo("Rent payment");

        List<Expense> foodExpenses = expenseRepository.findByTag(Tag.FOOD);
        assertThat(foodExpenses).hasSize(1);
        assertThat(foodExpenses.get(0).getDescription()).isEqualTo("Grocery shopping");
    }

    @Test
    void testSumByCategory() {
        BigDecimal totalFixed = expenseRepository.sumByCategory(ExpenseCategory.FIXED);
        assertThat(totalFixed).isEqualTo(new BigDecimal("100.00"));

        BigDecimal totalNeeds = expenseRepository.sumByCategory(ExpenseCategory.NEEDS);
        assertThat(totalNeeds).isEqualTo(new BigDecimal("50.00"));

        BigDecimal totalWants = expenseRepository.sumByCategory(ExpenseCategory.WANTS);
        assertThat(totalWants).isEqualTo(new BigDecimal("25.00"));
    }

    @Test
    void testFindAllByOrderByCreatedAtDesc() {
        List<Expense> expenses = expenseRepository.findAllByOrderByCreatedAtDesc();
        assertThat(expenses).hasSize(3);

        // Verify that all expected expenses are present
        assertThat(expenses)
                .extracting(Expense::getDescription)
                .containsExactlyInAnyOrder("Rent payment", "Grocery shopping", "Movie tickets");

        // Verify that the results are ordered by creation time (newest first)
        // Since creation timestamps might be identical in tests, we verify the overall ordering pattern
        assertThat(expenses.get(0).getCreatedAt())
                .isAfterOrEqualTo(expenses.get(1).getCreatedAt());
        assertThat(expenses.get(1).getCreatedAt())
                .isAfterOrEqualTo(expenses.get(2).getCreatedAt());
    }

    @Test
    void testFindAllByOrderByAmountDesc() {
        List<Expense> expenses = expenseRepository.findAllByOrderByAmountDesc();
        assertThat(expenses).hasSize(3);
        // The highest amount expense should be first
        assertThat(expenses.get(0).getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(expenses.get(0).getDescription()).isEqualTo("Rent payment");
    }
}

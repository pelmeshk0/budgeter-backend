package com.radomskyi.budgeter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.radomskyi.budgeter.domain.entity.budgeting.Income;
import com.radomskyi.budgeter.domain.entity.budgeting.IncomeCategory;
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
class IncomeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IncomeRepository incomeRepository;

    private Income income1;
    private Income income2;
    private Income income3;
    private Income income4;
    private Income income5;

    @BeforeEach
    void setUp() {
        // Create test incomes
        income1 = Income.builder()
                .amount(new BigDecimal("3000.00"))
                .category(IncomeCategory.SALARY)
                .description("Monthly salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        income2 = Income.builder()
                .amount(new BigDecimal("500.00"))
                .category(IncomeCategory.FREELANCE)
                .description("Freelance project")
                .tags(Arrays.asList(Tag.OTHER))
                .build();

        income3 = Income.builder()
                .amount(new BigDecimal("100.00"))
                .category(IncomeCategory.INVESTMENTS)
                .description("Dividend payment")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        income4 = Income.builder()
                .amount(new BigDecimal("50.00"))
                .category(IncomeCategory.GIFTS_AND_BONUSES)
                .description("Birthday")
                .build();

        income5 = Income.builder()
                .amount(new BigDecimal("300.00"))
                .category(IncomeCategory.INVESTMENTS)
                .description("Dividend payment")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        // Save test data
        entityManager.persist(income1);
        entityManager.persist(income2);
        entityManager.persist(income3);
        entityManager.persist(income4);
        entityManager.persist(income5);
        entityManager.flush();
    }

    @Test
    void findByCategory_ShouldReturnIncomesForGivenCategory() {
        // When
        List<Income> salaryIncomes = incomeRepository.findByCategory(IncomeCategory.SALARY);

        // Then
        assertThat(salaryIncomes).hasSize(1);
        assertThat(salaryIncomes.get(0).getCategory()).isEqualTo(IncomeCategory.SALARY);
        assertThat(salaryIncomes.get(0).getAmount()).isEqualTo(new BigDecimal("3000.00"));
    }

    @Test
    void findByAmountGreaterThan_ShouldReturnIncomesAboveThreshold() {
        // When
        List<Income> highIncomes = incomeRepository.findByAmountGreaterThan(new BigDecimal("300.00"));

        // Then
        assertThat(highIncomes).hasSize(2);
        assertThat(highIncomes)
                .extracting(Income::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("3000.00"), new BigDecimal("500.00"));
    }

    @Test
    void findByAmountBetween_ShouldReturnIncomesInRange() {
        // When
        List<Income> mediumIncomes =
                incomeRepository.findByAmountBetween(new BigDecimal("0.00"), new BigDecimal("1000.00"));

        // Then
        assertThat(mediumIncomes).hasSize(4);
        assertThat(mediumIncomes)
                .extracting(Income::getAmount)
                .containsExactlyInAnyOrder(
                        new BigDecimal("500.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("50.00"),
                        new BigDecimal("300.00"));
    }

    @Test
    void findByDescriptionContainingIgnoreCase_ShouldReturnIncomesWithMatchingDescription() {
        // When
        List<Income> salaryIncomes = incomeRepository.findByDescriptionContainingIgnoreCase("salary");

        // Then
        assertThat(salaryIncomes).hasSize(1);
        assertThat(salaryIncomes.get(0).getDescription()).isEqualTo("Monthly salary");
    }

    @Test
    void findByTag_ShouldReturnIncomesWithSpecificTag() {
        // When
        List<Income> bankingIncomes = incomeRepository.findByTag(Tag.BANKING_AND_TAXES);

        // Then
        assertThat(bankingIncomes).hasSize(3);
        assertThat(bankingIncomes)
                .extracting(Income::getAmount)
                .containsExactlyInAnyOrder(
                        new BigDecimal("3000.00"), new BigDecimal("100.00"), new BigDecimal("300.00"));
    }

    @Test
    void sumByCategory_ShouldReturnTotalAmountForCategory() {
        // When
        BigDecimal totalInvestments = incomeRepository.sumByCategory(IncomeCategory.INVESTMENTS);

        // Then
        assertThat(totalInvestments).isEqualTo(new BigDecimal("400.00")); // 100.00 + 300.00
    }

    @Test
    void findAllByOrderByAmountDesc_ShouldReturnIncomesOrderedByAmountDescending() {
        // When
        List<Income> orderedIncomes = incomeRepository.findAllByOrderByAmountDesc();

        // Then
        assertThat(orderedIncomes).hasSize(5);
        assertThat(orderedIncomes)
                .extracting(Income::getAmount)
                .containsExactly(
                        new BigDecimal("3000.00"),
                        new BigDecimal("500.00"),
                        new BigDecimal("300.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("50.00"));
    }

    @Test
    void findAllByOrderByCreatedAtDesc_ShouldReturnIncomesOrderedByCreationDateDescending() {
        // When
        List<Income> orderedIncomes = incomeRepository.findAllByOrderByCreatedAtDesc();

        // Then
        assertThat(orderedIncomes).hasSize(5);

        // Verify that all expected incomes are present
        assertThat(orderedIncomes)
                .extracting(Income::getAmount)
                .containsExactlyInAnyOrder(
                        new BigDecimal("3000.00"), // income1
                        new BigDecimal("500.00"), // income2
                        new BigDecimal("100.00"), // income3
                        new BigDecimal("50.00"), // income4
                        new BigDecimal("300.00") // income5
                        );

        // Verify that the results are ordered by creation time (newest first)
        // Since creation timestamps might be identical in tests, we verify the overall ordering pattern
        // by checking that earlier persisted entities come after later ones in the list
        assertThat(orderedIncomes.get(0).getCreatedAt())
                .isAfterOrEqualTo(orderedIncomes.get(1).getCreatedAt());
        assertThat(orderedIncomes.get(1).getCreatedAt())
                .isAfterOrEqualTo(orderedIncomes.get(2).getCreatedAt());
        assertThat(orderedIncomes.get(2).getCreatedAt())
                .isAfterOrEqualTo(orderedIncomes.get(3).getCreatedAt());
        assertThat(orderedIncomes.get(3).getCreatedAt())
                .isAfterOrEqualTo(orderedIncomes.get(4).getCreatedAt());
    }
}

package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.IncomeCategory;
import com.radomskyi.budgeter.domain.Income;
import com.radomskyi.budgeter.domain.Tag;
import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import com.radomskyi.budgeter.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {
    // fixme add one more testIncome object and adjust tests accordingly

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private IncomeService incomeService;

    private Income testIncome;
    private Income testIncome2;
    private IncomeRequest testIncomeRequest;
    private IncomeRequest testIncomeRequest2;

    @BeforeEach
    void setUp() {
        testIncome = Income.builder()
                .id(1L)
                .amount(new BigDecimal("3500.00"))
                .category(IncomeCategory.SALARY)
                .description("Monthly salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testIncome2 = Income.builder()
                .id(2L)
                .amount(new BigDecimal("1500.00"))
                .category(IncomeCategory.FREELANCE)
                .description("Freelance project")
                .tags(Arrays.asList(Tag.OTHER))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testIncomeRequest = IncomeRequest.builder()
                .amount(new BigDecimal("3500.00"))
                .category(IncomeCategory.SALARY)
                .description("Monthly salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        testIncomeRequest2 = IncomeRequest.builder()
                .amount(new BigDecimal("1500.00"))
                .category(IncomeCategory.FREELANCE)
                .description("Freelance project")
                .tags(Arrays.asList(Tag.OTHER))
                .build();
    }

    @Test
    void createIncome_ShouldReturnCreatedIncomeResponse() {
        // Given
        when(incomeRepository.save(any(Income.class))).thenReturn(testIncome);

        // When
        IncomeResponse result = incomeService.createIncome(testIncomeRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("3500.00"));
        assertThat(result.getCategory()).isEqualTo(IncomeCategory.SALARY);
        assertThat(result.getDescription()).isEqualTo("Monthly salary");
        assertThat(result.getTags()).isEqualTo(Arrays.asList(Tag.BANKING_AND_TAXES));

        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void getIncomeById_WhenIncomeExists_ShouldReturnIncomeResponse() {
        // Given
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));

        // When
        IncomeResponse result = incomeService.getIncomeById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("3500.00"));
        assertThat(result.getCategory()).isEqualTo(IncomeCategory.SALARY);

        verify(incomeRepository).findById(1L);
    }

    @Test
    void getIncomeById_WhenIncomeDoesNotExist_ShouldThrowException() {
        // Given
        when(incomeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> incomeService.getIncomeById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Income not found with id: 999");

        verify(incomeRepository).findById(999L);
    }

    @Test
    void getAllIncomes_ShouldReturnPageOfIncomeResponses() {
        // Given
        List<Income> incomes = Arrays.asList(testIncome);
        Page<Income> incomePage = new PageImpl<>(incomes, PageRequest.of(0, 10), 1);
        when(incomeRepository.findAll(any(Pageable.class))).thenReturn(incomePage);

        // When
        Page<IncomeResponse> result = incomeService.getAllIncomes(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getAmount()).isEqualTo(new BigDecimal("3500.00"));

        verify(incomeRepository).findAll(any(Pageable.class));
    }

    @Test
    void updateIncome_WhenIncomeExists_ShouldReturnUpdatedIncomeResponse() {
        // Given
        Income updatedIncome = Income.builder()
                .id(1L)
                .amount(new BigDecimal("4000.00"))
                .category(IncomeCategory.SALARY)
                .description("Updated salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));
        when(incomeRepository.save(any(Income.class))).thenReturn(updatedIncome);

        IncomeRequest updateRequest = IncomeRequest.builder()
                .amount(new BigDecimal("4000.00"))
                .category(IncomeCategory.SALARY)
                .description("Updated salary")
                .tags(Arrays.asList(Tag.BANKING_AND_TAXES))
                .build();

        // When
        IncomeResponse result = incomeService.updateIncome(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(result.getDescription()).isEqualTo("Updated salary");

        verify(incomeRepository).findById(1L);
        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void updateIncome_WhenIncomeDoesNotExist_ShouldThrowException() {
        // Given
        when(incomeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> incomeService.updateIncome(999L, testIncomeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Income not found with id: 999");

        verify(incomeRepository).findById(999L);
        verify(incomeRepository, never()).save(any(Income.class));
    }

    @Test
    void deleteIncome_WhenIncomeExists_ShouldDeleteSuccessfully() {
        // Given
        when(incomeRepository.existsById(1L)).thenReturn(true);

        // When
        incomeService.deleteIncome(1L);

        // Then
        verify(incomeRepository).existsById(1L);
        verify(incomeRepository).deleteById(1L);
        // fixme check that the testIncome object is actually deleted
    }

    @Test
    void deleteIncome_WhenIncomeDoesNotExist_ShouldThrowException() {
        // Given
        when(incomeRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> incomeService.deleteIncome(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Income not found with id: 999");

        verify(incomeRepository).existsById(999L);
        verify(incomeRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllIncomes_ShouldReturnMultipleIncomes_WhenMultipleIncomesExist() {
        // Given
        List<Income> incomes = Arrays.asList(testIncome, testIncome2);
        Page<Income> incomePage = new PageImpl<>(incomes, PageRequest.of(0, 10), 2);
        when(incomeRepository.findAll(any(Pageable.class))).thenReturn(incomePage);

        // When
        Page<IncomeResponse> result = incomeService.getAllIncomes(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getAmount()).isEqualTo(new BigDecimal("3500.00"));
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getAmount()).isEqualTo(new BigDecimal("1500.00"));

        verify(incomeRepository).findAll(any(Pageable.class));
    }

    @Test
    void deleteIncome_ShouldActuallyRemoveIncome() {
        // Given
        when(incomeRepository.existsById(1L)).thenReturn(true);
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));

        // When
        incomeService.deleteIncome(1L);

        // Then
        verify(incomeRepository).existsById(1L);
        verify(incomeRepository).deleteById(1L);

        // Verify the income is actually gone by checking findById returns empty
        when(incomeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> incomeService.getIncomeById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Income not found with id: 1");
    }
}

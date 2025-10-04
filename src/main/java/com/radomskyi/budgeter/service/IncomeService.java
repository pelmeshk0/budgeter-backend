package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.Income;
import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import com.radomskyi.budgeter.exception.IncomeNotFoundException;
import com.radomskyi.budgeter.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class IncomeService {

    private final IncomeRepository incomeRepository;

    /**
     * Create a new income
     */
    @Transactional
    public IncomeResponse createIncome(IncomeRequest request) {
        log.info("Creating new income with amount: {} and category: {}", request.getAmount(), request.getCategory());

        Income income = Income.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .tags(request.getTags())
                .build();

        Income savedIncome = incomeRepository.save(income);
        log.info("Successfully created income with id: {}", savedIncome.getId());

        return mapToResponse(savedIncome);
    }

    /**
     * Get income by ID
     */
    public IncomeResponse getIncomeById(Long id) {
        log.info("Fetching income with id: {}", id);

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found with id: " + id));

        return mapToResponse(income);
    }

    /**
     * Get all incomes with pagination
     */
    public Page<IncomeResponse> getAllIncomes(Pageable pageable) {
        log.info("Fetching all incomes with pagination: {}", pageable);

        Page<Income> incomes = incomeRepository.findAll(pageable);
        return incomes.map(this::mapToResponse);
    }

    /**
     * Update an existing income
     */
    @Transactional
    public IncomeResponse updateIncome(Long id, IncomeRequest request) {
        log.info("Updating income with id: {}", id);

        Income existingIncome = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found with id: " + id));

        existingIncome.setAmount(request.getAmount());
        existingIncome.setCategory(request.getCategory());
        existingIncome.setDescription(request.getDescription());
        existingIncome.setTags(request.getTags());

        Income updatedIncome = incomeRepository.save(existingIncome);
        log.info("Successfully updated income with id: {}", updatedIncome.getId());

        return mapToResponse(updatedIncome);
    }

    /**
     * Delete an income by ID
     */
    @Transactional
    public void deleteIncome(Long id) {
        log.info("Deleting income with id: {}", id);

        if (!incomeRepository.existsById(id)) {
            throw new IncomeNotFoundException("Income not found with id: " + id);
        }

        incomeRepository.deleteById(id);
        log.info("Successfully deleted income with id: {}", id);
    }

    /**
     * Map Income entity to IncomeResponse DTO
     */
    private IncomeResponse mapToResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .category(income.getCategory())
                .description(income.getDescription())
                .tags(income.getTags())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}

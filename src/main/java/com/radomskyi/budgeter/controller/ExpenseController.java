package com.radomskyi.budgeter.controller;

import com.radomskyi.budgeter.domain.controller.ExpenseControllerInterface;
import com.radomskyi.budgeter.dto.ExpenseRequest;
import com.radomskyi.budgeter.dto.ExpenseResponse;
import com.radomskyi.budgeter.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController implements ExpenseControllerInterface {

    private final ExpenseService expenseService;

    @PostMapping
    @Override
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        log.info("Received request to create expense: {}", request);
        ExpenseResponse response = expenseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ExpenseResponse> getById(@PathVariable Long id) {
        log.info("Received request to get expense with id: {}", id);
        ExpenseResponse response = expenseService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<ExpenseResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to get all expenses with pagination: {}", pageable);
        Page<ExpenseResponse> response = expenseService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        log.info("Received request to update expense with id: {} and data: {}", id, request);
        ExpenseResponse response = expenseService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request to delete expense with id: {}", id);
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

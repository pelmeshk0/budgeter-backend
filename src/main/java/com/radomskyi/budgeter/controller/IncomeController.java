package com.radomskyi.budgeter.controller;

import com.radomskyi.budgeter.domain.controller.IncomeControllerInterface;
import com.radomskyi.budgeter.dto.IncomeRequest;
import com.radomskyi.budgeter.dto.IncomeResponse;
import com.radomskyi.budgeter.service.IncomeService;
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
@RequestMapping("/api/income")
@RequiredArgsConstructor
@Slf4j
public class IncomeController implements IncomeControllerInterface {

    private final IncomeService incomeService;

    @PostMapping
    @Override
    public ResponseEntity<IncomeResponse> create(@Valid @RequestBody IncomeRequest request) {
        log.info("Received request to create income: {}", request);
        IncomeResponse response = incomeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<IncomeResponse> getById(@PathVariable Long id) {
        log.info("Received request to get income with id: {}", id);
        IncomeResponse response = incomeService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<IncomeResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to get all incomes with pagination: {}", pageable);
        Page<IncomeResponse> response = incomeService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<IncomeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        log.info("Received request to update income with id: {} and data: {}", id, request);
        IncomeResponse response = incomeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request to delete income with id: {}", id);
        incomeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

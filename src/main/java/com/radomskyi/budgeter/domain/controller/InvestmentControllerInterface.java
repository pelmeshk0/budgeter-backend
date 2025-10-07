package com.radomskyi.budgeter.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Banking documents import controller", description = "Responsible for importing files from apps like Trading212, Trade Repulic, Finanz Guru, etc.")
// fixme change this to /api/import
@RequestMapping("/api/investment-transactions")
// fixme rename this to ImportControllerInterface (and also change all other related classes in controller/service/repository/tests/etc.)
public interface InvestmentControllerInterface {

    // todo change this to "/import-trading212-orders-csv" (and adjust method signature accordingly)
    @PostMapping("/import-csv")
    @Operation(summary = "Import investment transactions from Trading212 CSV file")
    ResponseEntity<String> importCsv(
            @Parameter(description = "CSV file to import") @RequestParam("file") MultipartFile file);
}

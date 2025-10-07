package com.radomskyi.budgeter.controller;

import com.opencsv.exceptions.CsvException;
import com.radomskyi.budgeter.domain.controller.InvestmentControllerInterface;
import com.radomskyi.budgeter.dto.InvestmentTransactionResponse;
import com.radomskyi.budgeter.service.Trading212CsvImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
// todo change this to import controller (related to todo in InvestmentControllerInterface)
public class InvestmentTransactionController implements InvestmentControllerInterface {

    private final Trading212CsvImportService csvImportService; // rename this variable (and the class) to trading212ImportService/Trading212ImportService.java

    @PostMapping("/import-csv")
    @Override
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        log.info("Received request to import CSV file: {}", file.getOriginalFilename());

        try {
            List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

            String message = String.format("Successfully imported %d investment transactions from CSV file '%s'",
                    importedTransactions.size(), file.getOriginalFilename());

            log.info(message);
            return ResponseEntity.ok(message);

        } catch (IOException e) {
            String errorMessage = "Failed to read CSV file: " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);

        } catch (CsvException e) {
            String errorMessage = "Failed to parse CSV file: " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);

        } catch (Exception e) {
            String errorMessage = "Failed to import CSV file: " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}

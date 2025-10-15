package com.radomskyi.budgeter.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.radomskyi.budgeter.domain.entity.investment.Currency;
import com.radomskyi.budgeter.domain.entity.investment.InvestmentTransaction;
import com.radomskyi.budgeter.domain.entity.investment.InvestmentTransactionType;
import com.radomskyi.budgeter.dto.InvestmentTransactionRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class Trading212CsvImportService {

    private final InvestmentService investmentService;

    // CSV column indices (0-based)
    private static final int ACTION_INDEX = 0;
    private static final int TIME_INDEX = 1;
    private static final int ISIN_INDEX = 2;
    private static final int TICKER_INDEX = 3;
    private static final int NAME_INDEX = 4;
    private static final int ID_INDEX = 5;
    private static final int UNITS_INDEX = 6;
    private static final int PRICE_PER_UNIT_INDEX = 7;
    private static final int CURRENCY_PRICE_INDEX = 8;
    private static final int EXCHANGE_RATE_INDEX = 9;
    private static final int RESULT_INDEX = 10;
    private static final int CURRENCY_RESULT_INDEX = 11;
    private static final int GROSS_TOTAL_INDEX = 12;
    private static final int CURRENCY_GROSS_TOTAL_INDEX = 13;
    private static final int WITHHOLDING_TAX_INDEX = 14;
    private static final int CURRENCY_WITHHOLDING_TAX_INDEX = 15;
    private static final int CURRENCY_CONVERSION_FEE_INDEX = 16;
    private static final int CURRENCY_CURRENCY_CONVERSION_FEE_INDEX = 17;

    private static final DateTimeFormatter TRADING212_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /** Import Trading212 CSV file and create investment transactions */
    @Transactional
    public List<InvestmentTransaction> importCsvFile(MultipartFile file) throws IOException, CsvException {
        log.info("Starting CSV import for file: {}", file.getOriginalFilename());

        List<InvestmentTransaction> importedTransactions = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = csvReader.readAll();

            if (rows.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                if (row.length < 18) {
                    log.warn("Skipping row {}: insufficient columns", i);
                    continue;
                }

                try {
                    InvestmentTransaction transaction = processCsvRow(row);
                    if (transaction != null) {
                        importedTransactions.add(transaction);
                    }
                } catch (Exception e) {
                    log.error("Error processing row {}: {}", i, e.getMessage());
                    // Continue processing other rows
                }
            }
        }

        log.info("Successfully imported {} transactions from CSV file", importedTransactions.size());
        return importedTransactions;
    }

    /** Process a single CSV row and create an investment transaction */
    private InvestmentTransaction processCsvRow(String[] row) {
        try {
            // Parse basic transaction data
            String action = row[ACTION_INDEX].trim();
            String ticker = row[TICKER_INDEX].trim();
            String name = row[NAME_INDEX].trim();
            String isin = row[ISIN_INDEX].trim();

            // Parse numerical values
            BigDecimal units = parseBigDecimal(row[UNITS_INDEX]);
            BigDecimal pricePerUnit = parseBigDecimal(row[PRICE_PER_UNIT_INDEX]);
            BigDecimal exchangeRate = parseBigDecimal(row[EXCHANGE_RATE_INDEX]);
            BigDecimal grossTotal = parseBigDecimal(row[GROSS_TOTAL_INDEX]);

            // Parse currency
            String currencyStr = row[CURRENCY_GROSS_TOTAL_INDEX].trim();
            Currency currency = parseCurrency(currencyStr);

            // Parse fees (withholding tax + currency conversion fee)
            BigDecimal withholdingTax = parseBigDecimal(row[WITHHOLDING_TAX_INDEX]);
            BigDecimal conversionFee = parseBigDecimal(row[CURRENCY_CONVERSION_FEE_INDEX]);
            BigDecimal totalFees = (withholdingTax != null ? withholdingTax : BigDecimal.ZERO)
                    .add(conversionFee != null ? conversionFee : BigDecimal.ZERO);

            // Determine transaction type
            InvestmentTransactionType transactionType = determineTransactionType(action);

            // Create request DTO
            InvestmentTransactionRequest request = InvestmentTransactionRequest.builder()
                    .transactionType(transactionType)
                    .assetTicker(ticker)
                    .assetName(name)
                    .assetIsin(isin)
                    .units(units)
                    .pricePerUnit(pricePerUnit)
                    .fees(totalFees.compareTo(BigDecimal.ZERO) > 0 ? totalFees : null)
                    .currency(currency)
                    .exchangeRate(exchangeRate)
                    .name(name + " " + ticker)
                    .description("Imported from Trading212 CSV: " + action)
                    .brokerage("Trading212")
                    .build();

            // Validate required fields
            validateTransactionData(request);

            // Create transaction using investment service
            return investmentService.create(request);

        } catch (Exception e) {
            log.error("Error processing CSV row: {}", e.getMessage());
            throw new RuntimeException("Failed to process CSV row: " + e.getMessage(), e);
        }
    }

    /** Parse BigDecimal from string, handling empty/null values */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return null;
        }

        try {
            // Remove any non-numeric characters except decimal point and minus sign
            String cleaned = value.replaceAll("[^0-9.-]", "");
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Could not parse BigDecimal from value: {}", value);
            return null;
        }
    }

    /** Parse currency from string */
    private Currency parseCurrency(String currencyStr) {
        if (currencyStr == null || currencyStr.trim().isEmpty()) {
            return Currency.EUR; // Default to EUR
        }

        try {
            return Currency.valueOf(currencyStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown currency: {}, defaulting to EUR", currencyStr);
            return Currency.EUR;
        }
    }

    /** Determine transaction type from action string */
    private InvestmentTransactionType determineTransactionType(String action) {
        if (action == null || action.trim().isEmpty()) {
            return InvestmentTransactionType.BUY; // Default fallback
        }

        String normalizedAction = action.toLowerCase().trim();

        if (normalizedAction.contains("buy") || normalizedAction.startsWith("market buy")) {
            return InvestmentTransactionType.BUY;
        } else if (normalizedAction.contains("sell") || normalizedAction.startsWith("market sell")) {
            return InvestmentTransactionType.SELL;
        } else if (normalizedAction.contains("dividend")) {
            return InvestmentTransactionType.DIVIDEND;
        } else {
            log.warn("Unknown action type: {}, defaulting to BUY", action);
            return InvestmentTransactionType.BUY;
        }
    }

    /** Validate that required transaction data is present */
    private void validateTransactionData(InvestmentTransactionRequest request) {
        if (request.getAssetTicker() == null || request.getAssetTicker().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset ticker is required");
        }

        if (request.getAssetName() == null || request.getAssetName().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name is required");
        }

        if (request.getUnits() == null || request.getUnits().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid units are required");
        }

        if (request.getPricePerUnit() == null || request.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid price per unit is required");
        }

        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("Currency is required");
        }
    }
}

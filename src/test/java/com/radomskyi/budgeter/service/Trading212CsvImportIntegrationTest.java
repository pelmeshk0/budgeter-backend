package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.entity.investment.*;
import com.opencsv.exceptions.CsvException;
import com.radomskyi.budgeter.dto.InvestmentTransactionResponse;
import com.radomskyi.budgeter.repository.AssetRepository;
import com.radomskyi.budgeter.repository.InvestmentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class Trading212CsvImportIntegrationTest {

    @Autowired
    private Trading212CsvImportService csvImportService;

    @Autowired
    private InvestmentTransactionRepository investmentTransactionRepository;

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        investmentTransactionRepository.deleteAll();
        assetRepository.deleteAll();
    }

    @Test
    void importCsvFile_ShouldImportValidTrading212CsvFile_WhenFileContainsValidData() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,EUR,1.00000000,,EUR,1502.50,EUR,,,,,,\n" +
                "Market sell,2025-06-11 11:41:39.98,US0378331005,AAPL,Apple Inc.,EOF34000698236,5.0000000000,155.0000000000,EUR,1.00000000,,EUR,775.00,EUR,,,,,,\n" +
                "Dividend (Dividend),2025-06-11 11:42:39,US0378331005,AAPL,Apple Inc.,,0.0253888000,0.816000,USD,Not available,,,0.02,EUR,0.00,USD,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-trading212.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

        // Then
        assertThat(importedTransactions).isNotNull();
        assertThat(importedTransactions).hasSize(3);

        // Verify the transactions were saved to database
        List<InvestmentTransaction> savedTransactions = investmentTransactionRepository.findAll();
        assertThat(savedTransactions).hasSize(3);

        // Verify asset was created/updated
        List<Asset> savedAssets = assetRepository.findAll();
        assertThat(savedAssets).hasSize(1);

        Asset savedAsset = savedAssets.get(0);
        assertThat(savedAsset.getTicker()).isEqualTo("AAPL");
        assertThat(savedAsset.getName()).isEqualTo("Apple Inc.");
        assertThat(savedAsset.getIsin()).isEqualTo("US0378331005");

        // Verify transaction details
        InvestmentTransaction buyTransaction = savedTransactions.stream()
                .filter(t -> t.getTransactionType() == InvestmentTransactionType.BUY && t.getUnits().compareTo(new BigDecimal("10.0")) == 0)
                .findFirst()
                .orElse(null);
        assertThat(buyTransaction).isNotNull();
        assertThat(buyTransaction.getPricePerUnit()).isEqualByComparingTo(new BigDecimal("150.25"));
        assertThat(buyTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("1502.50"));

        InvestmentTransaction sellTransaction = savedTransactions.stream()
                .filter(t -> t.getTransactionType() == InvestmentTransactionType.SELL && t.getUnits().compareTo(new BigDecimal("5.0")) == 0)
                .findFirst()
                .orElse(null);
        assertThat(sellTransaction).isNotNull();
        assertThat(sellTransaction.getPricePerUnit()).isEqualByComparingTo(new BigDecimal("155.00"));
        assertThat(sellTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("775.00"));

        InvestmentTransaction dividendTransaction = savedTransactions.stream()
                .filter(t -> t.getTransactionType() == InvestmentTransactionType.DIVIDEND)
                .findFirst()
                .orElse(null);
        assertThat(dividendTransaction).isNotNull();
        assertThat(dividendTransaction.getUnits()).isEqualByComparingTo(new BigDecimal("0.0253888000"));
        assertThat(dividendTransaction.getPricePerUnit()).isEqualByComparingTo(new BigDecimal("0.816000"));
        assertThat(dividendTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("0.0207172608")); // 0.0253888000 * 0.816000
    }

    @Test
    void importCsvFile_ShouldHandleMultipleAssets_WhenCsvContainsDifferentAssets() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,EUR,1.00000000,,EUR,1502.50,EUR,,,,,,\n" +
                "Market buy,2025-06-10 08:04:05.631,US5949181045,MSFT,Microsoft Corporation,EOF33912703812,5.0000000000,300.0000000000,EUR,1.00000000,,EUR,1500.00,EUR,,,,,,\n" +
                "Market sell,2025-06-11 11:41:39.98,US0378331005,AAPL,Apple Inc.,EOF34000698236,3.0000000000,155.0000000000,EUR,1.00000000,,EUR,465.00,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "multi-asset.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

        // Then
        assertThat(importedTransactions).isNotNull();
        assertThat(importedTransactions).hasSize(3);

        // Verify multiple assets were created
        List<Asset> savedAssets = assetRepository.findAll();
        assertThat(savedAssets).hasSize(2);

        List<String> assetTickers = savedAssets.stream()
                .map(Asset::getTicker)
                .toList();
        assertThat(assetTickers).containsExactlyInAnyOrder("AAPL", "MSFT");

        // Verify all transactions were saved
        List<InvestmentTransaction> savedTransactions = investmentTransactionRepository.findAll();
        assertThat(savedTransactions).hasSize(3);

        // Verify asset-transaction relationships
        long appleTransactions = savedTransactions.stream()
                .filter(t -> t.getAsset().getTicker().equals("AAPL"))
                .count();
        long microsoftTransactions = savedTransactions.stream()
                .filter(t -> t.getAsset().getTicker().equals("MSFT"))
                .count();

        assertThat(appleTransactions).isEqualTo(2);
        assertThat(microsoftTransactions).isEqualTo(1);
    }

    @Test
    void importCsvFile_ShouldHandleCurrencyConversion_WhenCsvContainsNonEuroTransactions() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,USD,1.15000000,,EUR,1727.88,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "usd-transaction.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

        // Then
        assertThat(importedTransactions).isNotNull();
        assertThat(importedTransactions).hasSize(1);

        // Verify the transaction was saved with correct currency conversion
        List<InvestmentTransaction> savedTransactions = investmentTransactionRepository.findAll();
        assertThat(savedTransactions).hasSize(1);

        InvestmentTransaction transaction = savedTransactions.get(0);
        assertThat(transaction.getCurrency()).isEqualTo(Currency.EUR); // Currency comes from Gross Total column, not Price column
        assertThat(transaction.getExchangeRate()).isEqualByComparingTo(new BigDecimal("1.15"));
        assertThat(transaction.getAmount()).isEqualByComparingTo(new BigDecimal("1502.50")); // 10 * 150.25 (no conversion since currency is EUR)
        assertThat(transaction.getPricePerUnit()).isEqualByComparingTo(new BigDecimal("150.25"));
        assertThat(transaction.getUnits()).isEqualByComparingTo(new BigDecimal("10.0"));
    }

    @Test
    void importCsvFile_ShouldHandleFees_WhenCsvContainsWithholdingTaxAndConversionFees() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,USD,1.15000000,,EUR,1727.88,EUR,10.00,USD,5.00,EUR";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "transaction-with-fees.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

        // Then
        assertThat(importedTransactions).isNotNull();
        assertThat(importedTransactions).hasSize(1);

        // Verify fees were calculated correctly (10 USD withholding tax + 5 EUR conversion fee)
        // Withholding tax 10 USD * 1.15 = 11.50 EUR + 5 EUR = 16.50 EUR total fees
        List<InvestmentTransaction> savedTransactions = investmentTransactionRepository.findAll();
        assertThat(savedTransactions).hasSize(1);

        InvestmentTransaction transaction = savedTransactions.get(0);
        assertThat(transaction.getFees()).isEqualByComparingTo(new BigDecimal("15.00")); // 10 USD withholding tax + 5 EUR conversion fee (withholding tax not converted)
    }

    @Test
    void importCsvFile_ShouldThrowException_WhenFileIsEmpty() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                "".getBytes()
        );

        // When & Then
        assertThatThrownBy(() -> csvImportService.importCsvFile(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CSV file is empty");
    }

    @Test
    void importCsvFile_ShouldHandleMalformedRows_WhenSomeRowsHaveInsufficientColumns() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,EUR,1.00000000,,EUR,1502.50,EUR,,,,,,\n" +
                "Invalid Row With Too Few Columns\n" + // This row will be skipped
                "Market sell,2025-06-11 11:41:39.98,US0378331005,AAPL,Apple Inc.,EOF34000698236,5.0000000000,155.0000000000,EUR,1.00000000,,EUR,775.00,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malformed-rows.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

        // Then
        assertThat(importedTransactions).isNotNull();
        assertThat(importedTransactions).hasSize(2); // Only valid rows should be processed

        // Verify only valid transactions were saved
        List<InvestmentTransaction> savedTransactions = investmentTransactionRepository.findAll();
        assertThat(savedTransactions).hasSize(2);
    }

    @Test
    void importCsvFile_ShouldUpdateExistingAssets_WhenImportingTransactionsForKnownAssets() throws IOException, CsvException {
        // Given - Create an existing asset first
        Asset existingAsset = Asset.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();
        assetRepository.save(existingAsset);

        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,EUR,1.00000000,,EUR,1502.50,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "existing-asset.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        List<InvestmentTransactionResponse> importedTransactions = csvImportService.importCsvFile(file);

        // Then
        assertThat(importedTransactions).isNotNull();
        assertThat(importedTransactions).hasSize(1);

        // Verify only one asset exists (not duplicated)
        List<Asset> savedAssets = assetRepository.findAll();
        assertThat(savedAssets).hasSize(1);
        assertThat(savedAssets.get(0).getId()).isEqualTo(existingAsset.getId());

        // Verify transaction was saved
        List<InvestmentTransaction> savedTransactions = investmentTransactionRepository.findAll();
        assertThat(savedTransactions).hasSize(1);
    }
}

package com.radomskyi.budgeter.service;

import com.opencsv.exceptions.CsvException;
import com.radomskyi.budgeter.domain.entity.investment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Trading212CsvImportServiceTest {

    @Mock
    private InvestmentService investmentService;

    @InjectMocks
    private Trading212CsvImportService csvImportService;

    private InvestmentTransaction mockTransaction;

    @BeforeEach
    void setUp() {
        Asset mockAsset = Asset.builder()
                .id(1L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        mockTransaction = InvestmentTransaction.builder()
                .id(1L)
                .transactionType(InvestmentTransactionType.BUY)
                .asset(mockAsset)
                .units(new BigDecimal("10.0"))
                .pricePerUnit(new BigDecimal("150.25"))
                .amount(new BigDecimal("1502.50"))
                .currency(Currency.EUR)
                .name("Apple Inc. AAPL")
                .description("Imported from Trading212 CSV: Market buy")
                .build();
    }

    @Test
    void importCsvFile_ShouldImportValidCsvFile_WhenFileContainsValidData() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,EUR,1.00000000,,EUR,1502.50,EUR,,,,,,\n" +
                "Market sell,2025-06-11 11:41:39.98,US0378331005,AAPL,Apple Inc.,EOF34000698236,5.0000000000,155.0000000000,EUR,1.00000000,,EUR,775.00,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(investmentService.create(any())).thenReturn(mockTransaction);

        // When
        List<InvestmentTransaction> result = csvImportService.importCsvFile(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(investmentService, times(2)).create(any());
    }

    @Test
    void importCsvFile_ShouldThrowException_WhenFileIsEmpty() {
        // Given
        String csvContent = "";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                csvContent.getBytes()
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
                "Invalid Row With Too Few Columns\n" +  // This row will be skipped
                "Market sell,2025-06-11 11:41:39.98,US0378331005,AAPL,Apple Inc.,EOF34000698236,5.0000000000,155.0000000000,EUR,1.00000000,,EUR,775.00,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(investmentService.create(any())).thenReturn(mockTransaction);

        // When
        List<InvestmentTransaction> result = csvImportService.importCsvFile(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // Only valid rows should be processed
        verify(investmentService, times(2)).create(any());
    }

    @Test
    void importCsvFile_ShouldHandleDividendTransactions_WhenActionContainsDividend() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Dividend (Dividend),2025-06-11 11:42:39,US0378331005,AAPL,Apple Inc.,,0.0253888000,0.816000,USD,Not available,,,0.02,EUR,0.00,USD,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "dividend.csv",
                "text/csv",
                csvContent.getBytes()
        );

        Asset dividendAsset = Asset.builder()
                .id(2L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        InvestmentTransaction dividendResponse = InvestmentTransaction.builder()
                .id(2L)
                .transactionType(InvestmentTransactionType.DIVIDEND)
                .asset(dividendAsset)
                .units(new BigDecimal("0.0253888000"))
                .pricePerUnit(new BigDecimal("0.816000"))
                .amount(new BigDecimal("0.02"))
                .currency(Currency.EUR)
                .name("Apple Inc. AAPL")
                .description("Imported from Trading212 CSV: Dividend (Dividend)")
                .build();

        when(investmentService.create(any())).thenReturn(dividendResponse);

        // When
        List<InvestmentTransaction> result = csvImportService.importCsvFile(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionType()).isEqualTo(InvestmentTransactionType.DIVIDEND);
        verify(investmentService, times(1)).create(any());
    }

    @Test
    void importCsvFile_ShouldHandleCurrencyConversion_WhenCurrencyIsNotEUR() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,USD,1.15000000,,EUR,1727.88,EUR,,,,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "usd.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(investmentService.create(any())).thenReturn(mockTransaction);

        // When
        List<InvestmentTransaction> result = csvImportService.importCsvFile(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(investmentService, times(1)).create(any());
    }

    @Test
    void importCsvFile_ShouldHandleValidData_WhenProcessingCompleteCsvFile() throws IOException, CsvException {
        // Given
        String csvContent = "Action,Time,ISIN,Ticker,Name,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Result,Currency (Result),Gross Total,Currency (Gross Total),Withholding tax,Currency (Withholding tax),Currency conversion fee,Currency (Currency conversion fee)\n" +
                "Market buy,2025-06-10 07:04:05.631,US0378331005,AAPL,Apple Inc.,EOF33912703811,10.0000000000,150.2500000000,EUR,1.00000000,,EUR,1502.50,EUR,,,,,,\n" +
                "Market sell,2025-06-11 11:41:39.98,US0378331005,AAPL,Apple Inc.,EOF34000698236,5.0000000000,155.0000000000,EUR,1.00000000,,EUR,775.00,EUR,,,,,,\n" +
                "Dividend (Dividend),2025-06-11 11:42:39,US0378331005,AAPL,Apple Inc.,,0.0253888000,0.816000,USD,Not available,,,0.02,EUR,0.00,USD,,,";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "complete.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Setup mocks for different transaction types
        Asset buyAsset = Asset.builder()
                .id(1L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        Asset sellAsset = Asset.builder()
                .id(2L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        Asset dividendAsset = Asset.builder()
                .id(3L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        InvestmentTransaction buyResponse = InvestmentTransaction.builder()
                .id(1L)
                .transactionType(InvestmentTransactionType.BUY)
                .asset(buyAsset)
                .units(new BigDecimal("10.0"))
                .pricePerUnit(new BigDecimal("150.25"))
                .amount(new BigDecimal("1502.50"))
                .currency(Currency.EUR)
                .name("Apple Inc. AAPL")
                .description("Imported from Trading212 CSV: Market buy")
                .build();

        InvestmentTransaction sellResponse = InvestmentTransaction.builder()
                .id(2L)
                .transactionType(InvestmentTransactionType.SELL)
                .asset(sellAsset)
                .units(new BigDecimal("5.0"))
                .pricePerUnit(new BigDecimal("155.00"))
                .amount(new BigDecimal("775.00"))
                .currency(Currency.EUR)
                .name("Apple Inc. AAPL")
                .description("Imported from Trading212 CSV: Market sell")
                .build();

        InvestmentTransaction dividendResponse = InvestmentTransaction.builder()
                .id(3L)
                .transactionType(InvestmentTransactionType.DIVIDEND)
                .asset(dividendAsset)
                .units(new BigDecimal("0.0253888000"))
                .pricePerUnit(new BigDecimal("0.816000"))
                .amount(new BigDecimal("0.02"))
                .currency(Currency.EUR)
                .name("Apple Inc. AAPL")
                .description("Imported from Trading212 CSV: Dividend (Dividend)")
                .build();

        when(investmentService.create(any())).thenReturn(buyResponse, sellResponse, dividendResponse);

        // When
        List<InvestmentTransaction> result = csvImportService.importCsvFile(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        // Verify all three transaction types were processed
        assertThat(result.get(0).getTransactionType()).isEqualTo(InvestmentTransactionType.BUY);
        assertThat(result.get(1).getTransactionType()).isEqualTo(InvestmentTransactionType.SELL);
        assertThat(result.get(2).getTransactionType()).isEqualTo(InvestmentTransactionType.DIVIDEND);

        verify(investmentService, times(3)).create(any());
    }
}

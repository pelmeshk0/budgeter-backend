package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.entity.investment.*;
import com.radomskyi.budgeter.dto.InvestmentTransactionRequest;
import com.radomskyi.budgeter.dto.InvestmentTransactionResponse;
import com.radomskyi.budgeter.exception.InvestmentTransactionNotFoundException;
import com.radomskyi.budgeter.repository.AssetRepository;
import com.radomskyi.budgeter.repository.InvestmentTransactionRepository;
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
class InvestmentServiceTest {

    @Mock
    private InvestmentTransactionRepository investmentTransactionRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private InvestmentService investmentService;

    private Asset testAsset;
    private InvestmentTransaction testTransaction;
    private InvestmentTransactionRequest testRequest;

    @BeforeEach
    void setUp() {
        testAsset = Asset.builder()
                .id(1L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        testTransaction = InvestmentTransaction.builder()
                .id(1L)
                .transactionType(InvestmentTransactionType.BUY)
                .asset(testAsset)
                .units(new BigDecimal("10.0"))
                .pricePerUnit(new BigDecimal("150.25"))
                .fees(new BigDecimal("2.50"))
                .currency(Currency.EUR)
                .exchangeRate(new BigDecimal("1.0"))
                .amount(new BigDecimal("1502.50"))
                .name("Apple Inc. AAPL")
                .description("Test transaction")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testRequest = InvestmentTransactionRequest.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .assetTicker("AAPL")
                .assetName("Apple Inc.")
                .assetIsin("US0378331005")
                .units(new BigDecimal("10.0"))
                .pricePerUnit(new BigDecimal("150.25"))
                .fees(new BigDecimal("2.50"))
                .currency(Currency.EUR)
                .exchangeRate(new BigDecimal("1.0"))
                .name("Apple Inc. AAPL")
                .description("Test transaction")
                .build();
    }

    @Test
    void create_ShouldReturnInvestmentTransactionResponse_WhenValidRequest() {
        // Given
        when(assetRepository.findByIsin("US0378331005")).thenReturn(Optional.of(testAsset));
        when(investmentTransactionRepository.save(any(InvestmentTransaction.class))).thenReturn(testTransaction);

        // When
        InvestmentTransactionResponse result = investmentService.create(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTransactionType()).isEqualTo(InvestmentTransactionType.BUY);
        assertThat(result.getAssetTicker()).isEqualTo("AAPL");
        assertThat(result.getAssetName()).isEqualTo("Apple Inc.");
        assertThat(result.getUnits()).isEqualTo(new BigDecimal("10.0"));
        assertThat(result.getPricePerUnit()).isEqualTo(new BigDecimal("150.25"));
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("1502.50"));

        verify(assetRepository).findByIsin("US0378331005");
        verify(investmentTransactionRepository).save(any(InvestmentTransaction.class));
    }

    @Test
    void create_ShouldCreateNewAsset_WhenAssetDoesNotExist() {
        // Given
        when(assetRepository.findByIsin("US0378331005")).thenReturn(Optional.empty());
        when(assetRepository.findByTicker("AAPL")).thenReturn(Optional.empty());
        when(assetRepository.save(any(Asset.class))).thenReturn(testAsset);
        when(investmentTransactionRepository.save(any(InvestmentTransaction.class))).thenReturn(testTransaction);

        // When
        InvestmentTransactionResponse result = investmentService.create(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(assetRepository).findByIsin("US0378331005");
        verify(assetRepository).findByTicker("AAPL");
        verify(assetRepository).save(any(Asset.class));
        verify(investmentTransactionRepository).save(any(InvestmentTransaction.class));
    }

    @Test
    void getById_ShouldReturnInvestmentTransactionResponse_WhenTransactionExists() {
        // Given
        when(investmentTransactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When
        InvestmentTransactionResponse result = investmentService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTransactionType()).isEqualTo(InvestmentTransactionType.BUY);
        assertThat(result.getAssetTicker()).isEqualTo("AAPL");
        assertThat(result.getAssetName()).isEqualTo("Apple Inc.");

        verify(investmentTransactionRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenTransactionNotFound() {
        // Given
        when(investmentTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> investmentService.getById(999L))
                .isInstanceOf(InvestmentTransactionNotFoundException.class)
                .hasMessage("Investment transaction not found with id: 999");

        verify(investmentTransactionRepository).findById(999L);
    }

    @Test
    void getAll_ShouldReturnPageOfInvestmentTransactionResponses_WhenTransactionsExist() {
        // Given
        List<InvestmentTransaction> transactions = Arrays.asList(testTransaction);
        Page<InvestmentTransaction> transactionPage = new PageImpl<>(transactions);
        Pageable pageable = PageRequest.of(0, 10);

        when(investmentTransactionRepository.findAll(pageable)).thenReturn(transactionPage);

        // When
        Page<InvestmentTransactionResponse> result = investmentService.getAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getAssetTicker()).isEqualTo("AAPL");

        verify(investmentTransactionRepository).findAll(pageable);
    }

    @Test
    void update_ShouldReturnUpdatedInvestmentTransactionResponse_WhenTransactionExists() {
        // Given
        InvestmentTransactionRequest updateRequest = InvestmentTransactionRequest.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .assetTicker("AAPL")
                .assetName("Apple Inc.")
                .assetIsin("US0378331005")
                .units(new BigDecimal("5.0"))
                .pricePerUnit(new BigDecimal("155.00"))
                .fees(new BigDecimal("1.25"))
                .currency(Currency.EUR)
                .exchangeRate(new BigDecimal("1.0"))
                .name("Apple Inc. AAPL - Updated")
                .description("Updated transaction")
                .build();

        InvestmentTransaction updatedTransaction = InvestmentTransaction.builder()
                .id(1L)
                .transactionType(InvestmentTransactionType.SELL)
                .asset(testAsset)
                .units(new BigDecimal("5.0"))
                .pricePerUnit(new BigDecimal("155.00"))
                .fees(new BigDecimal("1.25"))
                .currency(Currency.EUR)
                .exchangeRate(new BigDecimal("1.0"))
                .amount(new BigDecimal("776.25")) // 5 * 155 + 1.25
                .name("Apple Inc. AAPL - Updated")
                .description("Updated transaction")
                .createdAt(testTransaction.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(investmentTransactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(assetRepository.findByIsin("US0378331005")).thenReturn(Optional.of(testAsset));
        when(investmentTransactionRepository.save(any(InvestmentTransaction.class))).thenReturn(updatedTransaction);

        // When
        InvestmentTransactionResponse result = investmentService.update(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTransactionType()).isEqualTo(InvestmentTransactionType.SELL);
        assertThat(result.getUnits()).isEqualTo(new BigDecimal("5.0"));
        assertThat(result.getPricePerUnit()).isEqualTo(new BigDecimal("155.00"));
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("776.25"));

        verify(investmentTransactionRepository).findById(1L);
        verify(assetRepository).findByIsin("US0378331005");
        verify(investmentTransactionRepository).save(any(InvestmentTransaction.class));
    }

    @Test
    void update_ShouldThrowException_WhenTransactionNotFound() {
        // Given
        when(investmentTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> investmentService.update(999L, testRequest))
                .isInstanceOf(InvestmentTransactionNotFoundException.class)
                .hasMessage("Investment transaction not found with id: 999");

        verify(investmentTransactionRepository).findById(999L);
        verify(assetRepository, never()).findByIsin(any());
        verify(investmentTransactionRepository, never()).save(any(InvestmentTransaction.class));
    }

    @Test
    void delete_ShouldDeleteTransaction_WhenTransactionExists() {
        // Given
        when(investmentTransactionRepository.existsById(1L)).thenReturn(true);

        // When
        investmentService.delete(1L);

        // Then
        verify(investmentTransactionRepository).existsById(1L);
        verify(investmentTransactionRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenTransactionNotFound() {
        // Given
        when(investmentTransactionRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> investmentService.delete(999L))
                .isInstanceOf(InvestmentTransactionNotFoundException.class)
                .hasMessage("Investment transaction not found with id: 999");

        verify(investmentTransactionRepository).existsById(999L);
        verify(investmentTransactionRepository, never()).deleteById(anyLong());
    }
}

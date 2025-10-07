package com.radomskyi.budgeter.domain.entity.investment;

import com.radomskyi.budgeter.domain.entity.investment.*;
import com.radomskyi.budgeter.domain.entity.budgeting.Transaction;
import com.radomskyi.budgeter.repository.InvestmentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InvestmentTransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvestmentTransactionRepository investmentTransactionRepository;

    private Asset appleAsset;
    private Asset microsoftAsset;
    private InvestmentTransaction appleBuy1;
    private InvestmentTransaction appleBuy2;
    private InvestmentTransaction appleSell1;
    private InvestmentTransaction microsoftBuy1;
    private InvestmentTransaction microsoftSell1;

    @BeforeEach
    void setUp() {
        // Create test assets
        appleAsset = Asset.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        microsoftAsset = Asset.builder()
                .ticker("MSFT")
                .name("Microsoft Corporation")
                .isin("US5949181045")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.VALUE)
                .build();

        entityManager.persist(appleAsset);
        entityManager.persist(microsoftAsset);
        entityManager.flush();

        // Create test investment transactions
        appleBuy1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .asset(appleAsset)
                .units(new BigDecimal("10.0"))
                .pricePerUnit(new BigDecimal("150.00"))
                .fees(new BigDecimal("1.50"))
                .currency(Currency.USD)
                .exchangeRate(new BigDecimal("0.85"))
                .amount(new BigDecimal("1276.50")) // (10 * 150 * 0.85) + 1.50
                .name("Apple Inc. AAPL Buy")
                .description("Initial purchase")
                .build();

        appleBuy2 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .asset(appleAsset)
                .units(new BigDecimal("5.0"))
                .pricePerUnit(new BigDecimal("155.00"))
                .fees(new BigDecimal("1.00"))
                .currency(Currency.EUR)
                .exchangeRate(null)
                .amount(new BigDecimal("774.00")) // 5 * 155 - 1
                .name("Apple Inc. AAPL Buy 2")
                .description("Additional purchase")
                .build();

        appleSell1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .asset(appleAsset)
                .units(new BigDecimal("3.0"))
                .pricePerUnit(new BigDecimal("160.00"))
                .fees(new BigDecimal("2.00"))
                .currency(Currency.EUR)
                .exchangeRate(null)
                .amount(new BigDecimal("478.00")) // 3 * 160 - 2
                .name("Apple Inc. AAPL Sell")
                .description("Partial sale")
                .realizedGainLoss(new BigDecimal("25.00"))
                .build();

        microsoftBuy1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .asset(microsoftAsset)
                .units(new BigDecimal("8.0"))
                .pricePerUnit(new BigDecimal("250.00"))
                .fees(new BigDecimal("2.00"))
                .currency(Currency.USD)
                .exchangeRate(new BigDecimal("0.85"))
                .amount(new BigDecimal("1702.00")) // (8 * 250 * 0.85) + 2
                .name("Microsoft Corporation MSFT Buy")
                .description("Initial purchase")
                .build();

        microsoftSell1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .asset(microsoftAsset)
                .units(new BigDecimal("2.0"))
                .pricePerUnit(new BigDecimal("260.00"))
                .fees(new BigDecimal("1.50"))
                .currency(Currency.EUR)
                .exchangeRate(null)
                .amount(new BigDecimal("518.50")) // 2 * 260 - 1.50
                .name("Microsoft Corporation MSFT Sell")
                .description("Partial sale")
                .realizedGainLoss(new BigDecimal("15.00"))
                .build();

        // Save test data
        entityManager.persist(appleBuy1);
        entityManager.persist(appleBuy2);
        entityManager.persist(appleSell1);
        entityManager.persist(microsoftBuy1);
        entityManager.persist(microsoftSell1);
        entityManager.flush();
    }

    @Test
    void findByAsset_ShouldReturnTransactionsForGivenAsset() {
        // When
        List<InvestmentTransaction> appleTransactions = investmentTransactionRepository.findByAsset(appleAsset);

        // Then
        assertThat(appleTransactions).hasSize(3);
        assertThat(appleTransactions).extracting(InvestmentTransaction::getAsset)
                .allMatch(asset -> asset.equals(appleAsset));
    }

    @Test
    void findByAssetAndTransactionType_ShouldReturnBuyTransactionsForAsset() {
        // When
        List<InvestmentTransaction> appleBuyTransactions = investmentTransactionRepository
                .findByAssetAndTransactionTypeOrderByCreatedAtAsc(appleAsset, InvestmentTransactionType.BUY);

        // Then
        assertThat(appleBuyTransactions).hasSize(2);
        assertThat(appleBuyTransactions).extracting(InvestmentTransaction::getTransactionType)
                .allMatch(type -> type.equals(InvestmentTransactionType.BUY));
        assertThat(appleBuyTransactions).extracting(InvestmentTransaction::getAsset)
                .allMatch(asset -> asset.equals(appleAsset));
    }

    @Test
    void findByAssetAndTransactionType_ShouldReturnSellTransactionsForAsset() {
        // When
        List<InvestmentTransaction> appleSellTransactions = investmentTransactionRepository
                .findByAssetAndTransactionTypeOrderByCreatedAtDesc(appleAsset, InvestmentTransactionType.SELL);

        // Then
        assertThat(appleSellTransactions).hasSize(1);
        assertThat(appleSellTransactions).extracting(InvestmentTransaction::getTransactionType)
                .allMatch(type -> type.equals(InvestmentTransactionType.SELL));
        assertThat(appleSellTransactions.get(0).getRealizedGainLoss()).isEqualTo(new BigDecimal("25.00"));
    }

    @Test
    void findByCurrency_ShouldReturnTransactionsForGivenCurrency() {
        // When
        List<InvestmentTransaction> usdTransactions = investmentTransactionRepository.findByCurrency(Currency.USD);

        // Then
        assertThat(usdTransactions).hasSize(2);
        assertThat(usdTransactions).extracting(InvestmentTransaction::getCurrency)
                .allMatch(currency -> currency.equals(Currency.USD));
    }

    @Test
    void findByAssetType_ShouldReturnTransactionsForGivenAssetType() {
        // When
        List<InvestmentTransaction> stockTransactions = investmentTransactionRepository
                .findByAssetType(AssetType.STOCK);

        // Then
        assertThat(stockTransactions).hasSize(5); // All test transactions are for stocks
        assertThat(stockTransactions).extracting(t -> t.getAsset().getAssetType())
                .allMatch(assetType -> assetType.equals(AssetType.STOCK));
    }

    @Test
    void findByInvestmentStyle_ShouldReturnTransactionsForGivenInvestmentStyle() {
        // When
        List<InvestmentTransaction> growthTransactions = investmentTransactionRepository
                .findByInvestmentStyle(InvestmentStyle.GROWTH);

        // Then
        assertThat(growthTransactions).hasSize(3); // Apple transactions (GROWTH style)
        assertThat(growthTransactions).extracting(t -> t.getAsset().getInvestmentStyle())
                .allMatch(style -> style.equals(InvestmentStyle.GROWTH));
    }

    @Test
    void getTotalUnitsForAsset_ShouldCalculateCorrectPosition() {
        // When
        BigDecimal appleUnits = investmentTransactionRepository.getTotalUnitsForAsset(appleAsset);
        BigDecimal microsoftUnits = investmentTransactionRepository.getTotalUnitsForAsset(microsoftAsset);

        // Then
        assertThat(appleUnits).isEqualByComparingTo(new BigDecimal("12.0")); // 10 + 5 - 3
        assertThat(microsoftUnits).isEqualByComparingTo(new BigDecimal("6.0")); // 8 - 2
    }

    @Test
    void getTotalCostBasisForAsset_ShouldCalculateCorrectCostBasis() {
        // When
        BigDecimal appleCostBasis = investmentTransactionRepository.getTotalCostBasisForAsset(appleAsset);
        BigDecimal microsoftCostBasis = investmentTransactionRepository.getTotalCostBasisForAsset(microsoftAsset);

        // Then
        assertThat(appleCostBasis).isEqualTo(new BigDecimal("2050.50")); // 1276.50 + 774
        assertThat(microsoftCostBasis).isEqualTo(new BigDecimal("1702.00")); // Only one buy (with fees added after conversion)
    }

    @Test
    void getTotalRealizedGainsForAsset_ShouldCalculateCorrectGains() {
        // When
        BigDecimal appleGains = investmentTransactionRepository.getTotalRealizedGainsForAsset(appleAsset);
        BigDecimal microsoftGains = investmentTransactionRepository.getTotalRealizedGainsForAsset(microsoftAsset);

        // Then
        assertThat(appleGains).isEqualTo(new BigDecimal("25.00"));
        assertThat(microsoftGains).isEqualTo(new BigDecimal("15.00"));
    }

    @Test
    void getPortfolioValueByAssetType_ShouldReturnCorrectGrouping() {
        // When
        List<Object[]> portfolioByType = investmentTransactionRepository.getPortfolioValueByAssetType();

        // Then
        assertThat(portfolioByType).hasSize(1); // Only STOCK type in test data
        Object[] stockData = portfolioByType.get(0);
        assertThat(stockData[0]).isEqualTo(AssetType.STOCK);
        assertThat(stockData[1]).isEqualTo(new BigDecimal("3752.50")); // 2050.50 + 1702
    }

    @Test
    void getPortfolioValueByInvestmentStyle_ShouldReturnCorrectGrouping() {
        // When
        List<Object[]> portfolioByStyle = investmentTransactionRepository.getPortfolioValueByInvestmentStyle();

        // Then
        assertThat(portfolioByStyle).hasSize(2); // GROWTH and VALUE styles in test data
        // Find GROWTH and VALUE entries
        Object[] growthData = portfolioByStyle.stream()
                .filter(data -> data[0].equals(InvestmentStyle.GROWTH))
                .findFirst()
                .orElse(null);
        Object[] valueData = portfolioByStyle.stream()
                .filter(data -> data[0].equals(InvestmentStyle.VALUE))
                .findFirst()
                .orElse(null);

        assertThat(growthData).isNotNull();
        assertThat(growthData[1]).isEqualTo(new BigDecimal("2050.50")); // Apple cost basis (with fees added after conversion)
        assertThat(valueData).isNotNull();
        assertThat(valueData[1]).isEqualTo(new BigDecimal("1702.00")); // Microsoft cost basis (with fees added after conversion)
    }

    @Test
    void findAssetsWithPositions_ShouldReturnAssetsWithNonZeroPositions() {
        // When
        List<Asset> assetsWithPositions = investmentTransactionRepository.findAssetsWithPositions();

        // Then
        assertThat(assetsWithPositions).hasSize(2); // Both Apple and Microsoft have positions
        assertThat(assetsWithPositions).extracting(Asset::getTicker)
                .containsExactlyInAnyOrder("AAPL", "MSFT");
    }

    @Test
    void getTotalPortfolioValue_ShouldCalculateCorrectTotalValue() {
        // When
        BigDecimal totalValue = investmentTransactionRepository.getTotalPortfolioValue();

        // Then
        // Apple: 1276.50 + 774 - 478 = 1572.50
        // Microsoft: 1702 - 518.50 = 1183.50
        // Total: 1572.50 + 1183.50 = 2756.00
        assertThat(totalValue).isEqualTo(new BigDecimal("2756.00"));
    }

    @Test
    void countByTransactionType_ShouldReturnCorrectCounts() {
        // When & Then
        assertThat(investmentTransactionRepository.countByTransactionType(InvestmentTransactionType.BUY))
                .isEqualTo(3);
        assertThat(investmentTransactionRepository.countByTransactionType(InvestmentTransactionType.SELL))
                .isEqualTo(2);
        assertThat(investmentTransactionRepository.countByTransactionType(InvestmentTransactionType.DIVIDEND))
                .isEqualTo(0); // No dividends in test data
    }

    @Test
    void findAllByOrderByCreatedAtDesc_ShouldReturnTransactionsOrderedByDate() {
        // When
        List<InvestmentTransaction> transactions = investmentTransactionRepository.findAllByOrderByCreatedAtDesc();

        // Then
        assertThat(transactions).hasSize(5);
        // The transactions should be ordered by creation date (newest first)
        // Note: Due to test setup complexity, we just verify the correct number and that ordering works
        assertThat(transactions).isSortedAccordingTo((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
    }

    @Test
    void findByAsset_WithPagination_ShouldReturnPagedResults() {
        // When
        Page<InvestmentTransaction> appleTransactions = investmentTransactionRepository
                .findByAsset(appleAsset, PageRequest.of(0, 2));

        // Then
        assertThat(appleTransactions.getContent()).hasSize(2);
        assertThat(appleTransactions.getTotalElements()).isEqualTo(3);
        assertThat(appleTransactions.getTotalPages()).isEqualTo(2);
    }


}

package com.radomskyi.budgeter.domain.entity.investment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvestmentTest {

    private Asset asset;
    private Investment investment;

    @BeforeEach
    void setUp() {
        asset = Asset.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        investment = Investment.builder()
                .asset(asset)
                .totalCost(BigDecimal.ZERO)
                .totalUnits(BigDecimal.ZERO)
                .costBasis(BigDecimal.ZERO)
                .currency(Currency.USD)
                .brokerage("TestBroker")
                .build();
    }

    @Test
    void testInvestmentCreation() {
        assertThat(investment.getAsset()).isEqualTo(asset);
        assertThat(investment.getTotalCost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(investment.getCostBasis()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(investment.getCurrency()).isEqualTo(Currency.USD);
        assertThat(investment.getTransactions()).isEmpty();
        assertThat(investment.getLatestPrice()).isNull();
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testUpdateCostBasis() {
        investment.setTotalCost(new BigDecimal("1500.00"));
        investment.setTotalUnits(new BigDecimal("10"));

        investment.updateCostBasis();

        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("150.00000000"));
    }

    @Test
    void testUpdateCostBasisWithZeroUnits() {
        investment.setTotalCost(new BigDecimal("1500.00"));
        investment.setTotalUnits(BigDecimal.ZERO);

        investment.updateCostBasis();

        assertThat(investment.getCostBasis()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testUpdateCostBasisWithNullUnits() {
        investment.setTotalCost(new BigDecimal("1500.00"));
        investment.setTotalUnits(null);

        investment.updateCostBasis();

        assertThat(investment.getCostBasis()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAddBuyTransaction() {
        InvestmentTransaction buyTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .currency(Currency.USD)
                .build();

        investment.addTransaction(buyTransaction);

        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("150.00000000"));
        assertThat(investment.getLatestPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(investment.getTransactions()).hasSize(1);
        assertThat(buyTransaction.getInvestment()).isEqualTo(investment);
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAddBuyTransactionWithFees() {
        InvestmentTransaction buyTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .fees(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();

        investment.addTransaction(buyTransaction);

        // Total cost includes fees: (10 * 150) + 5 = 1505
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("1505.00"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("150.50000000"));
        assertThat(investment.getLatestPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAddMultipleBuyTransactions() {
        InvestmentTransaction buy1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .fees(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();

        InvestmentTransaction buy2 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("160.00"))
                .fees(new BigDecimal("2.50"))
                .currency(Currency.USD)
                .build();

        investment.addTransaction(buy1);
        investment.addTransaction(buy2);

        // Total units: 10 + 5 = 15
        // Total cost: (10 * 150 + 5) + (5 * 160 + 2.50) = 1505 + 802.50 = 2307.50
        // Cost basis: 2307.50 / 15 = 153.83333333
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("15"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("2307.50"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("153.83333333"));
        assertThat(investment.getLatestPrice()).isEqualByComparingTo(new BigDecimal("160.00"));
        assertThat(investment.getTransactions()).hasSize(2);
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAddSellTransactionWithGain() {
        // First buy some shares
        InvestmentTransaction buyTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .fees(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buyTransaction);

        // Then sell some at a higher price
        InvestmentTransaction sellTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("170.00"))
                .fees(new BigDecimal("3.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sellTransaction);

        // After buy: totalCost = 1505, totalUnits = 10, costBasis = 150.50
        // Sell proceeds: 5 * 170 = 850
        // Cost of sold units: 5 * 150.50 = 752.50
        // Realized gain: 850 - 752.50 - 3 = 94.50
        // Remaining units: 10 - 5 = 5
        // Remaining cost: 1505 - 752.50 = 752.50
        // Cost basis stays: 752.50 / 5 = 150.50
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("752.50"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("150.50000000"));
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("94.50"));
        assertThat(investment.getLatestPrice()).isEqualByComparingTo(new BigDecimal("170.00"));
        assertThat(investment.getTransactions()).hasSize(2);
    }

    @Test
    void testAddSellTransactionWithLoss() {
        // First buy some shares
        InvestmentTransaction buyTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .fees(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buyTransaction);

        // Then sell some at a lower price
        InvestmentTransaction sellTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("130.00"))
                .fees(new BigDecimal("2.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sellTransaction);

        // After buy: totalCost = 1505, totalUnits = 10, costBasis = 150.50
        // Sell proceeds: 5 * 130 = 650
        // Cost of sold units: 5 * 150.50 = 752.50
        // Realized loss: 650 - 752.50 - 2 = -104.50
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("752.50"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("150.50000000"));
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("-104.50"));
        assertThat(investment.getLatestPrice()).isEqualByComparingTo(new BigDecimal("130.00"));
    }

    @Test
    void testAddSellTransactionWithoutFees() {
        // First buy
        InvestmentTransaction buyTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buyTransaction);

        // Then sell without fees
        InvestmentTransaction sellTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("120.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sellTransaction);

        // After buy: totalCost = 1000, totalUnits = 10, costBasis = 100.00
        // Sell proceeds: 5 * 120 = 600
        // Cost of sold units: 5 * 100 = 500
        // Realized gain: 600 - 500 - 0 = 100
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("100.00000000"));
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void testMultipleBuysAndSells() {
        // Buy #1: 10 shares at $150 with $5 fee
        InvestmentTransaction buy1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .fees(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buy1);
        // State: units=10, cost=1505, basis=150.50, realized=0

        // Buy #2: 5 shares at $160 with $2.50 fee
        InvestmentTransaction buy2 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("160.00"))
                .fees(new BigDecimal("2.50"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buy2);
        // State: units=15, cost=2307.50, basis=153.83333333, realized=0

        // Sell #1: 5 shares at $170 with $3 fee
        InvestmentTransaction sell1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("170.00"))
                .fees(new BigDecimal("3.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sell1);
        // Sell proceeds: 5 * 170 = 850
        // Cost of sold: 5 * 153.83333333 = 769.16666665
        // Gain: 850 - 769.16666665 - 3 = 77.83333335
        // State: units=10, cost=1538.33333335, basis=153.83333333, realized=77.83333335

        // Sell #2: 3 shares at $180 with $2 fee
        InvestmentTransaction sell2 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("3"))
                .pricePerUnit(new BigDecimal("180.00"))
                .fees(new BigDecimal("2.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sell2);
        // Sell proceeds: 3 * 180 = 540
        // Cost of sold: 3 * 153.83333333 = 461.49999999
        // Gain: 540 - 461.49999999 - 2 = 76.50000001
        // State: units=7, cost=1076.83333336, basis=153.83333333, realized=154.33333336

        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("7"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("1076.83333333"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("153.83333333"));
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("154.33333333"));
        assertThat(investment.getLatestPrice()).isEqualByComparingTo(new BigDecimal("180.00"));
        assertThat(investment.getTransactions()).hasSize(4);
    }

    @Test
    void testRemoveTransaction() {
        InvestmentTransaction buyTransaction = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .currency(Currency.USD)
                .build();

        investment.addTransaction(buyTransaction);
        assertThat(investment.getTransactions()).hasSize(1);

        investment.removeTransaction(buyTransaction);
        assertThat(investment.getTransactions()).isEmpty();
        assertThat(buyTransaction.getInvestment()).isNull();
    }

    @Test
    void testFractionalShares() {
        // Buy fractional shares
        InvestmentTransaction buyFractional = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("0.5"))
                .pricePerUnit(new BigDecimal("200.00"))
                .fees(new BigDecimal("1.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buyFractional);

        // Cost: 0.5 * 200 + 1 = 101
        // Cost basis: 101 / 0.5 = 202
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("0.5"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("101.00"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("202.00000000"));

        // Sell fractional shares
        InvestmentTransaction sellFractional = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("0.25"))
                .pricePerUnit(new BigDecimal("220.00"))
                .fees(new BigDecimal("0.50"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sellFractional);

        // Sell proceeds: 0.25 * 220 = 55
        // Cost of sold: 0.25 * 202 = 50.50
        // Gain: 55 - 50.50 - 0.50 = 4.00
        // Remaining units: 0.5 - 0.25 = 0.25
        // Remaining cost: 101 - 50.50 = 50.50
        assertThat(investment.getTotalUnits()).isEqualByComparingTo(new BigDecimal("0.25"));
        assertThat(investment.getTotalCost()).isEqualByComparingTo(new BigDecimal("50.50"));
        assertThat(investment.getCostBasis()).isEqualByComparingTo(new BigDecimal("202.00000000"));
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    void testEqualsAndHashCode() {
        Investment investment1 = Investment.builder()
                .asset(asset)
                .totalCost(new BigDecimal("1500.00"))
                .totalUnits(new BigDecimal("10"))
                .costBasis(new BigDecimal("150.00"))
                .currency(Currency.USD)
                .brokerage("TestBroker")
                .build();

        Investment investment2 = Investment.builder()
                .asset(asset)
                .totalCost(new BigDecimal("1500.00"))
                .totalUnits(new BigDecimal("10"))
                .costBasis(new BigDecimal("150.00"))
                .currency(Currency.USD)
                .brokerage("TestBroker")
                .build();

        Asset differentAsset = Asset.builder()
                .ticker("MSFT")
                .name("Microsoft Corporation")
                .isin("US5949181045")
                .assetType(AssetType.STOCK)
                .investmentStyle(InvestmentStyle.VALUE)
                .build();

        Investment investment3 = Investment.builder()
                .asset(differentAsset)
                .totalCost(new BigDecimal("2000.00"))
                .totalUnits(new BigDecimal("10"))
                .costBasis(new BigDecimal("200.00"))
                .currency(Currency.USD)
                .brokerage("TestBroker")
                .build();

        assertThat(investment1).isEqualTo(investment2);
        assertThat(investment1).isNotEqualTo(investment3);
        assertThat(investment1.hashCode()).isEqualTo(investment2.hashCode());
    }

    @Test
    void testAccumulativeRealizedGainLoss() {
        // Buy 10 shares
        InvestmentTransaction buy = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.BUY)
                .investment(investment)
                .units(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(buy);

        // First sell with gain
        InvestmentTransaction sell1 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("3"))
                .pricePerUnit(new BigDecimal("120.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sell1);
        // Gain: (3 * 120) - (3 * 100) = 60
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("60.00"));

        // Second sell with loss
        InvestmentTransaction sell2 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("2"))
                .pricePerUnit(new BigDecimal("90.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sell2);
        // Loss: (2 * 90) - (2 * 100) = -20
        // Total realized: 60 + (-20) = 40
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("40.00"));

        // Third sell with gain
        InvestmentTransaction sell3 = InvestmentTransaction.builder()
                .transactionType(InvestmentTransactionType.SELL)
                .investment(investment)
                .units(new BigDecimal("5"))
                .pricePerUnit(new BigDecimal("110.00"))
                .fees(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();
        investment.addTransaction(sell3);
        // Gain: (5 * 110) - (5 * 100) - 5 = 45
        // Total realized: 40 + 45 = 85
        assertThat(investment.getRealizedGainLoss()).isEqualByComparingTo(new BigDecimal("85.00"));
    }
}

package com.radomskyi.budgeter.domain.entity.investment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import com.radomskyi.budgeter.domain.entity.budgeting.Transaction;

import java.math.BigDecimal;

@Entity
@Table(name = "investment_transaction")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentTransaction extends Transaction {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 10)
    private InvestmentTransactionType transactionType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotNull
    @Positive
    @Column(name = "units", nullable = false, precision = 15, scale = 8)
    private BigDecimal units;

    @NotNull
    @Positive
    @Column(name = "price_per_unit", nullable = false, precision = 15, scale = 8)
    private BigDecimal pricePerUnit;

    @DecimalMin(value = "0.00")
    @Column(name = "fees", precision = 10, scale = 2)
    private BigDecimal fees;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @DecimalMin(value = "0.000001")
    @Column(name = "exchange_rate", precision = 15, scale = 8)
    private BigDecimal exchangeRate;

    // Calculated field for EUR amount (amount already represents EUR value)
    // For portfolio tracking and German tax calculations
    @Column(name = "realized_gain_loss", precision = 15, scale = 2)
    private BigDecimal realizedGainLoss;

    // Constructor for required fields
    public InvestmentTransaction(InvestmentTransactionType transactionType, Asset asset,
                               BigDecimal units, BigDecimal pricePerUnit, Currency currency) {
        this.transactionType = transactionType;
        this.asset = asset;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.currency = currency;
        calculateAmount();
    }

    // Constructor with fees and exchange rate
    public InvestmentTransaction(InvestmentTransactionType transactionType, Asset asset,
                               BigDecimal units, BigDecimal pricePerUnit, BigDecimal fees,
                               Currency currency, BigDecimal exchangeRate) {
        this.transactionType = transactionType;
        this.asset = asset;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.fees = fees;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        calculateAmount();
    }

    // Calculate total amount based on units, price per unit, fees, and exchange rate
    public void calculateAmount() {
        BigDecimal grossAmount = units.multiply(pricePerUnit);

        // Convert to EUR if currency is not EUR
        if (currency != Currency.EUR && exchangeRate != null) {
            grossAmount = grossAmount.multiply(exchangeRate);
        }

        // Add fees (fees are always in EUR)
        if (fees != null) {
            super.amount = grossAmount.add(fees);
        } else {
            super.amount = grossAmount;
        }
    }

    // Custom constructor for Trading212 CSV import
    // TODO: Consider moving to a separate CsvImportService class for better separation of concerns
    public static InvestmentTransaction fromTrading212CsvData(String action, String ticker, String name, String isin,
                                                             BigDecimal units, BigDecimal pricePerUnit, String currencyStr,
                                                             BigDecimal exchangeRate, BigDecimal fees, BigDecimal grossTotal) {
        InvestmentTransactionType type;
        if ("Market buy".equals(action) || "buy".equalsIgnoreCase(action)) {
            type = InvestmentTransactionType.BUY;
        } else if ("Market sell".equals(action) || "sell".equalsIgnoreCase(action)) {
            type = InvestmentTransactionType.SELL;
        } else if ("dividend".equalsIgnoreCase(action) || action.toLowerCase().contains("dividend")) {
            type = InvestmentTransactionType.DIVIDEND;
        } else {
            type = InvestmentTransactionType.BUY; // Default fallback
        }

        Currency currency = "EUR".equals(currencyStr) ? Currency.EUR : Currency.USD;

        // Create or find asset (for now, we'll create a minimal asset)
        Asset asset = Asset.builder()
            .ticker(ticker)
            .name(name)
            .isin(isin)
            .assetType(AssetType.STOCK) // Default, should be determined from ISIN or other data
            .investmentStyle(InvestmentStyle.GROWTH) // Default
            .build();

        return InvestmentTransaction.builder()
            .transactionType(type)
            .asset(asset)
            .units(units)
            .pricePerUnit(pricePerUnit)
            .fees(fees)
            .currency(currency)
            .exchangeRate(exchangeRate)
            .amount(grossTotal) // Use provided gross total
            .name(name + " " + ticker)
            .description("Imported from CSV: " + action + (type == InvestmentTransactionType.DIVIDEND ? " (Dividend)" : ""))
            .build();
    }
}

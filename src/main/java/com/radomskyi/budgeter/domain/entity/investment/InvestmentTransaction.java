package com.radomskyi.budgeter.domain.entity.investment;

import com.radomskyi.budgeter.domain.entity.budgeting.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    @JoinColumn(name = "investment_id", nullable = false)
    private Investment investment;

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
    public InvestmentTransaction(
            InvestmentTransactionType transactionType,
            Investment investment,
            BigDecimal units,
            BigDecimal pricePerUnit,
            Currency currency) {
        this.transactionType = transactionType;
        this.investment = investment;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.currency = currency;
        calculateAmount();
    }

    // Constructor with fees and exchange rate
    public InvestmentTransaction(
            InvestmentTransactionType transactionType,
            Investment investment,
            BigDecimal units,
            BigDecimal pricePerUnit,
            BigDecimal fees,
            Currency currency,
            BigDecimal exchangeRate) {
        this.transactionType = transactionType;
        this.investment = investment;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.fees = fees;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        calculateAmount();
    }

    // Calculate total amount based on units, price per unit, fees, and exchange rate
    @PrePersist
    @PreUpdate
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

    /**
     * Helper method to get the asset from the parent investment.
     * This is a convenience method to access the asset through the investment relationship.
     *
     * @return The asset associated with this transaction's investment
     */
    public Asset getAsset() {
        return investment != null ? investment.getAsset() : null;
    }
}

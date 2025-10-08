package com.radomskyi.budgeter.domain.entity.investment;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "investment")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "total_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCost;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "total_units", nullable = false, precision = 15, scale = 8)
    private BigDecimal totalUnits;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "cost_basis", nullable = false, precision = 15, scale = 8)
    private BigDecimal costBasis;

    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvestmentTransaction> transactions = new ArrayList<>();

    @DecimalMin(value = "0.00000001")
    @Column(name = "latest_price", precision = 15, scale = 8)
    private BigDecimal latestPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    /**
     * Calculates total realized gain/loss by summing up all SELL transaction gains/losses.
     * This is a computed field that aggregates data from the transactions list.
     *
     * @return The total realized gain/loss across all SELL transactions
     */
    public BigDecimal getRealizedGainLoss() {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return transactions.stream()
                .filter(t -> t.getRealizedGainLoss() != null)
                .map(InvestmentTransaction::getRealizedGainLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Static factory method to create a new Investment with initial values.
     *
     * @param asset The asset for this investment
     * @param currency The currency for this investment
     * @return A new Investment instance
     */
    public static Investment createNew(Asset asset, Currency currency) {
        return Investment.builder()
                .asset(asset)
                .totalCost(BigDecimal.ZERO)
                .totalUnits(BigDecimal.ZERO)
                .costBasis(BigDecimal.ZERO)
                .currency(currency)
                .build();
    }

    /**
     * Updates the cost basis based on current total cost and total units.
     * Cost basis = Total Cost / Total Units
     */
    public void updateCostBasis() {
        if (totalUnits != null && totalUnits.compareTo(BigDecimal.ZERO) > 0) {
            this.costBasis = totalCost.divide(totalUnits, 8, java.math.RoundingMode.HALF_UP);
        } else {
            this.costBasis = BigDecimal.ZERO;
        }
    }

    /**
     * Adds a transaction to this investment and updates the investment metrics.
     * Fees are included in the total cost calculation to accurately reflect the cost basis.
     * For SELL transactions, calculates and sets the realized gain/loss.
     *
     * @param transaction The transaction to add
     */
    public void addTransaction(InvestmentTransaction transaction) {
        transactions.add(transaction);
        transaction.setInvestment(this);

        BigDecimal fees = transaction.getFees() != null ? transaction.getFees() : BigDecimal.ZERO;

        // Update total units and total cost based on transaction type
        if (transaction.getTransactionType() == InvestmentTransactionType.BUY) {
            totalUnits = totalUnits.add(transaction.getUnits());
            // Total cost includes purchase price plus fees
            BigDecimal purchaseCost = transaction.getUnits().multiply(transaction.getPricePerUnit());
            totalCost = totalCost.add(purchaseCost).add(fees);
        } else if (transaction.getTransactionType() == InvestmentTransactionType.SELL) {
            // Calculate realized gain/loss before updating totals
            BigDecimal saleProceeds = transaction.getUnits().multiply(transaction.getPricePerUnit());
            BigDecimal costOfSoldUnits = transaction.getUnits().multiply(costBasis);
            BigDecimal transactionGainLoss =
                    saleProceeds.subtract(costOfSoldUnits).subtract(fees);

            // Set the realized gain/loss on the transaction
            transaction.setRealizedGainLoss(transactionGainLoss);

            // Update totals
            totalUnits = totalUnits.subtract(transaction.getUnits());
            totalCost = totalCost.subtract(costOfSoldUnits);
        }

        // Update cost basis
        updateCostBasis();

        // Update latest price from the transaction
        this.latestPrice = transaction.getPricePerUnit();
    }

    /**
     * Removes a transaction from this investment.
     *
     * @param transaction The transaction to remove
     */
    public void removeTransaction(InvestmentTransaction transaction) {
        transactions.remove(transaction);
        transaction.setInvestment(null);
    }
}

package com.radomskyi.budgeter.dto;

import com.radomskyi.budgeter.domain.entity.investment.Currency;
import com.radomskyi.budgeter.domain.entity.investment.InvestmentTransaction;
import com.radomskyi.budgeter.domain.entity.investment.InvestmentTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for investment transaction data")
public class InvestmentTransactionResponse {

    @Schema(description = "Unique identifier of the investment transaction", example = "1")
    private Long id;

    @Schema(description = "Type of investment transaction", example = "BUY")
    private InvestmentTransactionType transactionType;

    @Schema(description = "Asset ticker symbol", example = "AAPL")
    private String assetTicker;

    @Schema(description = "Asset name", example = "Apple Inc.")
    private String assetName;

    @Schema(description = "International Securities Identification Number", example = "US0378331005")
    private String assetIsin;

    @Schema(description = "Number of units/shares", example = "10.5")
    private BigDecimal units;

    @Schema(description = "Price per unit/share", example = "150.25")
    private BigDecimal pricePerUnit;

    @Schema(description = "Transaction fees", example = "2.50")
    private BigDecimal fees;

    @Schema(description = "Transaction currency", example = "EUR")
    private Currency currency;

    @Schema(description = "Exchange rate for currency conversion", example = "1.15")
    private BigDecimal exchangeRate;

    @Schema(description = "Realized gain/loss amount", example = "25.50")
    private BigDecimal realizedGainLoss;

    @Schema(description = "Transaction amount in EUR", example = "1577.63")
    private BigDecimal amount;

    @Schema(description = "Transaction name/description", example = "Apple Inc. Purchase")
    private String name;

    @Schema(description = "Detailed transaction description", example = "Bought Apple shares via broker")
    private String description;

    @Schema(description = "Date and time when the transaction was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time when the transaction was last updated")
    private LocalDateTime updatedAt;

    /** Creates an InvestmentTransactionResponse from an InvestmentTransaction entity */
    public static InvestmentTransactionResponse fromTransaction(InvestmentTransaction transaction) {
        return InvestmentTransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .assetTicker(transaction.getAsset().getTicker())
                .assetName(transaction.getAsset().getName())
                .assetIsin(transaction.getAsset().getIsin())
                .units(transaction.getUnits())
                .pricePerUnit(transaction.getPricePerUnit())
                .fees(transaction.getFees())
                .currency(transaction.getCurrency())
                .exchangeRate(transaction.getExchangeRate())
                .realizedGainLoss(transaction.getRealizedGainLoss())
                .amount(transaction.getAmount())
                .name(transaction.getName())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}

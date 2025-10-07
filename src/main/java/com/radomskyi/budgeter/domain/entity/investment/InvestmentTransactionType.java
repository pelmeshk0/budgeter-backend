package com.radomskyi.budgeter.domain.entity.investment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of investment transactions")
public enum InvestmentTransactionType {

    @Schema(description = "Purchase of investment assets")
    BUY,

    @Schema(description = "Sale of investment assets")
    SELL,

    @Schema(description = "Dividend payment received")
    DIVIDEND
}

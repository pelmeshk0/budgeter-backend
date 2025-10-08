package com.radomskyi.budgeter.domain.entity.investment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Investment styles for portfolio analysis")
public enum InvestmentStyle {
    @Schema(description = "Growth-oriented investments focusing on capital appreciation")
    GROWTH,

    @Schema(description = "Value-oriented investments focusing on undervalued assets")
    VALUE,

    @Schema(description = "High-risk, speculative investments")
    SPECULATION,

    @Schema(description = "Fixed income investments for stable returns")
    FIXED_INCOME
}

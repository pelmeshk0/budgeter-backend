package com.radomskyi.budgeter.domain.entity.budgeting;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categories for income transactions")
public enum IncomeCategory {

    @Schema(description = "Regular salary or wages from employment")
    SALARY,

    @Schema(description = "Income from freelance work, consulting, or side gigs")
    FREELANCE,

    @Schema(description = "Income from investments, dividends, or interest")
    INVESTMENTS,

    @Schema(description = "Business income from owning or operating a business")
    BUSINESS,

    @Schema(description = "Gifts, bonuses, or one-time payments")
    GIFTS_AND_BONUSES,

    @Schema(description = "Income from rental properties or other assets")
    RENTAL,

    @Schema(description = "Government benefits, pensions, or social security")
    GOVERNMENT_BENEFITS,

    @Schema(description = "Other sources of income not covered by other categories")
    OTHER_INCOME
}

package com.radomskyi.budgeter.domain.entity.investment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supported currencies for investment transactions")
public enum Currency {

    @Schema(description = "US Dollar")
    USD,

    @Schema(description = "Euro")
    EUR
}

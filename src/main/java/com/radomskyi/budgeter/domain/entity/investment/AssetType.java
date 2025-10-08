package com.radomskyi.budgeter.domain.entity.investment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categories for investment asset types")
public enum AssetType {
    @Schema(description = "Index funds and Exchange Traded Funds")
    INDEX_ETF,

    @Schema(description = "Individual stocks")
    STOCK,

    @Schema(description = "Fixed income securities")
    BOND,

    @Schema(description = "Physical commodities like gold, oil, etc.")
    COMMODITY,

    @Schema(description = "Cryptocurrencies and digital assets")
    CRYPTO,

    @Schema(description = "Derivative instruments like options, futures")
    DERIVATIVE
}

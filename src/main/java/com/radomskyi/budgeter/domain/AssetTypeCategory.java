package com.radomskyi.budgeter.domain;

import io.swagger.v3.oas.annotations.media.Schema;
// fixme rename this to AssetType
@Schema(description = "Categories for investment asset types")
public enum AssetTypeCategory {

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

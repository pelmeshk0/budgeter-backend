package com.radomskyi.budgeter.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

class AssetTest {

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = Asset.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetTypeCategory(AssetTypeCategory.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();
    }

    @Test
    void testAssetCreation() {
        assertThat(asset.getTicker()).isEqualTo("AAPL");
        assertThat(asset.getName()).isEqualTo("Apple Inc.");
        assertThat(asset.getIsin()).isEqualTo("US0378331005");
        assertThat(asset.getAssetTypeCategory()).isEqualTo(AssetTypeCategory.STOCK);
        assertThat(asset.getInvestmentStyle()).isEqualTo(InvestmentStyle.GROWTH);
    }

    @Test
    void testAssetBuilder() {
        Asset newAsset = Asset.builder()
                .ticker("MSFT")
                .name("Microsoft Corporation")
                .isin("US5949181045")
                .assetTypeCategory(AssetTypeCategory.STOCK)
                .investmentStyle(InvestmentStyle.VALUE)
                .build();

        assertThat(newAsset.getTicker()).isEqualTo("MSFT");
        assertThat(newAsset.getName()).isEqualTo("Microsoft Corporation");
        assertThat(newAsset.getIsin()).isEqualTo("US5949181045");
        assertThat(newAsset.getAssetTypeCategory()).isEqualTo(AssetTypeCategory.STOCK);
        assertThat(newAsset.getInvestmentStyle()).isEqualTo(InvestmentStyle.VALUE);
    }

    @Test
    void testAssetEqualsAndHashCode() {
        Asset asset1 = Asset.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetTypeCategory(AssetTypeCategory.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        Asset asset2 = Asset.builder()
                .ticker("AAPL")
                .name("Apple Inc.")
                .isin("US0378331005")
                .assetTypeCategory(AssetTypeCategory.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        Asset asset3 = Asset.builder()
                .ticker("MSFT")
                .name("Microsoft Corporation")
                .isin("US5949181045")
                .assetTypeCategory(AssetTypeCategory.STOCK)
                .investmentStyle(InvestmentStyle.VALUE)
                .build();

        assertThat(asset1).isEqualTo(asset2);
        assertThat(asset1).isNotEqualTo(asset3);
        assertThat(asset1.hashCode()).isEqualTo(asset2.hashCode());
        assertThat(asset1.hashCode()).isNotEqualTo(asset3.hashCode());
    }

    @Test
    void testAssetToString() {
        String toString = asset.toString();
        assertThat(toString).contains("AAPL");
        assertThat(toString).contains("Apple Inc.");
        assertThat(toString).contains("US0378331005");
        assertThat(toString).contains("STOCK");
        assertThat(toString).contains("GROWTH");
    }

    @Test
    void testAssetWithNullIsin() {
        Asset assetWithoutIsin = Asset.builder()
                .ticker("TSLA")
                .name("Tesla Inc.")
                .assetTypeCategory(AssetTypeCategory.STOCK)
                .investmentStyle(InvestmentStyle.GROWTH)
                .build();

        assertThat(assetWithoutIsin.getTicker()).isEqualTo("TSLA");
        assertThat(assetWithoutIsin.getName()).isEqualTo("Tesla Inc.");
        assertThat(assetWithoutIsin.getIsin()).isNull();
        assertThat(assetWithoutIsin.getAssetTypeCategory()).isEqualTo(AssetTypeCategory.STOCK);
        assertThat(assetWithoutIsin.getInvestmentStyle()).isEqualTo(InvestmentStyle.GROWTH);
    }

    @Test
    void testAssetTypeCategoryEnum() {
        assertThat(AssetTypeCategory.values()).hasSize(6);
        assertThat(AssetTypeCategory.valueOf("STOCK")).isEqualTo(AssetTypeCategory.STOCK);
        assertThat(AssetTypeCategory.valueOf("INDEX_ETF")).isEqualTo(AssetTypeCategory.INDEX_ETF);
        assertThat(AssetTypeCategory.valueOf("BOND")).isEqualTo(AssetTypeCategory.BOND);
        assertThat(AssetTypeCategory.valueOf("COMMODITY")).isEqualTo(AssetTypeCategory.COMMODITY);
        assertThat(AssetTypeCategory.valueOf("CRYPTO")).isEqualTo(AssetTypeCategory.CRYPTO);
        assertThat(AssetTypeCategory.valueOf("DERIVATIVE")).isEqualTo(AssetTypeCategory.DERIVATIVE);
    }

    @Test
    void testInvestmentStyleEnum() {
        assertThat(InvestmentStyle.values()).hasSize(4);
        assertThat(InvestmentStyle.valueOf("GROWTH")).isEqualTo(InvestmentStyle.GROWTH);
        assertThat(InvestmentStyle.valueOf("VALUE")).isEqualTo(InvestmentStyle.VALUE);
        assertThat(InvestmentStyle.valueOf("SPECULATION")).isEqualTo(InvestmentStyle.SPECULATION);
        assertThat(InvestmentStyle.valueOf("FIXED_INCOME")).isEqualTo(InvestmentStyle.FIXED_INCOME);
    }
}

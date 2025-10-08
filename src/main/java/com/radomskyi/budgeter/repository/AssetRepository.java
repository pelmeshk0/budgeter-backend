package com.radomskyi.budgeter.repository;

import com.radomskyi.budgeter.domain.entity.investment.Asset;
import com.radomskyi.budgeter.domain.entity.investment.AssetType;
import com.radomskyi.budgeter.domain.entity.investment.InvestmentStyle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    // Find asset by ticker
    Optional<Asset> findByTicker(String ticker);

    // Find asset by ISIN
    Optional<Asset> findByIsin(String isin);

    // Find assets by name (case-insensitive, partial match)
    List<Asset> findByNameContainingIgnoreCase(String name);

    // Find assets by asset type
    List<Asset> findByAssetType(AssetType assetType);

    // Find assets by investment style
    List<Asset> findByInvestmentStyle(InvestmentStyle investmentStyle);

    // Find assets by ticker or name (for search functionality)
    List<Asset> findByTickerOrName(String ticker, String name);

    // Check if asset exists by ticker
    boolean existsByTicker(String ticker);

    // Check if asset exists by ISIN
    boolean existsByIsin(String isin);
}

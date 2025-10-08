package com.radomskyi.budgeter.repository;

import com.radomskyi.budgeter.domain.entity.investment.Asset;
import com.radomskyi.budgeter.domain.entity.investment.Currency;
import com.radomskyi.budgeter.domain.entity.investment.Investment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    // Find investment by asset
    Optional<Investment> findByAsset(Asset asset);

    // Find all investments with a specific currency
    List<Investment> findByCurrency(Currency currency);

    // Find all investments with a specific currency with pagination
    Page<Investment> findByCurrency(Currency currency, Pageable pageable);

    // Find investments by asset type
    @Query("SELECT i FROM Investment i WHERE i.asset.assetType = :assetType")
    List<Investment> findByAssetType(
            @Param("assetType") com.radomskyi.budgeter.domain.entity.investment.AssetType assetType);

    // Find investments by investment style
    @Query("SELECT i FROM Investment i WHERE i.asset.investmentStyle = :investmentStyle")
    List<Investment> findByInvestmentStyle(
            @Param("investmentStyle") com.radomskyi.budgeter.domain.entity.investment.InvestmentStyle investmentStyle);

    // Find all investments with non-zero units (active positions)
    @Query("SELECT i FROM Investment i WHERE i.totalUnits > 0")
    List<Investment> findActiveInvestments();

    // Find all investments with non-zero units (active positions) with pagination
    @Query("SELECT i FROM Investment i WHERE i.totalUnits > 0")
    Page<Investment> findActiveInvestments(Pageable pageable);
}

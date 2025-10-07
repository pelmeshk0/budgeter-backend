package com.radomskyi.budgeter.repository;

import com.radomskyi.budgeter.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvestmentTransactionRepository extends JpaRepository<InvestmentTransaction, Long> {

    // Find all transactions for a specific asset
    List<InvestmentTransaction> findByAsset(Asset asset);

    // Find all transactions for a specific asset with pagination
    Page<InvestmentTransaction> findByAsset(Asset asset, Pageable pageable);

    // Find all transactions for a specific asset ordered by creation date (oldest first for FIFO)
    List<InvestmentTransaction> findByAssetOrderByCreatedAtAsc(Asset asset);

    // Find BUY transactions for an asset (for cost basis calculation)
    List<InvestmentTransaction> findByAssetAndTransactionTypeOrderByCreatedAtAsc(Asset asset, InvestmentTransactionType transactionType);

    // Find SELL transactions for an asset (for realized gains/losses)
    List<InvestmentTransaction> findByAssetAndTransactionTypeOrderByCreatedAtDesc(Asset asset, InvestmentTransactionType transactionType);

    // Find transactions by asset and date range
    List<InvestmentTransaction> findByAssetAndCreatedAtBetween(Asset asset, LocalDateTime startDate, LocalDateTime endDate);

    // Find transactions by asset, type and date range
    List<InvestmentTransaction> findByAssetAndTransactionTypeAndCreatedAtBetween(
        Asset asset, InvestmentTransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate);

    // Find transactions by currency
    List<InvestmentTransaction> findByCurrency(Currency currency);

    // Find transactions by currency with pagination
    Page<InvestmentTransaction> findByCurrency(Currency currency, Pageable pageable);

    // Find transactions within a date range
    List<InvestmentTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find transactions within a date range with pagination
    Page<InvestmentTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Find transactions by asset type
    @Query("SELECT it FROM InvestmentTransaction it WHERE it.asset.assetType = :assetType")
    List<InvestmentTransaction> findByAssetType(@Param("assetType") AssetType assetType);

    // Find transactions by investment style
    @Query("SELECT it FROM InvestmentTransaction it WHERE it.asset.investmentStyle = :investmentStyle")
    List<InvestmentTransaction> findByInvestmentStyle(@Param("investmentStyle") InvestmentStyle investmentStyle);

    // Calculate total units held for an asset (current position)
    @Query("SELECT COALESCE(SUM(CASE WHEN it.transactionType = 'BUY' THEN it.units ELSE -it.units END), 0) " +
           "FROM InvestmentTransaction it WHERE it.asset = :asset")
    BigDecimal getTotalUnitsForAsset(@Param("asset") Asset asset);

    // Calculate total cost basis for an asset (sum of all BUY transactions in EUR)
    @Query("SELECT COALESCE(SUM(it.amount), 0) FROM InvestmentTransaction it " +
           "WHERE it.asset = :asset AND it.transactionType = 'BUY'")
    BigDecimal getTotalCostBasisForAsset(@Param("asset") Asset asset);

    // Calculate total realized gains/losses for an asset
    @Query("SELECT COALESCE(SUM(it.realizedGainLoss), 0) FROM InvestmentTransaction it " +
           "WHERE it.asset = :asset AND it.transactionType = 'SELL' AND it.realizedGainLoss IS NOT NULL")
    BigDecimal getTotalRealizedGainsForAsset(@Param("asset") Asset asset);

    // Calculate total portfolio value by asset type
    @Query("SELECT it.asset.assetType, SUM(it.amount) FROM InvestmentTransaction it " +
           "WHERE it.transactionType = 'BUY' GROUP BY it.asset.assetType")
    List<Object[]> getPortfolioValueByAssetType();

    // Calculate total portfolio value by investment style
    @Query("SELECT it.asset.investmentStyle, SUM(it.amount) FROM InvestmentTransaction it " +
           "WHERE it.transactionType = 'BUY' GROUP BY it.asset.investmentStyle")
    List<Object[]> getPortfolioValueByInvestmentStyle();

    // Find all assets with current positions (non-zero units)
    @Query("SELECT DISTINCT it.asset FROM InvestmentTransaction it " +
           "GROUP BY it.asset HAVING SUM(CASE WHEN it.transactionType = 'BUY' THEN it.units ELSE -it.units END) > 0")
    List<Asset> findAssetsWithPositions();

    // Calculate current portfolio value in EUR
    @Query("SELECT SUM(CASE WHEN it.transactionType = 'BUY' THEN it.amount ELSE -it.amount END) " +
           "FROM InvestmentTransaction it")
    BigDecimal getTotalPortfolioValue();

    // Find transactions for tax year (German tax year is calendar year)
    List<InvestmentTransaction> findByCreatedAtBetweenAndTransactionType(
        LocalDateTime startDate, LocalDateTime endDate, InvestmentTransactionType transactionType);

    // Count transactions by type for reporting
    long countByTransactionType(InvestmentTransactionType transactionType);

    // Find transactions ordered by creation date (newest first)
    List<InvestmentTransaction> findAllByOrderByCreatedAtDesc();

    // Find transactions ordered by creation date (newest first) with pagination
    Page<InvestmentTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

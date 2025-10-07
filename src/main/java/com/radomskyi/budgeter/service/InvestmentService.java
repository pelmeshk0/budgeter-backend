package com.radomskyi.budgeter.service;

import com.radomskyi.budgeter.domain.entity.investment.*;
import com.radomskyi.budgeter.domain.service.InvestmentServiceInterface;
import com.radomskyi.budgeter.dto.InvestmentTransactionRequest;
import com.radomskyi.budgeter.dto.InvestmentTransactionResponse;
import com.radomskyi.budgeter.exception.InvestmentTransactionNotFoundException;
import com.radomskyi.budgeter.repository.AssetRepository;
import com.radomskyi.budgeter.repository.InvestmentTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvestmentService implements InvestmentServiceInterface {

    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final AssetRepository assetRepository;

    /**
     * Create a new investment transaction
     */
    @Override
    @Transactional
    public InvestmentTransactionResponse create(InvestmentTransactionRequest request) {
        log.info("Creating new investment transaction for asset: {} with amount: {}",
                request.getAssetName(), request.getUnits().multiply(request.getPricePerUnit()));

        // Find or create asset
        Asset asset = findOrCreateAsset(request);

        // Create investment transaction
        InvestmentTransaction transaction = InvestmentTransaction.builder()
                .transactionType(request.getTransactionType())
                .asset(asset)
                .units(request.getUnits())
                .pricePerUnit(request.getPricePerUnit())
                .fees(request.getFees())
                .currency(request.getCurrency())
                .exchangeRate(request.getExchangeRate())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        // Calculate the amount after building
        transaction.calculateAmount();

        InvestmentTransaction savedTransaction = investmentTransactionRepository.save(transaction);
        log.info("Successfully created investment transaction with id: {}", savedTransaction.getId());

        // todo here and everywhere perform mapping to InvestmentTransactionResponse at controller level
        //  service layer should only be responsible for business logic and operate with entities as return type
        return mapToResponse(savedTransaction);
    }

    /**
     * Get investment transaction by ID
     */
    @Override
    public InvestmentTransactionResponse getById(Long id) {
        log.info("Fetching investment transaction with id: {}", id);

        InvestmentTransaction transaction = investmentTransactionRepository.findById(id)
                .orElseThrow(() -> new InvestmentTransactionNotFoundException("Investment transaction not found with id: " + id));

        return mapToResponse(transaction);
    }

    /**
     * Get all investment transactions with pagination
     */
    @Override
    public Page<InvestmentTransactionResponse> getAll(Pageable pageable) {
        log.info("Fetching all investment transactions with pagination: {}", pageable);

        Page<InvestmentTransaction> transactions = investmentTransactionRepository.findAll(pageable);
        return transactions.map(this::mapToResponse);
    }

    /**
     * Update an existing investment transaction
     */
    @Override
    @Transactional
    public InvestmentTransactionResponse update(Long id, InvestmentTransactionRequest request) {
        log.info("Updating investment transaction with id: {}", id);

        InvestmentTransaction existingTransaction = investmentTransactionRepository.findById(id)
                .orElseThrow(() -> new InvestmentTransactionNotFoundException("Investment transaction not found with id: " + id));

        // Find or create asset if ticker/name changed
        Asset asset = findOrCreateAsset(request);

        existingTransaction.setTransactionType(request.getTransactionType());
        existingTransaction.setAsset(asset);
        existingTransaction.setUnits(request.getUnits());
        existingTransaction.setPricePerUnit(request.getPricePerUnit());
        existingTransaction.setFees(request.getFees());
        existingTransaction.setCurrency(request.getCurrency());
        existingTransaction.setExchangeRate(request.getExchangeRate());
        existingTransaction.setName(request.getName());
        existingTransaction.setDescription(request.getDescription());

        // Recalculate amount based on updated data
        recalculateAmount(existingTransaction);

        InvestmentTransaction updatedTransaction = investmentTransactionRepository.save(existingTransaction);
        log.info("Successfully updated investment transaction with id: {}", updatedTransaction.getId());

        return mapToResponse(updatedTransaction);
    }

    /**
     * Delete an investment transaction by ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting investment transaction with id: {}", id);

        if (!investmentTransactionRepository.existsById(id)) {
            throw new InvestmentTransactionNotFoundException("Investment transaction not found with id: " + id);
        }

        investmentTransactionRepository.deleteById(id);
        log.info("Successfully deleted investment transaction with id: {}", id);
    }

    /**
     * Find existing asset or create a new one
     */
    private Asset findOrCreateAsset(InvestmentTransactionRequest request) {
        Asset asset = null;

        // Try to find by ISIN first (most reliable identifier)
        if (request.getAssetIsin() != null && !request.getAssetIsin().trim().isEmpty()) {
            asset = assetRepository.findByIsin(request.getAssetIsin()).orElse(null);
        }

        // If not found by ISIN, try by ticker
        if (asset == null && request.getAssetTicker() != null && !request.getAssetTicker().trim().isEmpty()) {
            asset = assetRepository.findByTicker(request.getAssetTicker()).orElse(null);
        }

        // If asset doesn't exist, create a new one
        if (asset == null) {
            asset = Asset.builder()
                    .ticker(request.getAssetTicker())
                    .name(request.getAssetName())
                    .isin(request.getAssetIsin())
                    .assetType(AssetType.STOCK) // Default, could be improved with better detection logic
                    .investmentStyle(InvestmentStyle.GROWTH) // Default, could be improved
                    .build();

            asset = assetRepository.save(asset);
            log.info("Created new asset: {} ({})", asset.getName(), asset.getTicker());
        }

        return asset;
    }

    /**
     * Recalculate the amount for an investment transaction
     */
    private void recalculateAmount(InvestmentTransaction transaction) {
        transaction.calculateAmount();
    }

    /**
     * Map InvestmentTransaction entity to InvestmentTransactionResponse DTO
     */
    // todo as a part of working with entities at the service layer, move these helper functions to the controller level
    private InvestmentTransactionResponse mapToResponse(InvestmentTransaction transaction) {
        return InvestmentTransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .assetTicker(transaction.getAsset().getTicker())
                .assetName(transaction.getAsset().getName())
                .assetIsin(transaction.getAsset().getIsin())
                .units(transaction.getUnits())
                .pricePerUnit(transaction.getPricePerUnit())
                .fees(transaction.getFees())
                .currency(transaction.getCurrency())
                .exchangeRate(transaction.getExchangeRate())
                .realizedGainLoss(transaction.getRealizedGainLoss())
                .amount(transaction.getAmount())
                .name(transaction.getName())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}

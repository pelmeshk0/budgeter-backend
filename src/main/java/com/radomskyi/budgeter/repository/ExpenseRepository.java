package com.radomskyi.budgeter.repository;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Expense;
import com.radomskyi.budgeter.domain.Tag;
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
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Find expenses by category
    List<Expense> findByCategory(Category category);
    
    // Find expenses by category with pagination
    Page<Expense> findByCategory(Category category, Pageable pageable);
    
    // Find expenses within a date range
    List<Expense> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find expenses within a date range with pagination
    Page<Expense> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find expenses by category and date range
    List<Expense> findByCategoryAndCreatedAtBetween(Category category, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find expenses by category and date range with pagination
    Page<Expense> findByCategoryAndCreatedAtBetween(Category category, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find expenses with amount greater than specified amount
    List<Expense> findByAmountGreaterThan(BigDecimal amount);
    
    // Find expenses with amount between specified amounts
    List<Expense> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find expenses by description containing text (case insensitive)
    List<Expense> findByDescriptionContainingIgnoreCase(String description);
    
    // Find expenses by tag
    @Query("SELECT e FROM Expense e WHERE :tag MEMBER OF e.tags")
    List<Expense> findByTag(@Param("tag") Tag tag);
    
    // Find expenses by multiple tags
    @Query("SELECT DISTINCT e FROM Expense e JOIN e.tags t WHERE t IN :tags")
    List<Expense> findByTagsIn(@Param("tags") List<Tag> tags);
    
    // Find expenses by tag with pagination
    @Query("SELECT e FROM Expense e WHERE :tag MEMBER OF e.tags")
    Page<Expense> findByTag(@Param("tag") Tag tag, Pageable pageable);
    
    // Calculate total expenses by category
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category = :category")
    BigDecimal sumByCategory(@Param("category") Category category);
    
    // Calculate total expenses by category within date range
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category = :category AND e.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumByCategoryAndDateRange(@Param("category") Category category, 
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    // Calculate total expenses within date range
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumByDateRange(@Param("startDate") LocalDateTime startDate, 
                             @Param("endDate") LocalDateTime endDate);
    
    // Find expenses ordered by creation date (newest first)
    List<Expense> findAllByOrderByCreatedAtDesc();
    
    // Find expenses ordered by creation date (newest first) with pagination
    Page<Expense> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Find expenses ordered by amount (highest first)
    List<Expense> findAllByOrderByAmountDesc();
    
    // Find expenses ordered by amount (highest first) with pagination
    Page<Expense> findAllByOrderByAmountDesc(Pageable pageable);
}

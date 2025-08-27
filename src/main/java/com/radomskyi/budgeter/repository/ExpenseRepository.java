package com.radomskyi.budgeter.repository;

import com.radomskyi.budgeter.domain.Category;
import com.radomskyi.budgeter.domain.Expense;
import com.radomskyi.budgeter.domain.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    @Query("{'category': ?0}")
    List<Expense> findByCategory(Category category);

    @Query("{'tags': {$in: ?0}}")
    List<Expense> findByTags(List<Tag> tags);

    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Expense> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{'value': {$gte: ?0}}")
    List<Expense> findByValueGreaterThanEqual(double minValue);

    @Query("{'value': {$lte: ?0}}")
    List<Expense> findByValueLessThanEqual(double maxValue);
}

package com.radomskyi.budgeter.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Base interface defining common CRUD operations for entity services.
 * This interface provides a template for standard service operations.
 *
 * @param <T> The request DTO type
 * @param <R> The response DTO type
 */
public interface BaseService<T, R> {

    /**
     * Creates a new entity.
     *
     * @param request The request object containing entity data
     * @return The created entity as response DTO
     */
    R create(T request);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve
     * @return The entity as response DTO
     */
    R getById(Long id);

    /**
     * Retrieves all entities with pagination support.
     *
     * @param pageable Pagination parameters
     * @return Page of entities as response DTOs
     */
    Page<R> getAll(Pageable pageable);

    /**
     * Updates an existing entity.
     *
     * @param id The ID of the entity to update
     * @param request The request object containing updated entity data
     * @return The updated entity as response DTO
     */
    R update(Long id, T request);

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete
     */
    void delete(Long id);
}

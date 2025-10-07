package com.radomskyi.budgeter.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Base interface defining common CRUD operations for entity controllers.
 * This interface provides a template for standard REST API operations.
 *
 * @param <T> The request DTO type
 * @param <R> The response DTO type
 */
public interface BaseController<T, R> {

    /**
     * Creates a new entity.
     *
     * @param request The request object containing entity data
     * @return ResponseEntity containing the created entity
     */
    @Operation(summary = "Create entity", description = "Creates a new entity with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entity created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    ResponseEntity<R> create(@Valid @RequestBody T request);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve
     * @return ResponseEntity containing the entity
     */
    @Operation(summary = "Get entity by ID", description = "Retrieves a specific entity by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity found"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    ResponseEntity<R> getById(@Parameter(description = "ID of the entity to retrieve") @PathVariable Long id);

    /**
     * Retrieves all entities with pagination support.
     *
     * @param pageable Pagination parameters
     * @return ResponseEntity containing a page of entities
     */
    @Operation(summary = "Get all entities", description = "Retrieves all entities with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entities retrieved successfully")
    })
    ResponseEntity<Page<R>> getAll(@Parameter(hidden = true) Pageable pageable);

    /**
     * Updates an existing entity.
     *
     * @param id The ID of the entity to update
     * @param request The request object containing updated entity data
     * @return ResponseEntity containing the updated entity
     */
    @Operation(summary = "Update entity", description = "Updates an existing entity with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    ResponseEntity<R> update(
            @Parameter(description = "ID of the entity to update") @PathVariable Long id,
            @Valid @RequestBody T request);

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @return ResponseEntity indicating the deletion result
     */
    @Operation(summary = "Delete entity", description = "Deletes an entity by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entity deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    ResponseEntity<Void> delete(@Parameter(description = "ID of the entity to delete") @PathVariable Long id);
}

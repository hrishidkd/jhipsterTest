package com.book.app.service;

import com.book.app.domain.Books;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.book.app.domain.Books}.
 */
public interface BooksService {
    /**
     * Save a books.
     *
     * @param books the entity to save.
     * @return the persisted entity.
     */
    Books save(Books books);

    /**
     * Updates a books.
     *
     * @param books the entity to update.
     * @return the persisted entity.
     */
    Books update(Books books);

    /**
     * Partially updates a books.
     *
     * @param books the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Books> partialUpdate(Books books);

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Books> findAll(Pageable pageable);

    /**
     * Get the "id" books.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Books> findOne(Long id);

    /**
     * Delete the "id" books.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

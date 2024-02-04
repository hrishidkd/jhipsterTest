package com.book.app.web.rest;

import com.book.app.domain.Books;
import com.book.app.repository.BooksRepository;
import com.book.app.service.BooksQueryService;
import com.book.app.service.BooksService;
import com.book.app.service.criteria.BooksCriteria;
import com.book.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.book.app.domain.Books}.
 */
@RestController
@RequestMapping("/api/books")
public class BooksResource {

    private final Logger log = LoggerFactory.getLogger(BooksResource.class);

    private static final String ENTITY_NAME = "books";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BooksService booksService;

    private final BooksRepository booksRepository;

    private final BooksQueryService booksQueryService;

    public BooksResource(BooksService booksService, BooksRepository booksRepository, BooksQueryService booksQueryService) {
        this.booksService = booksService;
        this.booksRepository = booksRepository;
        this.booksQueryService = booksQueryService;
    }

    /**
     * {@code POST  /books} : Create a new books.
     *
     * @param books the books to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new books, or with status {@code 400 (Bad Request)} if the books has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Books> createBooks(@Valid @RequestBody Books books) throws URISyntaxException {
        log.debug("REST request to save Books : {}", books);
        if (books.getId() != null) {
            throw new BadRequestAlertException("A new books cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Books result = booksService.save(books);
        return ResponseEntity
            .created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /books/:id} : Updates an existing books.
     *
     * @param id the id of the books to save.
     * @param books the books to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated books,
     * or with status {@code 400 (Bad Request)} if the books is not valid,
     * or with status {@code 500 (Internal Server Error)} if the books couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Books> updateBooks(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Books books)
        throws URISyntaxException {
        log.debug("REST request to update Books : {}, {}", id, books);
        if (books.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, books.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!booksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Books result = booksService.update(books);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, books.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /books/:id} : Partial updates given fields of an existing books, field will ignore if it is null
     *
     * @param id the id of the books to save.
     * @param books the books to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated books,
     * or with status {@code 400 (Bad Request)} if the books is not valid,
     * or with status {@code 404 (Not Found)} if the books is not found,
     * or with status {@code 500 (Internal Server Error)} if the books couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Books> partialUpdateBooks(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Books books
    ) throws URISyntaxException {
        log.debug("REST request to partial update Books partially : {}, {}", id, books);
        if (books.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, books.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!booksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Books> result = booksService.partialUpdate(books);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, books.getId().toString())
        );
    }

    /**
     * {@code GET  /books} : get all the books.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of books in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Books>> getAllBooks(
        BooksCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Books by criteria: {}", criteria);

        Page<Books> page = booksQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /books/count} : count all the books.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBooks(BooksCriteria criteria) {
        log.debug("REST request to count Books by criteria: {}", criteria);
        return ResponseEntity.ok().body(booksQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /books/:id} : get the "id" books.
     *
     * @param id the id of the books to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the books, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Books> getBooks(@PathVariable("id") Long id) {
        log.debug("REST request to get Books : {}", id);
        Optional<Books> books = booksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(books);
    }

    /**
     * {@code DELETE  /books/:id} : delete the "id" books.
     *
     * @param id the id of the books to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooks(@PathVariable("id") Long id) {
        log.debug("REST request to delete Books : {}", id);
        booksService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.book.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.app.IntegrationTest;
import com.book.app.domain.Author;
import com.book.app.domain.Books;
import com.book.app.repository.BooksRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BooksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BooksResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 0D;
    private static final Double UPDATED_PRICE = 1D;
    private static final Double SMALLER_PRICE = 0D - 1D;

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBooksMockMvc;

    private Books books;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createEntity(EntityManager em) {
        Books books = new Books().title(DEFAULT_TITLE).price(DEFAULT_PRICE);
        // Add required entity
        Author author;
        if (TestUtil.findAll(em, Author.class).isEmpty()) {
            author = AuthorResourceIT.createEntity(em);
            em.persist(author);
            em.flush();
        } else {
            author = TestUtil.findAll(em, Author.class).get(0);
        }
        books.setAuthor(author);
        return books;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createUpdatedEntity(EntityManager em) {
        Books books = new Books().title(UPDATED_TITLE).price(UPDATED_PRICE);
        // Add required entity
        Author author;
        if (TestUtil.findAll(em, Author.class).isEmpty()) {
            author = AuthorResourceIT.createUpdatedEntity(em);
            em.persist(author);
            em.flush();
        } else {
            author = TestUtil.findAll(em, Author.class).get(0);
        }
        books.setAuthor(author);
        return books;
    }

    @BeforeEach
    public void initTest() {
        books = createEntity(em);
    }

    @Test
    @Transactional
    void createBooks() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();
        // Create the Books
        restBooksMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isCreated());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate + 1);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBooks.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createBooksWithExistingId() throws Exception {
        // Create the Books with an existing ID
        books.setId(1L);

        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBooksMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = booksRepository.findAll().size();
        // set the field null
        books.setTitle(null);

        // Create the Books, which fails.

        restBooksMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = booksRepository.findAll().size();
        // set the field null
        books.setPrice(null);

        // Create the Books, which fails.

        restBooksMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList
        restBooksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void getBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get the books
        restBooksMockMvc
            .perform(get(ENTITY_API_URL_ID, books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(books.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        Long id = books.getId();

        defaultBooksShouldBeFound("id.equals=" + id);
        defaultBooksShouldNotBeFound("id.notEquals=" + id);

        defaultBooksShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBooksShouldNotBeFound("id.greaterThan=" + id);

        defaultBooksShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBooksShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where title equals to DEFAULT_TITLE
        defaultBooksShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the booksList where title equals to UPDATED_TITLE
        defaultBooksShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultBooksShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the booksList where title equals to UPDATED_TITLE
        defaultBooksShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where title is not null
        defaultBooksShouldBeFound("title.specified=true");

        // Get all the booksList where title is null
        defaultBooksShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTitleContainsSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where title contains DEFAULT_TITLE
        defaultBooksShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the booksList where title contains UPDATED_TITLE
        defaultBooksShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where title does not contain DEFAULT_TITLE
        defaultBooksShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the booksList where title does not contain UPDATED_TITLE
        defaultBooksShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price equals to DEFAULT_PRICE
        defaultBooksShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the booksList where price equals to UPDATED_PRICE
        defaultBooksShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultBooksShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the booksList where price equals to UPDATED_PRICE
        defaultBooksShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price is not null
        defaultBooksShouldBeFound("price.specified=true");

        // Get all the booksList where price is null
        defaultBooksShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price is greater than or equal to DEFAULT_PRICE
        defaultBooksShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the booksList where price is greater than or equal to UPDATED_PRICE
        defaultBooksShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price is less than or equal to DEFAULT_PRICE
        defaultBooksShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the booksList where price is less than or equal to SMALLER_PRICE
        defaultBooksShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price is less than DEFAULT_PRICE
        defaultBooksShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the booksList where price is less than UPDATED_PRICE
        defaultBooksShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList where price is greater than DEFAULT_PRICE
        defaultBooksShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the booksList where price is greater than SMALLER_PRICE
        defaultBooksShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsEqualToSomething() throws Exception {
        Author author;
        if (TestUtil.findAll(em, Author.class).isEmpty()) {
            booksRepository.saveAndFlush(books);
            author = AuthorResourceIT.createEntity(em);
        } else {
            author = TestUtil.findAll(em, Author.class).get(0);
        }
        em.persist(author);
        em.flush();
        books.setAuthor(author);
        booksRepository.saveAndFlush(books);
        Long authorId = author.getId();
        // Get all the booksList where author equals to authorId
        defaultBooksShouldBeFound("authorId.equals=" + authorId);

        // Get all the booksList where author equals to (authorId + 1)
        defaultBooksShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBooksShouldBeFound(String filter) throws Exception {
        restBooksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restBooksMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBooksShouldNotBeFound(String filter) throws Exception {
        restBooksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBooksMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBooks() throws Exception {
        // Get the books
        restBooksMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books
        Books updatedBooks = booksRepository.findById(books.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBooks are not directly saved in db
        em.detach(updatedBooks);
        updatedBooks.title(UPDATED_TITLE).price(UPDATED_PRICE);

        restBooksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBooks.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBooks))
            )
            .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBooks.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();
        books.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBooksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, books.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();
        books.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBooksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();
        books.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBooksMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBooksWithPatch() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books using partial update
        Books partialUpdatedBooks = new Books();
        partialUpdatedBooks.setId(books.getId());

        partialUpdatedBooks.title(UPDATED_TITLE).price(UPDATED_PRICE);

        restBooksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooks.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBooks))
            )
            .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBooks.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateBooksWithPatch() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books using partial update
        Books partialUpdatedBooks = new Books();
        partialUpdatedBooks.setId(books.getId());

        partialUpdatedBooks.title(UPDATED_TITLE).price(UPDATED_PRICE);

        restBooksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooks.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBooks))
            )
            .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBooks.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();
        books.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBooksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, books.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();
        books.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBooksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();
        books.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBooksMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(books))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        int databaseSizeBeforeDelete = booksRepository.findAll().size();

        // Delete the books
        restBooksMockMvc
            .perform(delete(ENTITY_API_URL_ID, books.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

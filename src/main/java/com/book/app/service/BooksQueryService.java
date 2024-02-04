package com.book.app.service;

import com.book.app.domain.*; // for static metamodels
import com.book.app.domain.Books;
import com.book.app.repository.BooksRepository;
import com.book.app.service.criteria.BooksCriteria;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Books} entities in the database.
 * The main input is a {@link BooksCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Books} or a {@link Page} of {@link Books} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BooksQueryService extends QueryService<Books> {

    private final Logger log = LoggerFactory.getLogger(BooksQueryService.class);

    private final BooksRepository booksRepository;

    public BooksQueryService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    /**
     * Return a {@link List} of {@link Books} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Books> findByCriteria(BooksCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Books> specification = createSpecification(criteria);
        return booksRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Books} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Books> findByCriteria(BooksCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Books> specification = createSpecification(criteria);
        return booksRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BooksCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Books> specification = createSpecification(criteria);
        return booksRepository.count(specification);
    }

    /**
     * Function to convert {@link BooksCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Books> createSpecification(BooksCriteria criteria) {
        Specification<Books> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Books_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Books_.title));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), Books_.price));
            }
            if (criteria.getAuthorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAuthorId(), root -> root.join(Books_.author, JoinType.LEFT).get(Author_.id))
                    );
            }
        }
        return specification;
    }
}

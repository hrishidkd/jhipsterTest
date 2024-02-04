package com.book.app.repository;

import com.book.app.domain.Books;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Books entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BooksRepository extends JpaRepository<Books, Long>, JpaSpecificationExecutor<Books> {}

package com.book.app.domain;

import static com.book.app.domain.AuthorTestSamples.*;
import static com.book.app.domain.BooksTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.book.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BooksTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Books.class);
        Books books1 = getBooksSample1();
        Books books2 = new Books();
        assertThat(books1).isNotEqualTo(books2);

        books2.setId(books1.getId());
        assertThat(books1).isEqualTo(books2);

        books2 = getBooksSample2();
        assertThat(books1).isNotEqualTo(books2);
    }

    @Test
    void authorTest() throws Exception {
        Books books = getBooksRandomSampleGenerator();
        Author authorBack = getAuthorRandomSampleGenerator();

        books.setAuthor(authorBack);
        assertThat(books.getAuthor()).isEqualTo(authorBack);

        books.author(null);
        assertThat(books.getAuthor()).isNull();
    }
}

package com.book.app.domain;

import static com.book.app.domain.AuthorTestSamples.*;
import static com.book.app.domain.BooksTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.book.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AuthorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Author.class);
        Author author1 = getAuthorSample1();
        Author author2 = new Author();
        assertThat(author1).isNotEqualTo(author2);

        author2.setId(author1.getId());
        assertThat(author1).isEqualTo(author2);

        author2 = getAuthorSample2();
        assertThat(author1).isNotEqualTo(author2);
    }

    @Test
    void booksTest() throws Exception {
        Author author = getAuthorRandomSampleGenerator();
        Books booksBack = getBooksRandomSampleGenerator();

        author.addBooks(booksBack);
        assertThat(author.getBooks()).containsOnly(booksBack);
        assertThat(booksBack.getAuthor()).isEqualTo(author);

        author.removeBooks(booksBack);
        assertThat(author.getBooks()).doesNotContain(booksBack);
        assertThat(booksBack.getAuthor()).isNull();

        author.books(new HashSet<>(Set.of(booksBack)));
        assertThat(author.getBooks()).containsOnly(booksBack);
        assertThat(booksBack.getAuthor()).isEqualTo(author);

        author.setBooks(new HashSet<>());
        assertThat(author.getBooks()).doesNotContain(booksBack);
        assertThat(booksBack.getAuthor()).isNull();
    }
}

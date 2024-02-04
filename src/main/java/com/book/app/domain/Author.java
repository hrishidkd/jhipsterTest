package com.book.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Author.
 */
@Entity
@Table(name = "author")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 0)
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    @JsonIgnoreProperties(value = { "author" }, allowSetters = true)
    private Set<Books> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Author id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Author name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Books> getBooks() {
        return this.books;
    }

    public void setBooks(Set<Books> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.setAuthor(null));
        }
        if (books != null) {
            books.forEach(i -> i.setAuthor(this));
        }
        this.books = books;
    }

    public Author books(Set<Books> books) {
        this.setBooks(books);
        return this;
    }

    public Author addBooks(Books books) {
        this.books.add(books);
        books.setAuthor(this);
        return this;
    }

    public Author removeBooks(Books books) {
        this.books.remove(books);
        books.setAuthor(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Author)) {
            return false;
        }
        return getId() != null && getId().equals(((Author) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Author{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}

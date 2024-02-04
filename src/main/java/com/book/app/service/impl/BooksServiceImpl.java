package com.book.app.service.impl;

import com.book.app.broker.MessageProducer;
import com.book.app.constants.TopicConstantI;
import com.book.app.domain.Books;
import com.book.app.repository.BooksRepository;
import com.book.app.service.BooksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.book.app.domain.Books}.
 */
@Service
@Transactional
public class BooksServiceImpl implements BooksService {

    private final Logger log = LoggerFactory.getLogger(BooksServiceImpl.class);

    private final BooksRepository booksRepository;

    @Autowired
    private final MessageProducer messageProducer;

    @Autowired
    private final ObjectMapper mapper;

    public BooksServiceImpl(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
        this.messageProducer = new MessageProducer();
        this.mapper = new ObjectMapper();
    }

    @Override
    public Books save(Books books) {
        log.debug("Request to save Books : {}", books);
        try {
            String msg = mapper.writeValueAsString(books);

            messageProducer.sendMessage(TopicConstantI.PUBLISH_BOOK, msg);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return booksRepository.save(books);
    }

    @Override
    public Books update(Books books) {
        log.debug("Request to update Books : {}", books);
        return booksRepository.save(books);
    }

    @Override
    public Optional<Books> partialUpdate(Books books) {
        log.debug("Request to partially update Books : {}", books);

        return booksRepository
            .findById(books.getId())
            .map(existingBooks -> {
                if (books.getTitle() != null) {
                    existingBooks.setTitle(books.getTitle());
                }
                if (books.getPrice() != null) {
                    existingBooks.setPrice(books.getPrice());
                }

                return existingBooks;
            })
            .map(booksRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Books> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return booksRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Books> findOne(Long id) {
        log.debug("Request to get Books : {}", id);
        return booksRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Books : {}", id);
        booksRepository.deleteById(id);
    }
}

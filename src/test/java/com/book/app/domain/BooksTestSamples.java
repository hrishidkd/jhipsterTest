package com.book.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BooksTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Books getBooksSample1() {
        return new Books().id(1L).title("title1");
    }

    public static Books getBooksSample2() {
        return new Books().id(2L).title("title2");
    }

    public static Books getBooksRandomSampleGenerator() {
        return new Books().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}

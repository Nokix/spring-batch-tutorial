package com.example.springbatchtutorial.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;

public class IterableReader<T> implements ItemReader<T> {
    private final Iterator<T> iterator;

    public IterableReader(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public IterableReader(Iterable<T> iterable) {
        this.iterator = iterable.iterator();
    }

    @Override
    public T read() throws Exception,
            UnexpectedInputException, ParseException, NonTransientResourceException {
        return iterator.hasNext() ? iterator.next() : null;
    }
}

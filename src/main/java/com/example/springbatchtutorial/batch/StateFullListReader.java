package com.example.springbatchtutorial.batch;

import org.springframework.batch.item.*;

import java.util.List;

public class StateFullListReader<T> implements ItemStreamReader<T> {
    private static String CURRENT_INDEX_KEY = "currentIndex";

    private List<T> list;
    private int currentIndex;
    private long delayTimeInMillis;

    private boolean restarted = false;

    public StateFullListReader(List<T> list, long delayTimeInMillis) {
        this.list = list;
        this.delayTimeInMillis = delayTimeInMillis;
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Thread.sleep(delayTimeInMillis);
        if(currentIndex == 7 && !restarted) throw new RuntimeException("TOO MUCH!");
        if (currentIndex < list.size()) return list.get(currentIndex++);
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        restarted = executionContext.containsKey(CURRENT_INDEX_KEY);
        currentIndex = executionContext.getInt(CURRENT_INDEX_KEY, 0);
        System.out.println("Opened at: "+ currentIndex);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

        executionContext.putInt(CURRENT_INDEX_KEY, currentIndex);
        System.out.println("Saved at: "+ currentIndex);

    }

}

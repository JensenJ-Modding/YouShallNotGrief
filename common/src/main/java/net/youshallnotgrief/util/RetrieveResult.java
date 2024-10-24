package net.youshallnotgrief.util;

import java.util.ArrayList;

public class RetrieveResult<T> {
    private final ArrayList<T> records;
    private final int count;

    public RetrieveResult(ArrayList<T> records, int count) {
        this.records = records;
        this.count = count;
    }

    public ArrayList<T> getRecords() {
        return records;
    }

    public int getCount() {
        return count;
    }
}

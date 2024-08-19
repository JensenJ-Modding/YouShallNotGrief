package net.youshallnotsteal.database.manager;

public interface DataManager<T> {
    void addToDatabase(T data);
    void commitQueuedToDatabase();
    void createTableIfNotExists();
}

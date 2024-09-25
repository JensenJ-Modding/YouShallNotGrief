package net.youshallnotgrief.database.manager;

public interface DataManager<T> {
    void addToDatabase(T data);
    void commitQueuedToDatabase();
    void createTableIfNotExists();
}

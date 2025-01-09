package net.youshallnotgrief.database.data;

import net.minecraft.network.chat.Component;

import java.sql.Timestamp;

public abstract class BaseData {

    public Timestamp timestamp;

    public BaseData(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public abstract void queue();
    public abstract void queueForeignTables();
    public abstract Component formatDataForInspection();
}

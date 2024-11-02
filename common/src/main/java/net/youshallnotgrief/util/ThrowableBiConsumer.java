package net.youshallnotgrief.util;

import java.sql.SQLException;

@FunctionalInterface
public interface ThrowableBiConsumer<T, U>
{
    void accept(T t, U u) throws SQLException;
}

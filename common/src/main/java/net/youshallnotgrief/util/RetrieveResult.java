package net.youshallnotgrief.util;

import java.util.ArrayList;

public record RetrieveResult<T>(ArrayList<T> records, int count) {
}

package net.youshallnotgrief.inspection;

import java.util.ArrayList;

public record RetrieveResult<T>(ArrayList<T> records, int count) {
}

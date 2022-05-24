package ru.mobnius.core.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DbOperationTypeTest {
    @Test
    public void equalString() {
        assertEquals(DbOperationType.CREATED, "CREATED");
        assertEquals(DbOperationType.UPDATED, "UPDATED");
        assertEquals(DbOperationType.REMOVED, "REMOVED");
    }
}
package ru.mobnius.core.data.rpc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilterItemTest {

    @Test
    public void getInstance() {
        FilterItem filterItem = new FilterItem("name", "test");
        assertEquals(filterItem.operator, FilterItem.OPERATOR_EQUAL);

        filterItem = new FilterItem("name", null, "test");
        assertEquals(filterItem.operator, FilterItem.OPERATOR_EQUAL);

        assertEquals(FilterItem.OPERATOR_EQUAL, "=");
        assertEquals(FilterItem.OPERATOR_LIKE, "like");
        assertEquals(FilterItem.OPERATOR_NOT_EQUAL, "!=");
        assertEquals(FilterItem.OPERATOR_IN, "in");
        assertEquals(FilterItem.OPERATOR_NOT_IN, "notin");
        assertEquals(FilterItem.OPERATOR_LESS, "<");
        assertEquals(FilterItem.OPERATOR_LESS_EQUAL, "<=");
        assertEquals(FilterItem.OPERATOR_MORE, ">");
        assertEquals(FilterItem.OPERATOR_EQUAL, "=");
        assertEquals(FilterItem.OPERATOR_MORE_EQUAL, ">=");
    }
}
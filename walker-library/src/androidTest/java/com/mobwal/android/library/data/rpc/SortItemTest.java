package com.mobwal.android.library.data.rpc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.mobwal.android.library.data.rpc.SortItem;

public class SortItemTest {

    @Test
    public void getInstance() {
        SortItem sortItem = new SortItem("name");
        assertEquals(sortItem.direction, SortItem.DESC);

        sortItem = new SortItem("name", SortItem.ASC);
        assertEquals(sortItem.direction, SortItem.ASC);
    }
}
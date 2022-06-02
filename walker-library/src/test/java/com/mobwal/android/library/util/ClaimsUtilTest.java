package com.mobwal.android.library.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClaimsUtilTest {
    @Test
    public void isExistTest() {
        ClaimsUtil util = new ClaimsUtil(".admin.user.inspector.");
        assertTrue(util.isExists("user"));
        assertFalse(util.isExists("master"));
    }
}

package com.mobwal.android.library.authorization.credential;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class BasicCredentialTest {

    @Test
    public void auth() {
        BasicCredential basicCredential = new BasicCredential("","");
        BasicUser basicUser = new BasicUser(basicCredential, (long)1, ".admin.master.user.");
        Assert.assertTrue(basicUser.userInRole("user"));
        Assert.assertTrue(basicUser.userInRole("master"));
        Assert.assertTrue(basicUser.userInRole("admin"));
        Assert.assertFalse(basicUser.userInRole("filer"));

        Assert.assertEquals(1, (long) basicUser.getUserId());
    }
}
package ru.mobnius.core.data.credentials;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BasicCredentialsTest {

    private final BasicCredentials credentials;
    public BasicCredentialsTest(){
        credentials = new BasicCredentials("root", "root0");
    }

    @Test
    public void getTokenTest() {
        String validToken = "Token cm9vdDpyb290MA==";
        String returnValue =  credentials.getToken();
        assertEquals(validToken, returnValue);
    }

    @Test
    public void DecodeTest(){
        String validToken = "Token cm9vdDpyb290MA==";
        BasicCredentials credentials = BasicCredentials.decode(validToken);
        assertEquals("root", credentials.login);
        assertEquals("root0", credentials.password);
    }

    @Test
    public void isEqualsPassword() {
        assertTrue(credentials.isEqualsPassword("root0"));
        assertFalse(credentials.isEqualsPassword("root"));
    }
}

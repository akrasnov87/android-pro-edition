package ru.mobnius.core.utils;

import android.text.Spannable;
import android.text.Spanned;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContentUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getContentText() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("{0}", "Hello");
        jsonObject.addProperty("{1}", "World");

        Spanned content = ContentUtil.getContentText("{0}, {1}!!!", jsonObject.toString());
        String html = content.toString();
        assertEquals(html, "Hello, World!!!");

        try {
            ContentUtil.getContentText("{0}, {1}!!!", "123");
            assertFalse(false);
        } catch (JsonSyntaxException e) {
            assertTrue(true);
        }

        jsonObject = new JsonObject();
        jsonObject.addProperty("{0}", "Hello");
        jsonObject.addProperty("{1}", 1);

        content = ContentUtil.getContentText("{0}, {1}!!!", jsonObject.toString());
        html = content.toString();
        assertEquals(html, "Hello, 1!!!");
    }
}
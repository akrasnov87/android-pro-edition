package com.mobwal.android.library.sql;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.util.ReflectionUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

@RunWith(AndroidJUnit4.class)
public class SQLStatementInsertTest {

    private SQLContextTest.SQLContextProfile sqlContext;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sqlContext = new SQLContextTest.SQLContextProfile(appContext);
    }

    @Test
    public void convertToQuery() throws NoSuchFieldException, IllegalAccessException {
        SQLContextTest.Profile profile = new SQLContextTest.Profile();
        profile.id = 111;
        SQLStatementInsert sqlStatementInsert = new SQLStatementInsert(profile, sqlContext.getWritableDatabase());
        sqlStatementInsert.bind(profile);

        Collection<SQLContextTest.Profile> collection = sqlContext.select("select * from " + ReflectionUtil.getTableName(profile.getClass()) + " where id = ?", new String[] { "111" }, profile.getClass());
        assert collection != null;
        Assert.assertEquals(1, collection.size());
    }

    @After
    public void tearDown() {
        sqlContext.trash();
    }
}
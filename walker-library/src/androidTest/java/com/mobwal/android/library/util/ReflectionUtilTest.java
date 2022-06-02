package com.mobwal.android.library.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.mobwal.android.library.annotation.FieldMetaData;
import com.mobwal.android.library.annotation.TableMetaData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
public class ReflectionUtilTest {
    private TestObject testObject;

    @Before
    public void setUp() {
        testObject = new TestObject();
    }

    @Test
    public void reflection() {
        Field field = ReflectionUtil.getClassField(testObject, "link");
        assert field != null;
        Assert.assertEquals("id", field.getName());

        field = ReflectionUtil.getClassField(testObject, "c_name");
        assert field != null;
        Assert.assertEquals("c_name", field.getName());

        String tableName = ReflectionUtil.getTableName(TestObject.class);
        Assert.assertEquals(tableName, "test");

        TableMetaData tableMetaData = ReflectionUtil.getTableMetaData(TestObject.class);
        Assert.assertNotNull(tableMetaData);

        Field[] fields = ReflectionUtil.getDbFields(testObject);
        Assert.assertEquals(fields.length, 2);
    }

    @TableMetaData(name = "test")
    static class TestObject {
        public static String TAG = "TEST";

        @FieldMetaData(name = "link")
        public int id;
        public String c_name;

        private boolean hidden;
    }
}

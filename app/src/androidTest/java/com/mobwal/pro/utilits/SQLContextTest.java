package com.mobwal.pro.utilits;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.mobwal.pro.shared.Profile;
import com.mobwal.pro.shared.SQLContextProfile;

@RunWith(AndroidJUnit4.class)
public class SQLContextTest {

    private SQLContextProfile sqlContext;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sqlContext = new SQLContextProfile(appContext);
    }

    @Test
    public void getCreateQuery() {
        Profile profile = new Profile();
        String sql = sqlContext.getCreateQuery(profile);
        Assert.assertEquals(sql, "CREATE TABLE IF NOT EXISTS cd_profiles (B_MALE INTEGER, D_DATE TEXT, ID INTEGER64 PRIMARY KEY, N_AGE INTEGER, C_NAME TEXT, N_SUM REAL, N_YEAR INTEGER);");
    }

    @Test
    public void isExistsQuery() {
        Profile profile = new Profile();
        String sql = sqlContext.isExistsQuery(profile);
        Assert.assertEquals(sql, "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='cd_profiles';");
    }

    @Test
    public void select() {
        List<Profile> profiles = new ArrayList<>();
        Profile profile = new Profile();
        profile.b_male = true;
        profile.n_age = null;
        profile.name = "Шурик";
        profile.d_date = new Date();
        profiles.add(profile);

        sqlContext.insertMany(profiles.toArray());
        Collection<Profile> results = sqlContext.select("select * from cd_profiles;", null, Profile.class);
        Assert.assertEquals(1, results.stream().count());

        Profile resultItem = results.toArray(new Profile[0])[0];

        Assert.assertEquals(profile.name, resultItem.name);
        profile.name = "саша";
        profiles.clear();
        profiles.add(profile);
        sqlContext.insertMany(profiles.toArray());

        results = sqlContext.select("select * from cd_profiles;", null, Profile.class);
        Assert.assertEquals(1, results.stream().count());

        resultItem = results.toArray(new Profile[0])[0];

        Assert.assertEquals(profile.name, resultItem.name);

        Long count = sqlContext.count("delete from cd_profiles;");
        Assert.assertNull(count);
    }

    @Test
    public void insertMany() {
        List<Profile> profiles = new ArrayList<>();
        Profile profile = new Profile();
        profile.b_male = true;
        profile.n_age = null;
        profile.name = "Шурик";
        profile.d_date = new Date();
        profiles.add(profile);

        sqlContext.insertMany(profiles.toArray());

        Long count = sqlContext.count("select count(*) from cd_profiles;");
        Assert.assertNotNull(count);
        Assert.assertEquals(1, (long) count);
    }

    @Test
    public void count() {
        Long count = sqlContext.count("select count(*) from cd_profiles;");
        Assert.assertNotNull(count);
        Assert.assertTrue(count >= 0);

        count = sqlContext.count("select count(*) from Test;");
        Assert.assertNull(count);
    }

    @Test
    public void exists() {
        Assert.assertTrue(sqlContext.exists(new Profile()));
        Assert.assertFalse(sqlContext.exists(new TestClass()));
    }

    @After
    public void tearDown() {
        sqlContext.trash();
    }

    static class TestClass {

    }
}
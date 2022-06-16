package com.mobwal.android.library.authorization;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;

public class BasicSingletonAuthorizationTest
    implements AuthorizationListeners {

    private BasicAuthorizationSingleton mAuthorization;
    private final String mBaseClaims = "user";
    private final String DEFAULT_USER_NAME = "user";
    private final String DEFAULT_USER_PASSWORD = "password";

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mAuthorization = BasicAuthorizationSingleton.createInstance(appContext, mBaseClaims, this);
        PrefManager prefManager = new PrefManager(appContext);
        prefManager.put("login", DEFAULT_USER_NAME);
    }

    @After
    public void tearDown() {
        mAuthorization.destroy();
        assertFalse(mAuthorization.isAuthorized());
    }

    @Test
    public void isAutoSignIn() {
        mAuthorization.setUser(new BasicUser(new BasicCredential(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD), 1, ".user.inspector.", ""));
        assertTrue(mAuthorization.isAuthorized());

        mAuthorization.setUser(new BasicUser(new BasicCredential("temp", "temp0"), 1, ".user.inspector.", ""));
        assertNotNull(mAuthorization.getLastAuthUser());
        mAuthorization.reset();

        assertFalse(mAuthorization.isAuthorized());
    }

    @Test
    public void isUser() {
        mAuthorization.setUser(new BasicUser(new BasicCredential(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD), 1, ".user.master.", ""));
        assertTrue(mAuthorization.isUser());

        mAuthorization.setUser(new BasicUser(new BasicCredential(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD), 1, ".admin.manager.", ""));
        assertFalse(mAuthorization.isUser());
    }

    @Test
    public void getLastAuthUser() {
        mAuthorization.setUser(new BasicUser(new BasicCredential(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD), 1, ".user.inspector.", ""));
        BasicUser basicUser = mAuthorization.getLastAuthUser();
        assertEquals(basicUser.getCredential().login, DEFAULT_USER_NAME);
    }

    @Override
    public AuthorizationMeta authorization(@NonNull Context context, @NonNull String login, @NonNull String password) {
        return null;
    }

    @Override
    public AuthorizationMeta convertResponseToMeta(@NonNull Context context, @NonNull String response, int code) {
        return null;
    }
}
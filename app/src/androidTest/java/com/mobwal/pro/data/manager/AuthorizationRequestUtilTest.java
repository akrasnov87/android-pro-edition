/*package ru.mobnius.cic.data.manager;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import ru.mobnius.core.data.authorization.AuthorizationMeta;
import ru.mobnius.core.data.authorization.AuthorizationRequestUtil;
import ru.mobnius.cic.ManagerGenerate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class AuthorizationRequestUtilTest {
    @Test
    public void requestTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AuthorizationRequestUtil util = new AuthorizationRequestUtil();
        AuthorizationMeta meta = util.request(context, ManagerGenerate.getCredentials().login, ManagerGenerate.getCredentials().password);
        assertTrue(meta.isSuccess());
        meta = util.request(context, "inspector", "inspector");
        assertFalse(meta.isSuccess());
    }
}*/
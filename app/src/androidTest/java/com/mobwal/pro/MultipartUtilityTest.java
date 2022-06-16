package com.mobwal.pro;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.rpc.RPCResult;
import com.mobwal.android.library.util.PackageCreateUtils;
import com.mobwal.android.library.util.PackageReadUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.android.library.util.ReflectionUtil;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Result;

@RunWith(AndroidJUnit4.class)
public class MultipartUtilityTest extends DbGenerate {
    private final String URL_PART = "/synchronization/v0";
    private BasicCredential basicCredentials;

    @Before
    public void setUp() {
        basicCredentials = getCredentials();
    }

    @Test
    public void successTest() throws IOException {
        PackageCreateUtils packageCreateUtils = new PackageCreateUtils(true);
        byte[] resultBytes = packageCreateUtils.addFrom(new RPCItem("shell.getServerTime", null)).generatePackage(UUID.randomUUID().toString());

        byte[] outputResultBytes;
        try {
            MultipartUtility multipartUtility = new MultipartUtility(DbGenerate.getBaseUrl() + URL_PART, basicCredentials);
            multipartUtility.addFilePart("synchronization", resultBytes);
            outputResultBytes = multipartUtility.finish();
            PackageReadUtils packageReadUtils = new PackageReadUtils(outputResultBytes, true);

            RPCResult[] to = packageReadUtils.getToResult();
            assertEquals(to[0].result.records[0].get("message").getAsString(), "Hello");
            multipartUtility.destroy();
            packageReadUtils.destroy();
        }catch (Exception e) {
            fail();
        }

        packageCreateUtils.destroy();
    }

    @Test
    public void serverErrorTest() throws IOException {
        // обработка ошибки сервера
        PackageCreateUtils packageCreateUtils = new PackageCreateUtils(false);
        byte[] resultBytes = packageCreateUtils.addFrom(new RPCItem("server.error", null)).generatePackage(UUID.randomUUID().toString());
        MultipartUtility multipartUtility = null;
        try {
            multipartUtility = new MultipartUtility(DbGenerate.getBaseUrl() + URL_PART, basicCredentials);
            multipartUtility.addFilePart("synchronization", resultBytes);
            multipartUtility.finish();

            fail();
        }catch (Exception e){
            assertEquals("Статус код: 500. testing", e.getMessage());
        }
        assert multipartUtility != null;
        multipartUtility.destroy();
        packageCreateUtils.destroy();
    }

    @After
    public void tearDown() {
        destroy();
    }
}
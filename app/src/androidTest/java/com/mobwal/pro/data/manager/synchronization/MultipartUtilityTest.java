package com.mobwal.pro.data.manager.synchronization;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.utils.PackageCreateUtils;
import ru.mobnius.core.utils.PackageReadUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mobwal.pro.data.DbGenerate;
import com.mobwal.pro.data.MultipartUtility;

@RunWith(AndroidJUnit4.class)
public class MultipartUtilityTest {
    private final String URL_PART = "/synchronization/v0";
    private final BasicCredentials basicCredentials;

    public MultipartUtilityTest(){
        basicCredentials = new BasicCredentials("test", "1234");
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
}
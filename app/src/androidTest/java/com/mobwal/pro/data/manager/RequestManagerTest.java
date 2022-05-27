/*package ru.mobnius.cic.data.manager;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.RequestManager;
import ru.mobnius.core.data.configuration.ConfigurationSettingUtil;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.rpc.FilterItem;
import ru.mobnius.core.data.rpc.QueryData;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.rpc.SingleItemQuery;
import ru.mobnius.core.data.rpc.SortItem;

import static org.junit.Assert.*;

public class RequestManagerTest {
    private BasicCredentials basicCredentials;

    @Before
    public void setUp() {
        basicCredentials = new BasicCredentials("inspector", "inspector0");
    }

    @Test
    public void rpc() throws IOException {
        String[] params = new String[1];
        params[0] = "MBL";

        RPCResult[] results = RequestManager.rpc(GlobalSettings.getConnectUrl(), basicCredentials.getToken(), ConfigurationSettingUtil.ACTION, ConfigurationSettingUtil.METHOD, new SingleItemQuery(params));

        assertNotNull(results);
        assertTrue(results[0].isSuccess());
    }

    @Test
    public void rpcQuery() throws IOException {
        QueryData queryData = new QueryData();
        queryData.filter = new Object[3];
        queryData.filter[0] = new FilterItem("c_key", "ignore_test12");
        queryData.filter[1] = QueryData.FILTER_OR;
        queryData.filter[2] = new FilterItem("c_key", "ignore_test22");

        queryData.sort = new SortItem[1];
        queryData.sort[0] = new SortItem("c_key");

        RPCResult[] results = RequestManager.rpc(GlobalSettings.getConnectUrl(),
                basicCredentials.getToken(),
                "cd_settings",
                "Query",
                queryData);

        assertNotNull(results);
        assertTrue(results[0].isSuccess());
        assertEquals(results[0].result.total, 0);
    }

    @Test
    public void exists() throws IOException {
        HashMap<String, String> hashMap = RequestManager.exists(GlobalSettings.getConnectUrl());
        assertNotNull(hashMap);
        assertNotNull(hashMap.get(RequestManager.KEY_VERSION));
        assertNotNull(hashMap.get(RequestManager.KEY_DB_VERSION));
        assertNotNull(hashMap.get(RequestManager.KEY_IP));
    }
}*/
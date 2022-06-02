package com.mobwal.pro.data.meta;

import com.google.gson.Gson;

/*import org.junit.Test;

import com.mobwal.android.library.data.rpc.FilterItem;
import com.mobwal.android.library.data.rpc.RPCItem;

import static org.junit.Assert.assertEquals;

public class TableQueryTest {

    @Test
    public void toRPCQuery() {
        TableQuery tableQuery = new TableQuery("cd_settings", null, "");
        FilterItem[] filterItems = new FilterItem[1];
        filterItems[0] = new FilterItem("name", "my");
        RPCItem rpcItem = tableQuery.toRPCQuery(1, filterItems);
        rpcItem.tid = 0;
        String jsonString = toJsonString(rpcItem);
        assertEquals(jsonString, "{\"action\":\"cd_settings\",\"data\":[{\"filter\":[{\"operator\":\"\\u003d\",\"property\":\"name\",\"value\":\"my\"}],\"limit\":1,\"page\":1,\"query\":\"\",\"select\":\"\",\"start\":0}],\"method\":\"Query\",\"tid\":0,\"type\":\"rpc\"}");

        tableQuery = new TableQuery("cd_settings", null, "users", "name,tid");
        rpcItem = tableQuery.toRPCQuery(1, null);
        rpcItem.tid = 0;
        jsonString = toJsonString(rpcItem);
        assertEquals(jsonString, "{\"action\":\"cd_settings\",\"data\":[{\"alias\":\"users\",\"limit\":1,\"page\":1,\"query\":\"\",\"select\":\"name,tid\",\"start\":0}],\"method\":\"Query\",\"tid\":0,\"type\":\"rpc\"}");
    }

    @Test
    public void toRPCSelect() {
        TableQuery tableQuery = new TableQuery("cd_settings", null, "");
        RPCItem rpcItem = tableQuery.toRPCSelect(new MyObject("test"));
        rpcItem.tid = 0;
        String jsonString = toJsonString(rpcItem);
        assertEquals(jsonString, "{\"action\":\"cd_settings\",\"data\":[{\"limit\":10000,\"params\":[{\"name\":\"test\"}]}],\"method\":\"Select\",\"tid\":0,\"type\":\"rpc\"}");
    }

    private String toJsonString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    static class MyObject {
        public String name;
        public MyObject(String name) {
            this.name = name;
        }
    }
}*/
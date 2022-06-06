package com.mobwal.android.library.data.packager;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.rpc.SingleItemQuery;
import com.mobwal.android.library.data.sync.BaseSynchronization;

@RunWith(AndroidJUnit4.class)
public class StringBlockTest {

    @Test
    public void toJsonString(){
        RPCItem[] to = new RPCItem[2];
        to[0] = new RPCItem("shell.getServerTime", null);
        to[1] = new RPCItem("setting.getMobileSettings", null);

        RPCItem[] from = new RPCItem[1];
        SingleItemQuery singleItemQuery = new SingleItemQuery(BaseSynchronization.MAX_COUNT_IN_QUERY);

        from[0] = new RPCItem("shell.getServerTime", singleItemQuery);

        StringBlock block = new StringBlock(to, new RPCItem[0]);
        String str = block.toJsonString();
        assertEquals(str, "{\"from\":[],\"to\":[{\"action\":\"shell\",\"change\":null,\"data\":[[null]],\"method\":\"getServerTime\",\"schema\":null,\"tid\":"+to[0].tid +",\"type\":\"rpc\"},{\"action\":\"setting\",\"change\":null,\"data\":[[null]],\"method\":\"getMobileSettings\",\"schema\":null,\"tid\":"+to[1].tid+",\"type\":\"rpc\"}]}");

        block = new StringBlock(to, from);
        str = block.toJsonString();
        assertEquals(str, "{\"from\":[{\"action\":\"shell\",\"change\":null,\"data\":[[{\"filter\":null,\"limit\":" + BaseSynchronization.MAX_COUNT_IN_QUERY + ",\"params\":[]}]],\"method\":\"getServerTime\",\"schema\":null,\"tid\":"+from[0].tid+",\"type\":\"rpc\"}],\"to\":[{\"action\":\"shell\",\"change\":null,\"data\":[[null]],\"method\":\"getServerTime\",\"schema\":null,\"tid\":"+to[0].tid+",\"type\":\"rpc\"},{\"action\":\"setting\",\"change\":null,\"data\":[[null]],\"method\":\"getMobileSettings\",\"schema\":null,\"tid\":"+to[1].tid+",\"type\":\"rpc\"}]}");
    }
}

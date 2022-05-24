package ru.mobnius.core.utils;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.packager.FileBinary;
import ru.mobnius.core.data.packager.MetaPackage;
import ru.mobnius.core.data.packager.MetaSize;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.data.rpc.SingleItemQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PackageUtilUtilsTest {
    private boolean isZip = false;
    @Test
    public void lifeCycleTest() throws Exception {
        PackageCreateUtils packageCreateUtils = new PackageCreateUtils(isZip);
        packageCreateUtils.addFile("file1", "file1", "this is file number 1".getBytes()).addFile("file2", "file2", "this is file number 2".getBytes());
        packageCreateUtils.addFile("file3", "file3", "this is file number 3".getBytes());

        RPCItem item1 = new RPCItem("shell.getServerTime", null);
        Info info = new Info();
        info.name = "Test";
        SingleItemQuery query = new SingleItemQuery(info);

        RPCItem item2 = new RPCItem("shell.getItems", query);

        RPCItem fromItem1 = new RPCItem("users.Query", null);

        byte[] bytes = packageCreateUtils.addTo(item1).addTo(item2).addFrom(fromItem1).generatePackage(UUID.randomUUID().toString());

        PackageReadUtils packageReadUtils = new PackageReadUtils(bytes, isZip);
        MetaSize metaSize = packageReadUtils.getMetaSize();
        //assertEquals(metaSize.metaSize, 265);
        assertEquals(metaSize.status, 0);

        MetaPackage metaPackage = packageReadUtils.getMeta();
        assertTrue(!metaPackage.id.isEmpty());
        //assertEquals(metaPackage.stringSize, 285);
        assertEquals(metaPackage.binarySize, 63);
        assertEquals(metaPackage.attachments.length, 3);
        assertEquals(metaPackage.attachments[1].name, "file2");
        assertEquals(metaPackage.attachments[1].size, 21);

        FileBinary[] fileBinaries = packageReadUtils.getFiles();

        assertEquals(fileBinaries.length, 3);
        assertEquals(new String(fileBinaries[2].bytes), "this is file number 3");

        RPCItem[] to = packageReadUtils.getTo();
        assertEquals(to.length, 2);
        assertEquals(to[0].method, "getServerTime");

        RPCItem[] from = packageReadUtils.getFrom();
        assertEquals(from.length, 1);
        assertEquals(from[0].method, "Query");

        packageReadUtils.destroy();
        packageCreateUtils.destroy();
    }

    static class Info{
        String name;
    }
}

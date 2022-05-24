package ru.mobnius.core.utils.v2;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.mobnius.core.data.packager.FileBinary;
import ru.mobnius.core.data.packager.MetaPackage;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.data.rpc.SingleItemQuery;
import ru.mobnius.core.utils.PackageCreateUtils;
import ru.mobnius.core.utils.PackageReadUtils;

@RunWith(AndroidJUnit4.class)
public class PackageUtilUtilsTest {
    @Test
    public void lifeCycleTest() throws Exception {
        PackageCreateUtils packageCreateUtils = new PackageCreateUtils(false);
        packageCreateUtils.addFile("file1", "file1", "this is file number 1".getBytes()).addFile("file2", "file2", "this is file number 2".getBytes());
        packageCreateUtils.addFile("file3", "file3", "this is file number 3".getBytes());
        RPCItem item1 = new RPCItem("shell.getServerTime", null);
        Info info = new Info();
        info.name = "Test";
        RPCItem item2 = new RPCItem("shell.getItems", new SingleItemQuery(info));
        PackageReadUtils packageReadUtils = new PackageReadUtils(packageCreateUtils.addTo(item1).addTo(item2).addFrom(new RPCItem("users.Query", null)).generatePackage(UUID.randomUUID().toString()), false);
        Assert.assertEquals(packageReadUtils.getMetaSize().status, 0);
        MetaPackage metaPackage = packageReadUtils.getMeta();

        Assert.assertFalse(metaPackage.id.isEmpty());

        Assert.assertEquals(metaPackage.binarySize, 63);
        Assert.assertEquals(metaPackage.attachments.length, 3);
        Assert.assertEquals(metaPackage.attachments[1].name, "file2");
        Assert.assertEquals(metaPackage.attachments[1].size, 21);
        FileBinary[] fileBinaries = packageReadUtils.getFiles();
        Assert.assertEquals(fileBinaries.length, 3);
        Assert.assertEquals(new String(fileBinaries[2].bytes), "this is file number 3");
        RPCItem[] to = packageReadUtils.getTo();

        Assert.assertEquals(to.length, 2);
        Assert.assertEquals(to[0].method, "getServerTime");
        RPCItem[] from = packageReadUtils.getFrom();
        Assert.assertEquals(from.length, 1);
        Assert.assertEquals(from[0].method, "Query");
        packageReadUtils.destroy();
        packageCreateUtils.destroy();
    }

    static class Info {
        String name;

        Info() {

        }
    }
}

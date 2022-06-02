package com.mobwal.android.library.data.packager.v2;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mobwal.android.library.data.packager.FileBinary;
import com.mobwal.android.library.data.packager.MetaPackage;
import com.mobwal.android.library.data.packager.MetaSize;
import com.mobwal.android.library.data.packager.PackageUtil;
import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.zip.ZipManager;
import com.mobwal.android.library.util.PackageCreateUtils;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PackageUtilTest {
    @Test
    public void getString() throws IOException {
        Assert.assertEquals("HelloWorld!!!", PackageUtil.getString(ZipManager.compress("HelloWorld!!!").getCompress(), true));
        Assert.assertEquals("HelloWorld!!!", PackageUtil.getString("HelloWorld!!!".getBytes(), true));
    }

    @Test
    public void readMetaTest() throws Exception {
        MetaSize ms = new MetaSize("{\"attachments\":[{\"key\":\"name\",\"name\":\"name.jpg\",\"size\":100}],\"binarySize\":0,\"bufferBlockFromLength\":1,\"bufferBlockToLength\":1,\"dataInfo\":\"full\",\"id\":\"\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}".length(), 3, "NML");
        MetaPackage mp = PackageUtil.readMeta((ms.toJsonString() + "{\"attachments\":[{\"key\":\"name\",\"name\":\"name.jpg\",\"size\":100}],\"binarySize\":0,\"bufferBlockFromLength\":1,\"bufferBlockToLength\":1,\"dataInfo\":\"full\",\"id\":\"\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}").getBytes(), false);
        Assert.assertTrue(mp.transaction);
        Assert.assertEquals(mp.attachments.length, 1);
        Assert.assertEquals(mp.binarySize, 0);
        Assert.assertEquals(mp.stringSize, 100);
        Assert.assertEquals(mp.version, "1.1");
        Assert.assertEquals(mp.dataInfo, "full");
        Assert.assertEquals(mp.bufferBlockToLength, 1);
        Assert.assertEquals(mp.bufferBlockFromLength, 1);
    }

    @Test
    public void readBlockTest() throws Exception {
        PackageCreateUtils createUtils = new PackageCreateUtils(false);
        createUtils.addFile("file1", "file1", "Hello World!!!".getBytes());
        createUtils.addFile("file2", "file2", "Hello World 2!!!".getBytes());
        createUtils.addFile("file3", "file3", "Hello World 3!!!".getBytes());
        createUtils.addTo(new RPCItem("shell.getServerTime", null));
        byte[] bytes = createUtils.generatePackage("", true);
        RPCItem item = PackageUtil.readMapItem(bytes, 0, false);
        Assert.assertEquals(item.action, "shell");
        Assert.assertEquals(item.method, "getServerTime");
        FileBinary[] fileBinaries = PackageUtil.readBinaryBlock(bytes, false).getFiles();
        Assert.assertEquals(fileBinaries.length, 3);
        Assert.assertEquals(fileBinaries[0].name, "file1");
        Assert.assertEquals(new String(fileBinaries[1].bytes), "Hello World 2!!!");
    }
}

package com.mobwal.android.library.data.packager.v2;

import com.mobwal.android.library.data.packager.MetaAttachment;
import com.mobwal.android.library.data.packager.MetaPackage;

import org.junit.Assert;
import org.junit.Test;

public class MetaPackageTest {
    @Test
    public void toJsonString() {
        MetaAttachment[] metaAttachments = {new MetaAttachment(100, "name.jpg", "c_name")};
        MetaPackage meta = new MetaPackage("");
        meta.stringSize = 100;
        meta.binarySize = 0;
        meta.attachments = metaAttachments;
        meta.dataInfo = "full";
        meta.transaction = true;
        meta.version = "1.1";
        meta.bufferBlockFromLength = 1;
        meta.bufferBlockToLength = 1;
        Assert.assertEquals(meta.toJsonString(), "{\"attachments\":[{\"key\":\"c_name\",\"name\":\"name.jpg\",\"size\":100}],\"binarySize\":0,\"bufferBlockFromLength\":1,\"bufferBlockToLength\":1,\"dataInfo\":\"full\",\"id\":\"\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}");
    }
}
package com.mobwal.android.library.data.packager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BinaryBlockTest {

    @Test
    public void toBytesTest(){
        BinaryBlock block = new BinaryBlock();
        block.add("file1", "file1", "Hello World!!!".getBytes());
        block.add("file2", "file2", "Hello World 2!!!".getBytes());
        block.add("file3", "file3", "Hello World 3!!!".getBytes());

        byte[] bytes = block.toBytes();
        assertEquals(bytes.length, 46);

        MetaAttachment[] attachments = block.getAttachments();
        assertEquals(attachments.length, 3);
        assertEquals(attachments[1].key, "file2");

        block.clear();
        MetaAttachment[] metaAttachments = block.getAttachments();
        assertEquals(0, metaAttachments.length);
        assertEquals(0, block.getFiles().length);
        assertEquals(0, block.toBytes().length);
    }
}

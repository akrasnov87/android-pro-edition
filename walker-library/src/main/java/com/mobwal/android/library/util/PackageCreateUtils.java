package com.mobwal.android.library.util;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobwal.android.library.data.packager.BinaryBlock;
import com.mobwal.android.library.data.packager.MetaPackage;
import com.mobwal.android.library.data.packager.MetaSize;
import com.mobwal.android.library.data.packager.StringMapItem;
import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.zip.ZipManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageCreateUtils {
    private BinaryBlock mBinaryBlock;
    private ArrayList<RPCItem> mFrom;
    private final boolean mIsZip;
    private ArrayList<RPCItem> mTo;

    public PackageCreateUtils(boolean isZip) {
        mIsZip = isZip;
        mFrom = new ArrayList<>();
        mTo = new ArrayList<>();
        mBinaryBlock = new BinaryBlock();
    }

    public void setBinaryBlock(@NonNull BinaryBlock block) {
        mBinaryBlock = block;
    }

    public PackageCreateUtils addTo(@NonNull RPCItem to) {
        mTo.add(to);
        return this;
    }

    public PackageCreateUtils addAllTo(@NonNull RPCItem[] to) {
        mTo.addAll(Arrays.asList(to));
        return this;
    }

    public PackageCreateUtils addFrom(@NonNull RPCItem from) {
        mFrom.add(from);
        return this;
    }

    public PackageCreateUtils addAllFrom(@NonNull RPCItem[] from) {
        mFrom.addAll(Arrays.asList(from));
        return this;
    }

    public PackageCreateUtils addFile(@NonNull String name, @NonNull String key, byte[] bytes) {
        mBinaryBlock.add(name, key, bytes);
        return this;
    }

    public byte[] generatePackage(@NonNull String tid, boolean transaction) throws IOException {
        int bufferBlockToLength = 0;
        int bufferBlockFromLength = 0;

        byte[] mBytes;
        byte[] fromBuffer;
        byte[] toBuffer;

        Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        ArrayList<StringMapItem> stringMapArray = new ArrayList<>();

        List<byte[]> bufferBlockTo = new ArrayList<>(mTo.size());

        int idx = 0;
        for (RPCItem rpcItem : mTo) {
            String str = json.toJson(rpcItem);
            toBuffer = mIsZip ? ZipManager.compress(str).getCompress() : str.getBytes();

            bufferBlockTo.add(toBuffer);
            bufferBlockToLength += toBuffer.length;
            stringMapArray.add(new StringMapItem("to" + idx, toBuffer.length));
            idx++;
        }

        List<byte[]> bufferBlockFrom = new ArrayList<>(mFrom.size());

        idx = 0;
        for (RPCItem rpcItem : this.mFrom) {
            String str = json.toJson(rpcItem);
            fromBuffer = mIsZip ? ZipManager.compress(str).getCompress() : str.getBytes();
            bufferBlockFrom.add(fromBuffer);
            bufferBlockFromLength += fromBuffer.length;
            stringMapArray.add(new StringMapItem("from" + idx, fromBuffer.length));
            idx++;
        }

        String stringMap = json.toJson(stringMapArray);
        byte[] stringMapBytes = mIsZip ? ZipManager.compress(stringMap).getCompress() : stringMap.getBytes();

        byte[] binaryBlockBytes = mBinaryBlock.toBytes();

        MetaPackage meta = new MetaPackage(tid);
        meta.stringSize = stringMapBytes.length;
        meta.binarySize = binaryBlockBytes.length;
        meta.attachments = mBinaryBlock.getAttachments();
        meta.dataInfo = "";
        meta.transaction = transaction;
        meta.version = "v2";
        meta.bufferBlockToLength = bufferBlockToLength;
        meta.bufferBlockFromLength = bufferBlockFromLength;
        mBytes = mIsZip ? ZipManager.compress(meta.toJsonString()).getCompress() : meta.toJsonString().getBytes();

        MetaSize ms = new MetaSize(mBytes.length, 0, mIsZip ? ZipManager.getMode() : "NML");
        byte[] metaBytes = ms.toJsonString().getBytes();
        ByteBuffer buff = ByteBuffer.wrap(new byte[(metaBytes.length + mBytes.length + stringMapBytes.length + bufferBlockFromLength + bufferBlockToLength + binaryBlockBytes.length)]);
        buff.put(metaBytes);
        buff.put(mBytes);
        buff.put(stringMapBytes);

        for (byte[] b : bufferBlockTo) {
            buff.put(b);
        }

        for (byte[] bytes : bufferBlockFrom) {
            buff.put(bytes);
        }
        buff.put(binaryBlockBytes);
        return buff.array();
    }

    public byte[] generatePackage(@NonNull String tid) throws IOException {
        return generatePackage(tid, true);
    }

    public void destroy() {
        mTo.clear();
        mTo = null;
        mFrom.clear();
        mFrom = null;
        mBinaryBlock.clear();
        mBinaryBlock = null;
    }
}

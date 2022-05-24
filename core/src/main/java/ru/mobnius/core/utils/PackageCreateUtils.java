package ru.mobnius.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.packager.BinaryBlock;
import ru.mobnius.core.data.packager.MetaPackage;
import ru.mobnius.core.data.packager.MetaSize;
import ru.mobnius.core.data.packager.StringMapItem;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.data.zip.ZipManager;

public class PackageCreateUtils {
    private BinaryBlock mBinaryBlock;
    private ArrayList<RPCItem> mFrom;
    private boolean mIsZip;
    private ArrayList<RPCItem> mTo;

    public PackageCreateUtils(boolean isZip) {
        mIsZip = isZip;
        mFrom = new ArrayList<>();
        mTo = new ArrayList<>();
        mBinaryBlock = new BinaryBlock();
    }

    public void setBinaryBlock(BinaryBlock block) {
        mBinaryBlock = block;
    }

    public PackageCreateUtils addTo(RPCItem to) {
        mTo.add(to);
        return this;
    }

    public PackageCreateUtils addAllTo(RPCItem[] to) {
        mTo.addAll(Arrays.asList(to));
        return this;
    }

    public PackageCreateUtils addFrom(RPCItem from) {
        mFrom.add(from);
        return this;
    }

    public PackageCreateUtils addAllFrom(RPCItem[] from) {
        mFrom.addAll(Arrays.asList(from));
        return this;
    }

    public PackageCreateUtils addFile(String name, String key, byte[] bytes) {
        mBinaryBlock.add(name, key, bytes);
        return this;
    }

    public byte[] generatePackage(String tid, boolean transaction) throws IOException {
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
        meta.version = PreferencesManager.SYNC_PROTOCOL_v2;
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

    public byte[] generatePackage(String tid) throws IOException {
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

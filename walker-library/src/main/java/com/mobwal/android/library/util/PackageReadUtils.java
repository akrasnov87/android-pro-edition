package com.mobwal.android.library.util;

import androidx.annotation.NonNull;

import com.mobwal.android.library.data.packager.FileBinary;
import com.mobwal.android.library.data.packager.MetaPackage;
import com.mobwal.android.library.data.packager.MetaSize;
import com.mobwal.android.library.data.packager.PackageUtil;
import com.mobwal.android.library.data.packager.StringMapItem;
import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.rpc.RPCResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageReadUtils {
    private byte[] mAll;
    private final boolean mIsZip;

    public PackageReadUtils(@NonNull byte[] bytes, boolean isZip) {
        mAll = bytes;
        mIsZip = isZip;
    }

    public int getLength() {
        return mAll.length;
    }

    public MetaSize getMetaSize() throws Exception {
        return PackageUtil.readSize(mAll);
    }

    public MetaPackage getMeta() throws Exception {
        return PackageUtil.readMeta(mAll, mIsZip);
    }

    public FileBinary[] getFiles() throws Exception {
        return PackageUtil.readBinaryBlock(this.mAll, this.mIsZip).getFiles();
    }

    public FileBinary getFile(@NonNull String name) throws Exception {
        for (FileBinary file : getFiles()) {
            if (file.name.equals(name)) {
                return file;
            }
        }
        return null;
    }

    public RPCItem[] getTo() throws Exception {
        List<RPCItem> rpcItems = new ArrayList<>();
        List<StringMapItem> mapItems = PackageUtil.readMap(mAll, mIsZip);
        int idx = 0;
        for(StringMapItem mapItem : mapItems) {
            if(mapItem.name.startsWith("to")) {
                rpcItems.add(PackageUtil.readMapItem(mAll, idx, mIsZip));
            }
            idx++;
        }
        return rpcItems.toArray(new RPCItem[0]);
    }

    public RPCResult[] getToResult() throws Exception {
        List<RPCResult> rpcItems = new ArrayList<>();
        List<StringMapItem> mapItems = PackageUtil.readMap(mAll, mIsZip);
        int idx = 0;
        for(StringMapItem mapItem : mapItems) {
            if(mapItem.name.startsWith("to")) {
                rpcItems.addAll(Arrays.asList(PackageUtil.readMapItemResult(mAll, idx, mIsZip)));
            }
            idx++;
        }
        return rpcItems.toArray(new RPCResult[0]);
    }

    public RPCItem[] getFrom() throws Exception {
        List<RPCItem> rpcItems = new ArrayList<>();
        List<StringMapItem> mapItems = PackageUtil.readMap(mAll, mIsZip);
        int idx = 0;
        for(StringMapItem mapItem : mapItems) {
            if(mapItem.name.startsWith("from")) {
                rpcItems.add(PackageUtil.readMapItem(mAll, idx, mIsZip));
            }
            idx++;
        }
        return rpcItems.toArray(new RPCItem[0]);
    }

    public RPCResult[] getFromResult() throws Exception {
        List<RPCResult> rpcItems = new ArrayList<>();
        List<StringMapItem> mapItems = PackageUtil.readMap(mAll, mIsZip);
        int idx = 0;
        for (StringMapItem mapItem : mapItems) {
            if (mapItem.name.startsWith("from")) {
                rpcItems.addAll(Arrays.asList(PackageUtil.readMapItemResult(mAll, idx, mIsZip)));
            }
            idx++;
        }

        return rpcItems.toArray(new RPCResult[0]);
    }

    public void destroy() {
        mAll = null;
    }
}

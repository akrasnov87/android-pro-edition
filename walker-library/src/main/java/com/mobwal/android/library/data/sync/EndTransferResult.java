package com.mobwal.android.library.data.sync;

import com.mobwal.android.library.data.sync.util.transfer.Transfer;

public class EndTransferResult {
    public EndTransferResult(String tid, Transfer transfer, Object object) {
        mTid = tid;
        mObject = object;
        mTransfer = transfer;
    }
    public String mTid;
    public Transfer mTransfer;
    public Object mObject;
}

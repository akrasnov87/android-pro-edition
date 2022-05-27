package com.mobwal.pro.data;

import com.mobwal.pro.data.utils.transfer.Transfer;

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

package ru.mobnius.core.data.synchronization;

import ru.mobnius.core.data.synchronization.utils.transfer.Transfer;

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

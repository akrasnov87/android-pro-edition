package ru.mobnius.core.data;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OnCallbackListenerTest {

    @Test
    public void callbackTest() {
        MyTestManager manager = new MyTestManager();
        manager.onCall(new OnCallbackListener() {
            @Override
            public void onResult(Meta meta) {
                assertTrue(meta.isSuccess());
            }
        });
    }

    private static class MyTestManager {
        void onCall(final OnCallbackListener callback) {
            Meta meta = new Meta(Meta.OK, "");
            callback.onResult(meta);
        }
    }
}
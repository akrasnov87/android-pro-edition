package ru.mobnius.core.ui;

import android.text.Editable;
import android.util.Log;

public class StringTextWatcher implements OnStringTextWatcher {
    private OnStringTextWatcher mTextWatcher;
    private String mId;
    private String mPrevValue;

    public StringTextWatcher(OnStringTextWatcher textWatcher, String id) {
        mTextWatcher = textWatcher;
        mId = id;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mPrevValue = s.toString();
        mTextWatcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTextWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        String TAG = "CHANGE_STRING";
        Log.d(TAG, String.format("%s: %s->%s", mId, mPrevValue, value));
        afterStringTextChanged(mId, mPrevValue, value);

        mTextWatcher.afterTextChanged(s);
    }

    @Override
    public void afterStringTextChanged(String id, String prevValue, String value) {
        mTextWatcher.afterStringTextChanged(id, prevValue, value);
    }
}

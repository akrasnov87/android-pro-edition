package com.mobwal.android.library;

import com.mobwal.android.library.data.Meta;

public interface OnResponseListeners {
    /**
     * результат обработки обратного вызова
     * @param meta результат
     */
    void onResponseResult(Meta meta);
}

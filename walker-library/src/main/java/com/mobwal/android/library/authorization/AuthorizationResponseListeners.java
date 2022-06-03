package com.mobwal.android.library.authorization;

import android.app.Activity;

public interface AuthorizationResponseListeners {
    /**
     * результат обработки обратного вызова
     * @param activity экрана
     * @param meta результат
     */
    void onResponseAuthorizationResult(Activity activity, AuthorizationMeta meta);
}

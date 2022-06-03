package com.mobwal.android.library.authorization;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.mobwal.android.library.NewThread;
import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.R;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;
import com.mobwal.android.library.data.Meta;
import com.mobwal.android.library.util.ClaimsUtil;

/**
 * Авторизация
 */
public class BasicAuthorizationSingleton {

    /**
     * созданеие экземпляра класса
     *
     * @param context текущее приложение
     * @return Объект для реализации авторизации пользователя
     */
    public static BasicAuthorizationSingleton createInstance(@NonNull Context context, @NonNull String baseClaims, @NonNull AuthorizationListeners listeners) {
        if (mAuthorization != null) {
            return mAuthorization;
        } else {
            return mAuthorization = new BasicAuthorizationSingleton(context, baseClaims, listeners);
        }
    }

    private final String mBaseClaims;

    private final AuthorizationCache mAuthorizationCache;
    private final AuthorizationListeners mAuthorizationListeners;

    private NewThread mAuthThread;

    /**
     * Информация о пользователе
     */
    private BasicUser mUser;

    /**
     * Статус авторизации.
     * По умолчанию FAIL
     */
    private AuthorizationStatus mStatus = AuthorizationStatus.FAIL;

    private static BasicAuthorizationSingleton mAuthorization;
    private AuthorizationMeta mAuthorizationMeta;
    private final Context mContext;

    /**
     *
     * @param context контекст
     * @param baseClaims роль
     * @param listeners обработчик
     */
    private BasicAuthorizationSingleton(@NonNull Context context, @NonNull String baseClaims, @NonNull AuthorizationListeners listeners) {
        mContext = context;
        mAuthorizationCache = new AuthorizationCache(context);
        mBaseClaims = baseClaims;
        mAuthorizationListeners = listeners;
    }

    public static BasicAuthorizationSingleton getInstance() {
        return mAuthorization;
    }

    public AuthorizationMeta getAuthorizationMeta() {
        return mAuthorizationMeta;
    }

    /**
     * Авторизован пользователь или нет
     *
     * @return true - пользователь авторизован
     */
    public boolean isAuthorized() {
        return mStatus == AuthorizationStatus.SUCCESS;
    }

    /**
     * Обработчик авторизации
     *
     * @param login    логин
     * @param password пароль
     * @param mode     режим авторизации: AuthorizationListeners.ONLINE или AuthorizationListeners.OFFLINE
     */
    public void onSignIn(@NonNull Activity context, @NonNull String login, @NonNull String password, int mode, @NonNull AuthorizationResponseListeners listeners) {

        if (mAuthThread != null) {
            mAuthThread.destroy();
            mAuthThread = null;
        }

        if (mode == AuthorizationListeners.ONLINE) {
            mAuthThread = new NewThread(context) {
                private BasicCredential mCredentials;

                @Override
                public void onBackgroundExecute() {
                    mCredentials = new BasicCredential(login, password);

                    try {
                        mAuthorizationMeta = mAuthorizationListeners.authorization(context, mCredentials.login, mCredentials.password);
                    } catch (Exception ignore) {
                        mAuthorizationMeta = new AuthorizationMeta(Meta.ERROR_SERVER, mContext.getString(R.string.authorization_error));
                    }
                }

                @Override
                public void onPostExecute() {
                    if (!mAuthorizationMeta.isSuccess()) {
                        listeners.onResponseAuthorizationResult(context, mAuthorizationMeta);
                        reset();
                    } else {
                        BasicUser basicUser = new BasicUser(mCredentials, mAuthorizationMeta.getUserId(), mAuthorizationMeta.getClaims());
                        setUser(basicUser);

                        listeners.onResponseAuthorizationResult(context, mAuthorizationMeta);
                    }
                }
            };
            mAuthThread.run();
        } else {
            BasicUser basicUser = mAuthorizationCache.read(login);
            if (basicUser != null) {
                setUser(basicUser);
                if (basicUser.getCredential().isEqualsPassword(password)) {
                    listeners.onResponseAuthorizationResult(context, new AuthorizationMeta(
                            Meta.OK,
                            context.getString(R.string.authorization_success),
                            basicUser.getCredential().getToken(),
                            basicUser.getClaims(),
                            basicUser.getUserId()));
                } else {
                    listeners.onResponseAuthorizationResult(context, new AuthorizationMeta(Meta.NOT_AUTHORIZATION, context.getString(R.string.authorization_failed)));
                    reset();
                }
            } else {
                listeners.onResponseAuthorizationResult(context, new AuthorizationMeta(Meta.NOT_AUTHORIZATION, context.getString(R.string.authorization_network)));
                reset();
            }
        }
    }

    /**
     * обновление пользователя
     *
     * @param basicUser объект пользователя
     */
    public void setUser(BasicUser basicUser) {
        mUser = basicUser;
        mAuthorizationCache.write(basicUser);
        mStatus = AuthorizationStatus.SUCCESS;
    }

    /**
     * получение текущего авторизованного пользователя
     *
     * @return текущий пользователь
     */
    public BasicUser getUser() {
        return mUser;
    }

    /**
     * Является инспектором
     *
     * @return true - авторизованный пользователь является инспектором
     */
    public boolean isUser() {
        if (mUser != null) {
            ClaimsUtil util = new ClaimsUtil(mUser.getClaims());
            return util.isExists(mBaseClaims);
        }

        return false;
    }

    /**
     * Получение информации о последнем авторизованном пользователе
     *
     * @return пользователь
     */
    public BasicUser getLastAuthUser() {
        PrefManager prefManager = new PrefManager(mContext);
        String login = prefManager.get("login", "");

        return mAuthorizationCache.read(login);
    }

    /**
     * Получение информации о ранее авторизованном пользователе
     *
     * @param name логин пользователя
     * @return пользователь
     */
    public BasicUser getOfflineAuthUser(String name) {
        return mAuthorizationCache.read(name);
    }

    /**
     * Сброс авторизации
     */
    public void reset() {
        mUser = null;
        mAuthorizationMeta = null;
        mStatus = AuthorizationStatus.FAIL;
    }

    /**
     * Полная очистка информации об авторизации
     */
    public void destroy() {
        reset();

        if (mAuthThread != null) {
            mAuthThread.destroy();
            mAuthThread = null;
        }

        mAuthorizationCache.clear(null);
    }
}

package ru.mobnius.core.data.authorization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ru.mobnius.core.data.Meta;
import ru.mobnius.core.data.OnCallbackListener;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.ui.BaseLoginActivity;
import ru.mobnius.core.utils.ClaimsUtil;

/**
 * Авторизация
 */
public class Authorization {
    private static final String INSPECTOR_CLAIM = "inspector";

    private final AuthorizationCache mAuthorizationCache;
    private final AuthorizationRequestUtil mRequestUtil;

    private OnCallbackListener mListener;
    private AuthAsyncTask mAuthAsyncTask;

    /**
     * Авторизация при наличии интернет соединения
     */
    public static final int ONLINE = 0;

    /**
     * Авторизация при отсутствии интернета
     */
    public static final int OFFLINE = 1;

    /**
     * Информация о пользователе
     */
    private BasicUser mUser;

    /**
     * Статус авторизации.
     * По умолчанию FAIL
     */
    private AuthorizationStatus mStatus = AuthorizationStatus.FAIL;

    private static Authorization mAuthorization;
    private AuthorizationMeta mAuthorizationMeta;

    private Authorization(Context context) {
        mAuthorizationCache = new AuthorizationCache(context);
        mRequestUtil = new AuthorizationRequestUtil();
    }

    /**
     * созданеие экземпляра класса
     *
     * @param context текущее приложение
     * @return Объект для реализации авторизации пользователя
     */
    public static Authorization createInstance(Context context) {
        if (mAuthorization != null) {
            return mAuthorization;
        } else {
            return mAuthorization = new Authorization(context);
        }
    }

    public static Authorization getInstance() {
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
     * authorization.onSignIn("login", "password", Authorization.ONLINE, new ICallback() {
     * public void onResult(Meta meta) {
     * // здесь обработка
     * }
     * })
     *
     * @param login    логин
     * @param password пароль
     * @param mode     режим авторизации: Authorization.ONLINE или Authorization.OFFLINE
     * @param listener результат обратного вызова
     */
    public void onSignIn(BaseLoginActivity context, String login, String password, int mode, OnCallbackListener listener) {
        mListener = listener;

        if (mAuthAsyncTask != null) {
            mAuthAsyncTask.cancel(true);
            mAuthAsyncTask = null;
        }

        if (mode == ONLINE) {
            mAuthAsyncTask = new AuthAsyncTask(context);
            mAuthAsyncTask.execute(login, password);
        } else {
            BasicUser basicUser = mAuthorizationCache.read(login);
            if (basicUser != null) {
                setUser(basicUser);
                if (basicUser.getCredentials().password.equals(password)) {
                    listener.onResult(new AuthorizationMeta(
                            Meta.OK,
                            "Вы авторизованы",
                            basicUser.getCredentials().getToken(),
                            basicUser.claims,
                            Integer.parseInt(String.valueOf(basicUser.getUserId())),
                            ""));
                } else {
                    listener.onResult(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, "Логин или пароль введены не верно.", null, null, null, null));
                    reset();
                }
            } else {
                listener.onResult(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, "У приложения отсутствует доступ к серверу. Проверьте интернет.", null, null, null, null));
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
     * Требуется ли выполнять автоматическу авторизацию
     *
     * @return true - можно автоматически авторизовать пользователя.
     */
    public boolean isAutoSignIn() {
        return mAuthorizationCache.getNames().length == 1;
    }

    /**
     * Является инспектором
     *
     * @return true - авторизованный пользователь является инспектором
     */
    public boolean isInspector() {
        if (mUser != null) {
            ClaimsUtil util = new ClaimsUtil(mUser.claims);
            return util.isExists(INSPECTOR_CLAIM);
        }

        return false;
    }

    /**
     * Получение информации о последнем авторизованном пользователе
     *
     * @return пользователь
     */
    public BasicUser getLastAuthUser() {
        String[] names = mAuthorizationCache.getNames();
        if (names.length == 1) {
            String name = names[0];
            return mAuthorizationCache.read(name);
        }

        return null;
    }

    /**
     * Получение информации о последнем авторизованном пользователе
     *
     * @return пользователь
     */
    public BasicUser getAuthUser(String name) {
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

    public void destroy() {
        reset();
        mListener = null;
        mAuthorizationCache.clear(true);
    }

    @SuppressLint("StaticFieldLeak")
    private class AuthAsyncTask extends AsyncTask<String, Void, AuthorizationMeta> {
        private final Context mContext;
        private BasicCredentials mCredentials;
        private final WeakReference<BaseLoginActivity> loginActivityWeakRef;

        public AuthAsyncTask(BaseLoginActivity context) {
            mContext = context;
            this.loginActivityWeakRef = new WeakReference<>(context);
        }

        @Override
        protected AuthorizationMeta doInBackground(String... strings) {
            mCredentials = new BasicCredentials(strings[0], strings[1]);
            try {
                return mRequestUtil.request(mContext, mCredentials.login, mCredentials.password);
            } catch (Exception ignore) {
                return new AuthorizationMeta(Meta.ERROR_SERVER, "Ошибка авторизации");
            }
        }

        @Override
        protected void onPostExecute(AuthorizationMeta authorizationMeta) {
            super.onPostExecute(authorizationMeta);

            if (!authorizationMeta.isSuccess()) {
                reset();
            } else {
                BasicUser basicUser = new BasicUser(mCredentials, authorizationMeta.getUserId(), authorizationMeta.getClaims());
                setUser(basicUser);
            }
            if (loginActivityWeakRef.get() != null && !loginActivityWeakRef.get().isFinishing()) {
                mAuthorizationMeta = authorizationMeta;
                mListener.onResult(authorizationMeta);
            }
        }
    }
}

package ru.mobnius.core.data.authorization;

import android.app.Activity;
import android.content.Context;

import ru.mobnius.core.data.Meta;
import ru.mobnius.core.data.OnCallbackListener;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.ui.BaseLoginActivity;
import ru.mobnius.core.utils.ClaimsUtil;
import ru.mobnius.core.utils.NewThread;

/**
 * Авторизация
 */
public class Authorization {
    private static final String INSPECTOR_CLAIM = "user";

    private final AuthorizationCache mAuthorizationCache;
    private final AuthorizationRequestUtil mRequestUtil;

    private OnCallbackListener mListener;
    private NewThread mAuthThread;

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
    public void onSignIn(Activity context, String login, String password, int mode, OnCallbackListener listener) {
        mListener = listener;

        if (mAuthThread != null) {
            mAuthThread.destroy();
            mAuthThread = null;
        }

        if (mode == ONLINE) {
            mAuthThread = new NewThread(context) {
                private BasicCredentials mCredentials;

                @Override
                public void onBackgroundExecute() {
                    mCredentials = new BasicCredentials(login, password);
                    try {
                        mAuthorizationMeta = mRequestUtil.request(context, mCredentials.login, mCredentials.password);
                    } catch (Exception ignore) {
                        mAuthorizationMeta = new AuthorizationMeta(Meta.ERROR_SERVER, "Ошибка авторизации");
                    }
                }

                @Override
                public void onPostExecute() {
                    if (!mAuthorizationMeta.isSuccess()) {
                        mListener.onResult(mAuthorizationMeta);
                        reset();
                    } else {
                        BasicUser basicUser = new BasicUser(mCredentials, mAuthorizationMeta.getUserId(), mAuthorizationMeta.getClaims());
                        setUser(basicUser);

                        mListener.onResult(mAuthorizationMeta);
                    }
                }
            };
            mAuthThread.run();
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
                            Long.parseLong(String.valueOf(basicUser.getUserId())),
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
}

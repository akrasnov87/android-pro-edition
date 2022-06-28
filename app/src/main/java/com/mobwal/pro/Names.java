package com.mobwal.pro;

public class Names {
    public static final String PREFERENCE_NAME = "walker";
    public static final String LOG = "MOBILE_WALKER";
    public static final String LOG_ERROR = "MOBILE_WALKER_ERROR";
    public static final String HOME_PAGE = "https://info.mobwal.com/mobwal_pro";
    public static final String ROUTE_DOCS = "https://info.mobwal.com/ru/attachment#simple";
    public static final float SWIPE_THRESHOLD = 0.7f;

    public static final String SUPPORT_EMAIL = "vonsark87@yandex.ru";

    public static final int SECURITY_ACTIVITY = 0;
    public static final int MAIN_ACTIVITY = 1;
    public static final int MAIL_ACTIVITY = 2;

    public static String CLAIMS = "user";

    public static String BASE_URL = "https://pro-edition.mobwal.com";
    //public static String BASE_URL = "http://10.10.6.100:5007";
    public static String VIRTUAL_DIR_PATH = "/release";

    /**
     * Адрес соединения с сервером
     * @return адрес
     */
    public static String getConnectUrl() {
        return BASE_URL + VIRTUAL_DIR_PATH;
    }
}

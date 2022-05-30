package ru.mobnius.core.data;

/**
 * Настройки
 * TODO: 04.06.2019 Существует временно потом нужно его менять
 */
public class GlobalSettings {
    /**
     * Применять ли статическую скорось передачи данных
     * По умолчанию использовать false. При тестирование удобно true
     */
    public static boolean STATUS_TRANSFER_SPEED = false;

    /**
     * Тут может быть три значения dev,test,release. По умолчанию release
     */
    public static String ENVIRONMENT = "release";

    public static String ENVIRONMENT_RELEASE = "release";
    public static String ENVIRONMENT_TEST = "test";
    public static String ENVIRONMENT_DEV = "dev";
    public static String ENVIRONMENT_DEMO = "demo";

    public static final String DEFAULT_USER_NAME = "test";
    public static final String DEFAULT_USER_PASSWORD = "qwe-123+";
    public static final Object DEFAULT_USER_ID = 4;
    public static String BASE_URL = "http://192.168.1.69:5006";
    public static String VIRTUAL_DIR_PATH = "/release";

    /**
     * Адрес соединения с сервером
     * @return адрес
     */
    public static String getConnectUrl() {
        return BASE_URL + VIRTUAL_DIR_PATH;
    }
}

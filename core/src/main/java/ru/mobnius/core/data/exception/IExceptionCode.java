package ru.mobnius.core.data.exception;

public interface IExceptionCode {
    /**
     * Ошибка на уровне приложения.
     * Не была перехвачено ни кем
     */
    int ALL = 666;

    /**
     * Общее
     */
    int MAIN = 0;

    /**
     * Информация о сборке
     */
    int BUILD_ABOUT = 1;

    /**
     * Список номер телефонов у задания
     */
    int USER_TELEPHONE = 2;

    /**
     * Авторизация
     */
    int LOGIN = 3;

    /**
     * Создание PIN-кода
     */
    int CREATE_PIN = 4;

    /**
     * авторизация через PIN
     */
    int AUTH_FROM_PIN = 5;

    /**
     * Карточка точки
     */
    int POINT = 6;

    /**
     * Информация о точке
     */
    int POINT_INFO = 7;

    /**
     * Список точек
     */
    int POINTS = 8;

    /**
     * Маршруты
     */
    int ROUTES = 9;

    /**
     * синхронизация
     */
    int SYNCHRONIZATION = 10;

    /**
     * Синхронизация в сервисе
     */
    int SERVICE = 11;

    /**
     * Сохранение телеметрии
     */
    int SAVE_TELEMETRY = 12;

    /**
     * хранение трекинга
     */
    int SAVE_TRACKING = 13;

    /**
     * Работа с камерой
     */
    int CAMERA = 14;

    /**
     * Выбор пользовательской точки
     */
    int SELECT_USER_POINT = 15;

    /**
     * Вывод списка ошибок
     */
    int ERROR_LIST = 16;

    /**
     * Вывод карточки ошибки
     */
    int ERROR_DETAIL = 17;

    /**
     * Настройки
     */
    int MAIN_SETTING = 18;

    /**
     * Настройки о приложении
     */
    int SETTING_ABOUT = 19;

    /**
     * Настройки отладки
     */
    int SETTING_DEBUG = 20;

    /**
     * Вывод карточки редактирования email
     */
    int USER_EMAIL = 21;
    /*
     * Карта яндекс
     */
    int YANDEX_MAP = 22;

    /**
     * Форма обратной связи
     */
    int FEEDBACK = 23;

    /**
     * Форма обратной связи список
     */
    int FEEDBACK_LIST = 24;

    /**
     * Форма профиля пользователя
     */
    int USER_PROFILE = 25;

    /**
     * Форма информации о маршруте
     */
    int ROUTE_INFO = 26;

    /**
     * Форма акта снятия контрольного показания
     */
    int CONTROL_METER_READINGS = 27;

    /**
     * Выбор акта
     */
    int CHOICE_DOCUMENT = 28;
    /**
     * Добавление фото
     */
    int POINT_PHOTO = 29;
    /**
     * Добавление фильтров
     */
    int FILTER = 30;
    /**
     * Все задания
     */
    int ALL_POINTS = 31;
    /**
     * Сортировка маршрутов
     */
    int ROUTE_SORT = 32;
    /**
     * Сортировка заданий
     */
    int POINT_SORT = 33;
    /**
     * Ввод пин-кода
     */
    int PIN_CODE = 34;
    /**
     * Список уведомлений
     */
    int PUSH = 35;

    /**
     * Подробное фото
     */
    int IMAGE_VIEW = 36;

    /**
     * Акт об ограничении ээ у потребителя
     */
    int EE_LIMIT = 37;
    /**
     * Карта
     */
    int MAP = 38;

    /**
     * Акт о ручении уведомления
     */
    int NOTICE = 39;

    int SIGNATURE = 40;

    int SECURITY_PREF = 41;
    int PHOTO_CHANGE = 42;
    int SETTING_SERVICE = 43;
    int UPDATE_ABOUT = 49;
    int CREATE_ANOMALY = 51;
    int HELP = 50;
    /**
     * Настройки о приложении
     */
    int SETTING_INTERFACE = 53;
}

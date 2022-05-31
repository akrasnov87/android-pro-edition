package ru.mobnius.core.data;

import android.location.Location;
import android.os.Bundle;

//import org.greenrobot.greendao.AbstractDaoSession;

import java.io.Serializable;

import ru.mobnius.core.data.forms.FormResult;
import ru.mobnius.core.data.gallery.BasePhotoManager;
import ru.mobnius.core.utils.BundleUtil;

public abstract class DiffValueManager
        implements Serializable {

    private OnFormValuesListeners mListeners;
    protected Bundle mBundle;
    protected Bundle mMasterBundle;
    //protected AbstractDaoSession mDaoSession;

    private String mRouteId;
    private String mPointId;
    private String mResultId;

    /*public DiffValueManager(AbstractDaoSession daoSession, OnFormValuesListeners listeners) {
        mListeners = listeners;
        mDaoSession = daoSession;
        mBundle = new Bundle();
    }*/

    /**
     * Получение информации полях и значениях
     * @return значения
     */
    public Bundle getValues() {
        return mBundle;
    }

    /**
     * Получение информации полях и значениях
     * @return значения
     */
    public Bundle getMasterValues() {
        return mMasterBundle;
    }

    /**
     * Получение одного значения
     * @param fieldName имя поля
     * @param <T> тип параметра
     * @return значение поля
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String fieldName) {
        return (T)mBundle.get(fieldName);
    }

    /**
     * Получение одного значения
     * @param fieldName имя поля
     * @return значение поля
     */
    public String getValueString(String fieldName) {
        return (String)mBundle.getString(fieldName);
    }

    /**
     * Получение одного значения
     * @param fieldName имя поля
     * @return значение поля
     */
    public boolean getValueBoolean(String fieldName) {
        return (boolean) mBundle.getBoolean(fieldName);
    }

    /**
     * Получение одного значения
     * @param fieldName имя поля
     * @return значение поля
     */
    public int getValueInt(String fieldName) {
        return (int) mBundle.getInt(fieldName, 0);
    }

    /**
     * Получение одного значения
     * @param fieldName имя поля
     * @return значение поля
     */
    public long getValueLong(String fieldName) {
        return (long) mBundle.getLong(fieldName, 0);
    }

    /**
     * Установка / Добавление значений
     * @param values заначения
     */
    public void addValues(Bundle values) {
        mBundle.putAll(values);
    }

    /**
     * Установка / Добавление значений
     * @param name имя
     *
     */
    public void addValue(String name, Serializable values) {
        mBundle.putSerializable(name, values);
    }

    /**
     * Проверка на доступность поля
     * @param fieldName имя поля
     */
    public boolean existsValue(String fieldName) {
        return mBundle.containsKey(fieldName);
    }

    /**
     * Фиксируем изменения параметров
     */
    public void fixMasterBundle() {
        mMasterBundle = new Bundle();
        mMasterBundle.putAll(mBundle);
    }

    /**
     * Сброс данных
     */
    public void resetBundle() {
        mBundle.clear();
        mMasterBundle.clear();
    }

    public void initializeValues(String routeId, String pointId, String resultId) {
        mRouteId = routeId;
        mPointId = pointId;
        mResultId = resultId;

        //Bundle bundle = mListeners.onFormValues(mDaoSession, routeId, pointId, resultId);
        //mBundle.putAll(bundle);
    }

    /**
     * Были ли изменения
     * @return было ли изменение
     */
    public boolean isChanged() {
        return BundleUtil.equalBundles(mMasterBundle, mBundle);
    }

    public String getRouteId() {
        return mRouteId;
    }

    public String getPointId() {
        return mPointId;
    }

    public String getResultId() {
        return mResultId;
    }

    public abstract FormResult onUpdate(BasePhotoManager photoManager);

    public abstract FormResult onCreate(Location location, BasePhotoManager photoManager, boolean isValid);

    @Deprecated
    public abstract FormResult onUpdate(String telephone, BasePhotoManager photoManager, boolean isValid);
    @Deprecated
    public abstract FormResult onCreate(String telephone, Location location, BasePhotoManager photoManager, boolean isValid);
    @Deprecated
    public abstract FormResult onCreate(String telephone, Location location, Location manualLocation, BasePhotoManager photoManager, boolean isValid);
    @Deprecated
    public abstract FormResult onUpdate(String telephone, Location manualLocation, BasePhotoManager photoManager, boolean isValid);
}

package com.mobwal.android.library.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.android.library.annotation.FieldMetaData;
import com.mobwal.android.library.annotation.TableMetaData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    /**
     * Получение поля по имени в SQLite
     * @param entity сущность
     * @param fieldName имя поля в SQLite
     * @return поле класса
     */
    @Nullable
    public static <T> Field getClassField(@NonNull T entity, @NonNull String fieldName) {
        Field[] fields = getDbFields(entity);
        for (Field field:
             fields) {
            if(getFieldName(field).equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Получение имени поля, которое используется в SQLite
     * @param field поле класс (объекта)
     * @return имя поля
     */
    public static String getFieldName(@NonNull Field field) {
        FieldMetaData fieldMetaData = getFieldMetaData(field);
        if(fieldMetaData != null) {
            return fieldMetaData.name();
        } else {
            return field.getName();
        }
    }

    /**
     * Получение имени класса
     * @param entity сощность
     * @return наименование класса
     */
    public static String getTableName(@NonNull Class<?> entity) {
        TableMetaData tableMetaData = ReflectionUtil.getTableMetaData(entity);
        if(tableMetaData == null) {
            return entity.getSimpleName();
        }
        return tableMetaData.name();
    }

    /**
     * Получение мета описания поля для SQLite
     * @param field поле
     * @return анотация *.annotation.FieldMetaData
     */
    @Nullable
    public static FieldMetaData getFieldMetaData(@NonNull Field field) {
        return field.getAnnotation(FieldMetaData.class);
    }

    /**
     * Получение мета описания объекта для SQLite
     * @param entity сущность
     * @return анотация *.annotation.TableMetaData
     */
    @Nullable
    public static TableMetaData getTableMetaData(@NonNull Class<?> entity) {
        return entity.getAnnotation(TableMetaData.class);
    }

    /**
     * Получение списка списка полей, которые могут использоваться в запросах к SQLite
     * @param entity сущность
     * @return список полей
     */
    public static <T> Field[] getDbFields(@NonNull T entity) {
        // получение всех полей
        Field[] fields = entity.getClass().getDeclaredFields();

        List<Field> array = new ArrayList<>();

        for (Field field : fields) {
            // нужны только пубичные поля
            if (java.lang.reflect.Modifier.isPublic(field.getModifiers()) &&
                    !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                array.add(field);
            }
        }

        return array.toArray(new Field[0]);
    }
}

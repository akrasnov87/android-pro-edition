package com.mobwal.pro.reflection;

import androidx.annotation.Nullable;

import com.mobwal.pro.annotation.FieldMetaData;
import com.mobwal.pro.annotation.TableMetaData;

import org.jetbrains.annotations.NotNull;

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
    public static <T> Field getClassField(@NotNull T entity, @NotNull String fieldName) {
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
    public static String getFieldName(@NotNull Field field) {
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
    public static <T> String getTableName(@NotNull T entity) {
        TableMetaData tableMetaData = ReflectionUtil.getTableMetaData(entity);
        if(tableMetaData == null) {
            return entity.getClass().getSimpleName();
        }
        return tableMetaData.name();
    }

    /**
     * Получение мета описания поля для SQLite
     * @param field поле
     * @return анотация *.annotation.FieldMetaData
     */
    @Nullable
    public static FieldMetaData getFieldMetaData(@NotNull Field field) {
        return field.getAnnotation(FieldMetaData.class);
    }

    /**
     * Получение мета описания объекта для SQLite
     * @param entity сущность
     * @return анотация *.annotation.TableMetaData
     */
    @Nullable
    public static <T> TableMetaData getTableMetaData(@NotNull T entity) {
        return entity.getClass().getAnnotation(TableMetaData.class);
    }

    /**
     * Получение списка списка полей, которые могут использоваться в запросах к SQLite
     * @param entity сущность
     * @return список полей
     */
    public static <T> Field[] getDbFields(@NotNull T entity) {
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

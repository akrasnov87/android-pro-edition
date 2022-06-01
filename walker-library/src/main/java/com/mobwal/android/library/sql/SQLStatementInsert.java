package com.mobwal.android.library.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import com.mobwal.android.library.util.DateUtil;
import com.mobwal.android.library.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

/**
 * Дополнительный класс для массовой вставки данных в базу данных SQLite
 */
public class SQLStatementInsert {
    final String mTable;
    String[] mFields;
    String mParams;
    SQLiteStatement mStatement;

    public <T> SQLStatementInsert(@NonNull T entity, @NonNull SQLiteDatabase db) {
        mTable = ReflectionUtil.getTableName(entity.getClass());

        StringBuilder builder = new StringBuilder();
        ArrayList<String> tempFields = new ArrayList<>();

        Field[] fields = ReflectionUtil.getDbFields(entity);
        for (Field field : fields) {
            String fieldName = ReflectionUtil.getFieldName(field);
            builder.append("?,");
            tempFields.add(fieldName);
        }

        mFields = tempFields.toArray(new String[0]);
        mParams = builder.substring(0, builder.length() - 1);

        String sql = convertToQuery();
        mStatement = db.compileStatement(sql);
    }

    /**
     * запрос в БД для вставки
     * @return возвращается запрос
     */
    private String convertToQuery() {
        StringBuilder builder = new StringBuilder();
        for (String field : mFields) {
            builder.append(field).append(",");
        }
        return "INSERT OR REPLACE INTO " + mTable + "("+builder.substring(0, builder.length() - 1) + ")" + " VALUES(" + mParams +")";
    }

    /**
     * Привязка данных для вставки в запрос
     * @param entity сущность для обработки
     */
    public <T> void bind(T entity) throws NoSuchFieldException, IllegalAccessException {
        mStatement.clearBindings();

        for(int i = 0; i < mFields.length; i++) {
            String fieldName = mFields[i];
            Field field = ReflectionUtil.getClassField(entity, fieldName);
            if(field == null)
                continue;
            Object value = field.get(entity);
            if(value != null) {
                String fieldTypeName = field.getType().getSimpleName().toLowerCase();

                switch (fieldTypeName) {
                    case "double":
                        mStatement.bindDouble(i + 1, (Double) value);
                        break;

                    case "boolean":
                        mStatement.bindLong(i + 1, (boolean)value ? 1 : 0);
                        break;

                    case "long":
                        mStatement.bindLong(i + 1, (Long)value);
                        break;

                    case "int":
                    case "integer":
                        mStatement.bindLong(i + 1, Long.parseLong(String.valueOf(value)));
                        break;

                    case "date":
                        mStatement.bindString(i + 1, DateUtil.convertDateToSystemString((Date)value));
                        break;

                    default:
                        mStatement.bindString(i + 1, String.valueOf(value));
                        break;
                }
            } else {
                mStatement.bindNull(i + 1);
            }
        }

        mStatement.execute();
    }
}

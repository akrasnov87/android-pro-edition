package ru.mobnius.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

//import org.greenrobot.greendao.AbstractDao;

import java.util.ArrayList;
import java.util.Iterator;

import ru.mobnius.core.data.storage.FieldNames;

/**
 * Класс для обработки JSONObject и создания из него SQL запроса на обновление записи
 */
public class SqlUpdateFromJSONObject {
    //final String params;
    //final String tableName;
    //final String[] fields;
    //final String pkColumn;

    /**
     * Конструктор
     * @param object объект для обработки
     * @param tableName имя таблицы
     * @param pkColumn имя первичного ключа
     */
    /*public SqlUpdateFromJSONObject(JsonObject object, String tableName, String pkColumn, AbstractDao abstractDao) {
        //this.tableName = tableName;
        //this.pkColumn = pkColumn;

        StringBuilder builder = new StringBuilder();
        ArrayList<String> tempFields = new ArrayList<>();
        Iterator<String> keys = object.keySet().iterator();
        String fieldName;
        while (keys.hasNext()) {
            fieldName = keys.next();
            if (fieldName.equals(pkColumn)) {
                continue;
            }
            if (isColumnExists(abstractDao, fieldName.toLowerCase())) {
                tempFields.add(fieldName);
                builder.append(fieldName).append("  = ?, ");
            }
        }
        fields = tempFields.toArray(new String[0]);
        params = builder.substring(0, builder.length() - 2);
    }*/

    /**
     * запрос в БД для обновления
     * @param appendField добавить дополнительные поля
     * @return возвращается запрос
     */
    /*public String convertToQuery(boolean appendField) {
        String appendStr = "";
        if(appendField){
            appendStr= " and (" + FieldNames.OBJECT_OPERATION_TYPE + " = ? OR " + FieldNames.OBJECT_OPERATION_TYPE + " = ?)";
        }
        return "UPDATE " + tableName + " set " + params + " where " + pkColumn + " = ?" + (appendField ? appendStr : "");
    }*/

    /**
     * Получение объекта для передачи в запрос
     * @param object объект для обработки
     * @return Массив значений полей
     * @param appendField добавить дополнительные поля
     */
    /*public Object[] getValues(JsonObject object, boolean appendField) {
        ArrayList<Object> values = new ArrayList<>(fields.length);

        Object pk = null;

        for (String field : fields) {
            if (pkColumn.equals(field)) {
                pk = toObject(object.get(field));
                continue;
            }
            values.add(toObject(object.get(field)));
        }

        values.add(pk);
        if(appendField){
            values.add(null);
            values.add("");
        }

        return values.toArray();
    }*/

    private Object toObject(JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return null;
        } else if (value.getAsJsonPrimitive().isNumber()) {
            return value.getAsDouble();
        } else if (value.getAsJsonPrimitive().isBoolean()) {
            return value.getAsBoolean();
        } else {
           return value.getAsString();
        }
    }

    /*private boolean isColumnExists(AbstractDao abstractDao, String columnName) {
        for (String s : abstractDao.getAllColumns()) {
            if (s.toLowerCase().equals(columnName)) {
                return true;
            }
        }

        return false;
    }*/
}
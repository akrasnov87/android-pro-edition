package ru.mobnius.core.utils;

//import org.greenrobot.greendao.AbstractDao;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Класс для обработки JSONObject и создания из него SQL запроса на добавление записи
 */
public class SqlInsertFromJSONObject{
    //final String params;
    //final String tableName;
    //final String[] fields;

    /**
     * Конструктор
     *
     * @param object      объект для обработки
     * @param tableName   имя таблицы
     * @param abstractDao внутренняя сущность
     */
    /*public SqlInsertFromJSONObject(JSONObject object, String tableName, AbstractDao abstractDao) {
        this.tableName = tableName;

        StringBuilder builder = new StringBuilder();
        ArrayList<String> tempFields = new ArrayList<>();
        Iterator<String> keys = object.keys();

        while (keys.hasNext()) {
            String name = keys.next();
            if (isColumnExists(abstractDao, name.toLowerCase())) {
                builder.append("?,");
                tempFields.add(name);
            }
        }
        fields = tempFields.toArray(new String[0]);
        params = builder.substring(0, builder.length() - 1);
    }*/

    /**
     * запрос в БД для вставки
     * @param appendField добавить дополнительные поля
     * @return возвращается запрос
     */
    /*public String convertToQuery(boolean appendField) {
        StringBuilder builder = new StringBuilder();
        for (String field : fields) {
            builder.append(field).append(",");
        }
        String strAppendField = "";
        if(appendField){
            strAppendField = ",OBJECT_OPERATION_TYPE,IS_DELETE,IS_SYNCHRONIZATION,TID,BLOCK_TID";
        }
        return "INSERT INTO " + tableName + "("+builder.substring(0, builder.length() - 1) + strAppendField+")" + " VALUES("+ params + (appendField ? ",?,?,?,?,?" : "") +")";
    }*/

    /**
     * Получение объекта для передачи в запрос
     * @param object объект для обработки
     * @param appendField добавить дополнительные поля
     * @return Массив значений полей
     * @throws JSONException исключение
     */
    /*public Object[] getValues(JSONObject object, boolean appendField) throws JSONException {
        Object[] values = new Object[appendField? fields.length + 5 : fields.length];

        for(int i = 0; i < fields.length; i++){
            values[i] = object.get(fields[i]);
        }
        if(appendField){
            values[fields.length] = "";
            values[fields.length + 1] = false;
            values[fields.length + 2] = true;
            values[fields.length + 3] = "";
            values[fields.length + 4] = "";
        }
        return values;
    }*/

    /**
     * колонка доступна или нет
     *
     * @param columnName  имя колонки
     * @return true - колонка доступна в модели
     */
    /*private boolean isColumnExists(AbstractDao abstractDao, String columnName) {
        for (String s : abstractDao.getAllColumns()) {
            if (s.toLowerCase().equals(columnName)) {
                return true;
            }
        }

        return false;
    }*/
}

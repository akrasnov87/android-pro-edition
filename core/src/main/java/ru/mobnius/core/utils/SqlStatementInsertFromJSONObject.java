package ru.mobnius.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;

//import org.greenrobot.greendao.AbstractDao;
//import org.greenrobot.greendao.database.DatabaseStatement;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * https://www.youtube.com/watch?v=E4zklaVBj5w&list=PLyfVjOYzujugap6Rf3ETNKkx4v9ePllNK&index=40&pbjreload=101
 */
public class SqlStatementInsertFromJSONObject {

    //final String mParams;
    //final String mTable;
    //final String[] mFields;
    //DatabaseStatement mStatement;
    //final boolean mIsRequestToServer;

    /*public SqlStatementInsertFromJSONObject(JsonObject object, String tableName, boolean isRequestToServer, AbstractDao abstractDao) {
        mTable = tableName;
        mIsRequestToServer= isRequestToServer;

        StringBuilder builder = new StringBuilder();
        ArrayList<String> tempFields = new ArrayList<>();

        for (String name : object.keySet()) {
            if (isColumnExists(abstractDao, name.toLowerCase())) {
                builder.append("?,");
                tempFields.add(name);
            }
        }
        mFields = tempFields.toArray(new String[0]);
        mParams = builder.substring(0, builder.length() - 1);

        String sql  = convertToQuery(isRequestToServer);
        mStatement = abstractDao.getDatabase().compileStatement(sql);
    }*/

    /**
     * Получение объекта для передачи в запрос
     * @param object объект для обработки
     */
    public void bind(JsonObject object) {
        /*mStatement.clearBindings();

        for(int i = 0; i < mFields.length; i++) {
            bindObjectToStatement(mStatement, i + 1, object.get(mFields[i]));
        }

        if(mIsRequestToServer) {
            mStatement.bindString(mFields.length + 1, "");
            mStatement.bindLong(mFields.length + 2, 0);
            mStatement.bindLong(mFields.length + 3, 1);
            mStatement.bindString(mFields.length + 4, "");
            mStatement.bindString(mFields.length + 5, "");
        }
        mStatement.execute();*/
    }

    /**
     * запрос в БД для вставки
     * @param appendField добавить дополнительные поля
     * @return возвращается запрос
     */
    /*public  String convertToQuery(boolean appendField) {
        StringBuilder builder = new StringBuilder();
        for (String field : mFields) {
            builder.append(field).append(",");
        }
        String strAppendField = "";
        if(appendField) {
            strAppendField = ",OBJECT_OPERATION_TYPE,IS_DELETE,IS_SYNCHRONIZATION,TID,BLOCK_TID";
        }
        return "INSERT INTO " + mTable + "("+builder.substring(0, builder.length() - 1) + strAppendField+")" + " VALUES(" + mParams + (appendField ? ",?,?,?,?,?" : "") +")";
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

    /*private void bindObjectToStatement(DatabaseStatement statement, int index, JsonElement value) {
        if (value == null || value.isJsonNull()) {
            statement.bindNull(index);
        } else {
            JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();

            if (jsonPrimitive.isNumber() && jsonPrimitive.getAsNumber().toString().contains(".")) {
                statement.bindDouble(index, value.getAsDouble());
            } else if (jsonPrimitive.isNumber()) {
                statement.bindLong(index, value.getAsLong());
            } else if (jsonPrimitive.isBoolean()) {
                statement.bindLong(index, value.getAsBoolean() ? 1 : 0);
            } else {
                statement.bindString(index, value.getAsString());
            }
        }
    }*/
}

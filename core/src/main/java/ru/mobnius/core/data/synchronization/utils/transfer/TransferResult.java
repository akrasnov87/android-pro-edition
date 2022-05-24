package ru.mobnius.core.data.synchronization.utils.transfer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * результат обработки ответа
 */
public class TransferResult {
    /**
     * метаинформация
     */
    public Meta meta;

    /**
     * данные
     */
    public Data data;

    /**
     * результат
     */
    public byte[] result;

    /**
     * статус код
     */
    public int code;

    /**
     * идентияикатор транзакции
     */
    public String tid;

    public static class Meta{
        /**
         * Статус завершенности
         */
        public boolean processed;
        /**
         * индикатор начала
         */
        public int start;
        /**
         * длина данных
         */
        public int totalLength;
    }

    public static class Data{
        /**
         * Результат выполнения
         */
        public boolean success;
        /**
         * сообщение результата выполнения
         */
        public String msg;
    }

    /**
     * Создание результата сообщений об ошибке
     * @param message текст сообщения
     * @return результат отправки
     */
    public static TransferResult error(String message){
        TransferResult result = new TransferResult();

        Data d = new Data();
        d.success = false;
        d.msg = message;
        result.data = d;

        return result;
    }

    /**
     * Чтение информации из входных данным
     * @param object входной объект
     * @return результат передачи
     */
    public static TransferResult readResult(JSONObject object){

        if(object != null) {
            // обработка сообщений с сервера
            try {
                object.getString("tid");
            } catch (JSONException ignored) {
                return TransferResult.error("Ошибка чтения информации из результата");
            }

            JSONObject jsonData;
            try {
                jsonData = object.getJSONObject("data");
                TransferResult result = new TransferResult();
                result.result = (byte[]) object.get("result");
                result.tid = object.getString("tid");
                result.code = object.getInt("code");

                Data d = new Data();
                d.msg = jsonData.getString("msg");
                d.success = jsonData.getBoolean("success");

                JSONObject jsonMeta = object.getJSONObject("meta");

                Meta m = new Meta();
                m.processed = jsonMeta.getBoolean("processed");
                if(jsonMeta.has("start")) {
                    m.start = jsonMeta.getInt("start");
                }
                if(jsonMeta.has("totalLength")) {
                    m.totalLength = jsonMeta.getInt("totalLength");
                }

                result.data = d;
                result.meta = m;

                return result;
            } catch (JSONException e) {
                return TransferResult.error(e.getMessage());
            }
        }else {
            return TransferResult.error("Ответ от сервера получен. Данные отсуствуют.");
        }
    }
}

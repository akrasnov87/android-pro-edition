package com.mobwal.android.library.data.sync.util;

/**
 * чтение статусной информации у сокета
 */
public class SocketStatusReader {
    private String name;

    /**
     * Наименование статуса
     * @return наименование
     */
    public String getName(){
        return name;
    }

    private String[] params;

    /**
     * Параметры переданные со статусом
     * @return параметры
     */
    public String[] getParams(){
        return params;
    }

    private SocketStatusReader(String inputString){
        // получаем наименование
        int i = inputString.indexOf("]");
        name = inputString.substring(1, i);
        String paramsStr = inputString.substring(i+1);
        params = paramsStr.split(";");
    }

    /**
     * Создание объекта
     * @param status статусная строка
     * @return объект
     */
    public static SocketStatusReader getInstance(String status){
        if(status.matches("\\[\\w+]([\\S|\\s]+;?)+")) {
            return new SocketStatusReader(status);
        }else{
            return null;
        }
    }
}

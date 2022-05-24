package ru.mobnius.core.data.packager;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Блок с бинарными файлами
 */
public class BinaryBlock {

    private ArrayList<FileBinary> items;

    /**
     * возвращается список файлов
     * @return список файлов
     */
    public FileBinary[] getFiles() {
        if(items == null)
            return new FileBinary[0];

        return items.toArray(new FileBinary[0]);
    }

    /**
     * добавление в блок бинарного файла
     * @param name имя файла
     * @param key ключ файла. Должен быть уникальным
     * @param bytes масиив данных
     */
    public void add(String name, String key, byte[] bytes) {
        if(items == null)
            items = new ArrayList<>();

        items.add(new FileBinary(name, key, bytes));
    }

    /**
     * текущий блок превращается в массив байтов
     * @return массив байтов
     */
    public byte[] toBytes() {
        if(items == null)
            return new byte[0];

        int length = 0;
        for(int i =0; i < items.size(); i++){
            if(items.get(i).bytes != null) {
                length += items.get(i).bytes.length;
            }
        }

        byte[] allByteArray = new byte[length];

        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        for(int i =0; i < items.size(); i++) {
            if(items.get(i).bytes != null) {
                buff.put(items.get(i).bytes);
            }
        }

        return buff.array();
    }

    /**
     * информация для метаописания
     * @return возвращается массив вложений
     */
    public MetaAttachment[] getAttachments() {
        if(items == null)
            return new MetaAttachment[0];

        MetaAttachment[] attachments = new MetaAttachment[items.size()];
        for(int i =0; i < items.size(); i++) {
            FileBinary item = items.get(i);
            attachments[i] = new MetaAttachment(item.bytes == null ? 0 : item.bytes.length, item.name, item.key);
        }
        return attachments;
    }

    /**
     * очистка данных
     */
    public void clear(){
        if(items != null) {
            items.clear();
            items = null;
        }
    }
}

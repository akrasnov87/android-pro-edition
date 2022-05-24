package ru.mobnius.core.data.packager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.zip.ZipManager;

public class PackageUtil {

    public static MetaSize readSize(byte[] bytes) throws Exception {
        if (bytes.length >= 16) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                str.append(new String(new byte[]{bytes[i]}));
            }
            try {
                String type = str.substring(0, 3);
                int status = Integer.parseInt(str.substring(15));
                String sizeStr = str.substring(3, 15).replace(".", "");
                try {
                    return new MetaSize(Integer.parseInt(sizeStr), status, type);
                } catch (Exception e) {
                    throw new Exception("Ошибка чтения длины мета информации: " + sizeStr);
                }
            } catch (Exception e2) {
                throw new Exception("Ошибка чтения статуса пакета: " + str + " (символ " + 16 + ")");
            }
        } else {
            throw new Exception("Длина пакета меньше 16");
        }
    }

    public static MetaPackage readMeta(byte[] bytes, boolean zip) throws Exception {
        return new Gson().fromJson(getString(Arrays.copyOfRange(bytes, 16, readSize(bytes).metaSize + 16), zip), MetaPackage.class);
    }

    public static List<StringMapItem> readMap(byte[] bytes, boolean zip) throws Exception {
        MetaSize metaSize = readSize(bytes);
        MetaPackage aPackage = readMeta(bytes, zip);
        int start = metaSize.metaSize + 16;
        return Arrays.asList(new Gson().fromJson(getString(Arrays.copyOfRange(bytes, start, aPackage.stringSize + start), zip), StringMapItem[].class));
    }

    public static RPCItem readMapItem(byte[] bytes, int idx, boolean zip) throws Exception {
        List<StringMapItem> mapItems = readMap(bytes, zip);
        int start = readSize(bytes).metaSize + 16 + readMeta(bytes, zip).stringSize;
        for (int i = 0; i < idx; i++) {
            start += mapItems.get(i).length;
        }
        return new Gson().fromJson(getString(Arrays.copyOfRange(bytes, start, mapItems.get(idx).length + start), zip), RPCItem.class);
    }

    public static RPCResult[] readMapItemResult(byte[] bytes, int idx, boolean zip) throws Exception {
        List<StringMapItem> mapItems = readMap(bytes, zip);
        int start = readSize(bytes).metaSize + 16 + readMeta(bytes, zip).stringSize;
        for (int i = 0; i < idx; i++) {
            start += mapItems.get(i).length;
        }
        return RPCResult.createInstanceByGson(getString(Arrays.copyOfRange(bytes, start, mapItems.get(idx).length + start), zip));
    }

    public static BinaryBlock readBinaryBlock(byte[] bytes, boolean zip) throws Exception {
        MetaSize metaSize = readSize(bytes);
        MetaPackage aPackage = readMeta(bytes, zip);
        int start = metaSize.metaSize + 16 + aPackage.stringSize + aPackage.bufferBlockToLength + aPackage.bufferBlockFromLength;
        int end = aPackage.binarySize + start;
        BinaryBlock binaryBlock = new BinaryBlock();
        if (start == end) {
            return binaryBlock;
        }
        byte[] temp = Arrays.copyOfRange(bytes, start, end);
        int idx = 0;
        for (MetaAttachment attachment : aPackage.attachments) {
            byte[] t = new byte[attachment.size];
            System.arraycopy(temp, idx, t, 0, attachment.size);
            idx += attachment.size;
            binaryBlock.add(attachment.name, attachment.key, t);
        }
        return binaryBlock;
    }

    public static byte[] updateStatus(byte[] bytes, int status) throws Exception {
        if (bytes.length >= 16) {
            bytes[15] = String.valueOf(status).getBytes()[0];
            return bytes;
        }
        throw new Exception("Длина пакета меньше 16");
    }

    public static String getString(byte[] temp, boolean zip) {
        if (!zip) {
            return new String(temp);
        }
        try {
            byte[] zipBytes = ZipManager.decompress(temp);
            if (zipBytes != null) {
                return new String(zipBytes);
            }
            return new String(temp);
        } catch (IOException | DataFormatException e) {
            Logger.error("Ошибка распаковки строкового блока.", e);
            return new String(temp);
        }
    }
}

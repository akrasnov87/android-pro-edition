package ru.mobnius.core.utils;

import android.text.Html;
import android.text.Spanned;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.Set;

public class ContentUtil {
    /**
     * Обработка контента
     * @param content Текст для обработки. Например, {0}, world!!!
     * @param json объект анализа для данных. JSON
     * @return Результат обратки
     * @throws JsonSyntaxException исключение при обработке
     */
    public static Spanned getContentText(String content, String json) throws JsonSyntaxException{
        if(json != null && !json.isEmpty()) {
            String newContent = content;
            try {
                JsonElement jsonElement = JsonParser.parseString(json);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Set<String> keySet = jsonObject.keySet();
                for (String key :
                        keySet) {
                    String value;

                    if(jsonObject.get(key).isJsonNull()) {
                        continue;
                    }

                    if(jsonObject.getAsJsonPrimitive(key).isBoolean()) {
                        value = jsonObject.get(key).getAsBoolean() ? "Да" : "Нет";
                    } else if(jsonObject.getAsJsonPrimitive(key).isNumber()) {
                        value = jsonObject.get(key).toString();
                    } else {
                        value = jsonObject.get(key).getAsString();
                    }

                    newContent = newContent.replace(key, value);
                }
                return Html.fromHtml(newContent);
            }catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }
        return Html.fromHtml(content == null ? "" : content);
    }
}

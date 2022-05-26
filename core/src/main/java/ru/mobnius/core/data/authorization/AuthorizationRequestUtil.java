package ru.mobnius.core.data.authorization;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.Meta;
import ru.mobnius.core.utils.VersionUtil;

public class AuthorizationRequestUtil {


    /**
     * Создание запроса на сервер для выполнения авторизации
     *
     * @param context  context
     * @param login    логин
     * @param password пароль
     * @return мета информация в результате запроса
     */
    public AuthorizationMeta request(Context context, String login, String password) {
        try {
            String urlParams = "UserName=" + encodeValue(login) + "&Password=" + encodeValue(password) + "&Version=" + VersionUtil.getVersionName(context);
            byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(GlobalSettings.getConnectUrl() + "/auth");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataLength));
                urlConnection.setDoOutput(true);
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setUseCaches(false);
                urlConnection.getOutputStream().write(postData);
                final InputStream stream;
                if (urlConnection.getResponseCode() == Meta.OK) {
                    stream = urlConnection.getInputStream();
                } else {
                    stream = urlConnection.getErrorStream();
                }

                InputStream in = new BufferedInputStream(stream);
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String responseText = s.hasNext() ? s.next() : "";
                try {
                    return convertResponseToMeta(responseText);
                } catch (Exception formatExc) {
                    return new AuthorizationMeta(Meta.ERROR_SERVER, "Ошибка в преобразовании ответа на авторизацию.");
                }
            } catch (Exception innerErr) {
                return new AuthorizationMeta(Meta.ERROR_SERVER, "Ошибка создания запроса на авторизацию.");
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return new AuthorizationMeta(Meta.ERROR_SERVER, "Общая ошибка авторизации.");
        }
    }

    /**
     * Преобразование статуса ответа в мета-информацию
     *
     * @param response ответ от сервера в формате JSON
     * @return мета информация
     */
    AuthorizationMeta convertResponseToMeta(String response) {
        int status;
        String token = null;
        Long userId = null;
        String userName = null;
        String claims = null;
        String message;

        try {
            JSONObject jsonObject = new JSONObject(response);
            try {
                status = jsonObject.getInt("code");
                message = jsonObject.getJSONObject("meta").getString("msg");
            } catch (JSONException e) {
                status = Meta.OK;
                message = "Пользователь авторизован.";
                token = jsonObject.getString("token");
                userId = jsonObject.getJSONObject("user").getLong("id");
                claims = jsonObject.getJSONObject("user").getString("claims");
                userName = jsonObject.getJSONObject("user").getString("login");
            }
        } catch (Exception e) {
            status = Meta.ERROR_SERVER;
            message = "Результат авторизации не является JSON.";
        }
        return new AuthorizationMeta(status, message, token, claims, userId, userName);
    }

    private @NonNull
    String encodeValue(final @NonNull String value) {
        try {
            return URLEncoder.encode(value, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException | IllegalCharsetNameException e) {
            return value;
        }
    }
}
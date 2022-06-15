package com.mobwal.android.library.authorization;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;
import com.mobwal.android.library.util.DateUtil;
import com.mobwal.android.library.util.StringUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Хранение логина и пароля в памяти устройства.
 * Предназначен для проверки безопасности в offline
 */
public class AuthorizationCache {

    private final String PART_FILENAME = ".credentials";
    private final Context mContext;

    /**
     *
     * @param context контекст приложения
     */
    public AuthorizationCache(Context context) {
        this.mContext = context;
    }

    /**
     * Запись настроек безопасности
     * @param user объект в котором храниться безопасности пользователя
     *
     * @return Возвращается true если все хорошо
     */
    public boolean write(@NonNull BasicUser user) {
        return saveUser(user, new Date());
    }

    /**
     * Обновление даты входа
     * @param login логин
     */
    public void updateTime(@NonNull String login) {
        BasicUser user = read(login);
        saveUser(user, new Date());
    }

    /**
     * Чтение настроек безопасности
     * @param login логин пользователя
     * @return Данные об авторизации
     */
    public BasicUser read(@NonNull String login) {
        BasicUser user = null;
        try {
            File file = new File(mContext.getCacheDir(), login + PART_FILENAME);
            FileInputStream inputStream = new FileInputStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                builder.append(str);
            }
            JSONObject json = new JSONObject(builder.toString());
            user = new BasicUser(BasicCredential.decode(json.getString("token")), json.getInt("userId"), json.getString("claims"), json.getString("ip"));
            br.close();
        } catch (FileNotFoundException e) {
            Log.e(Constants.TAG, "Файл для сохранения авторизации не найден. " + e);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Ошибка записи безопасности в файл." + e);
        }
        return user;
    }

    /**
     * Чтение последнего времени входа
     * @param login логин пользователя
     * @return Время последнего входа
     */
    public Date readDate(@NonNull String login) {
        Object result = getData(login, "time");
        if(result != null){
            return DateUtil.convertStringToSystemDate(String.valueOf(result));
        }
        return null;
    }

    /**
     * Очистка кэшированных данных
     * @param currentLogin текущий логин авторизованного пользователя, либо null для удаления всех данных
     */
    public boolean clear(String currentLogin) {
        File dir = new File(mContext.getCacheDir().getPath());
        if(currentLogin == null) {
            File[] files = dir.listFiles();
            if(files != null) {
                for (File file : files) {
                    if (file.getName().indexOf(PART_FILENAME) > 0) {
                        file.delete();
                    }
                }
                return true;
            }
            return false;
        } else {
            if(!StringUtil.isEmptyOrNull(currentLogin)) {
                File file = new File(mContext.getCacheDir(), currentLogin + PART_FILENAME);
                if (file.exists()) {
                    return file.delete();
                }
            }
        }

        return false;
    }

    /**
     * Получение списка пользователея, которые авторизовывались на устройстве
     * @return Возвращается массив строк с логинали пользователей
     */
    public String[] getNames() {
        File dir = new File(mContext.getCacheDir().getPath());
        ArrayList<String> users = new ArrayList<>();

        File[] files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.getName().indexOf(PART_FILENAME) > 0) {
                    users.add(file.getName().replace(PART_FILENAME, ""));
                }
            }
        }
        return users.toArray(new String[0]);
    }

    /**
     * получение данных
     * @param login логин
     * @param key ключ
     * @return данные
     */
    @Nullable
    public Object getData(@NonNull String login, @NonNull String key) {
        Object data = null;
        try {
            File file = new File(mContext.getCacheDir(), login + PART_FILENAME);
            FileInputStream inputStream = new FileInputStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                builder.append(str);
            }
            br.close();
            JSONObject json = new JSONObject(builder.toString());
            data = json.has(key) ? json.get(key) : null;
        } catch (FileNotFoundException e){
            Log.e(Constants.TAG, "Файл для сохранения авторизации не найден. " + e);
        } catch (Exception e){
            Log.e(Constants.TAG, "Ошибка записи безопасности в файл." + e);
        }
        return data;
    }

    /**
     * Сохранение информации о пользователе
     * @param user пользователь
     * @param time время записи
     * @return Возвращается true если все хорошо
     */
    private boolean saveUser(@NonNull BasicUser user, @NonNull Date time) {
        boolean result = false;
        BasicCredential credential = user.getCredential();

        try {
            File file = new File(mContext.getCacheDir(), credential.login + PART_FILENAME);
            FileOutputStream outputStream = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            JSONObject json = new JSONObject();
            json.put("userId", user.getUserId());
            json.put("claims", user.getClaims());
            json.put("time", DateUtil.convertDateToSystemString(time));
            json.put("token", credential.getToken());
            json.put("ip", user.getIp());

            bw.write(json.toString());
            bw.close();

            result = true;
        } catch (FileNotFoundException e) {
            Log.e(Constants.TAG, "Файл для сохранения авторизации не найден. " + e);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Ошибка записи безопасности в файл." + e);
        }

        return result;
    }
}

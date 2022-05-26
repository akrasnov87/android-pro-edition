package ru.mobnius.core.data.authorization;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.StringUtil;

/**
 * Хранение логина и пароля в памяти устройства.
 * Предназначен для проверки безопасности в offline
 */
public class AuthorizationCache {

    private final Context context;
    private final String PART_FILENAME = ".credentials";

    /**
     *
     * @param context контекст приложения
     */
    public AuthorizationCache(Context context){
        this.context = context;
    }

    /**
     * Запись настроек безопасности
     * @param user объект в котором храниться безопасности пользователя
     * @return Возвращается true если все хорошо
     */
    public boolean write(BasicUser user) {
        return saveUser(user, new Date());
    }

    /**
     * Обновление информации об account
     * @param login логин
     * @param time время
     */
    public void update(String login, Date time){
        BasicUser user = read(login);
        saveUser(user, time);
    }

    /**
     * Чтение настроек безопасности
     * @param login логин пользователя
     * @return Данные об авторизации
     */
    public BasicUser read(String login) {
        BasicUser user = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(login + PART_FILENAME)));
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                builder.append(str);
            }
            JSONObject json = new JSONObject(builder.toString());
            user = new BasicUser(BasicCredentials.decode(json.getString("token")), json.getInt("userId"), json.getString("claims"));
            br.close();
        }catch (FileNotFoundException e){
            Logger.error(e);
        }catch (Exception e){
            Logger.error("Ошибка чтения безопасности из файла.",e);
        }
        return user;
    }

    /**
     * Чтение последнего времени входа
     * @param login логин пользователя
     * @return Время последнего входа
     */
    public Date readDate(String login) throws ParseException {
        Object result = getData(login, "time");
        if(result != null){
            return DateUtil.convertStringToDate(String.valueOf(result));
        }
        return null;
    }

    /**
     * Очистка кэшированных данных
     * @param all удалить все файлы безопасности или нет
     */
    public boolean clear(boolean all) {
        File dir = new File(context.getFilesDir().getPath());
        if(all){
            File[] files = dir.listFiles();
            if(files != null) {
                for (File file : files) {
                    if (file.getName().indexOf(PART_FILENAME) > 0) {
                        context.deleteFile(file.getName());
                    }
                }
                return true;
            }
            return false;
        } else {
            String login = Authorization.getInstance().getUser().getCredentials().login;
            if(!StringUtil.isEmptyOrNull(login)) {
                File file = new File(context.getFilesDir(), login + PART_FILENAME);
                if (file.exists()) {
                    return context.deleteFile(PART_FILENAME);
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
        File dir = new File(context.getFilesDir().getPath());
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
    private Object getData(String login, String key) {
        Object data = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(login + PART_FILENAME)));
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                builder.append(str);
            }
            br.close();
            JSONObject json = new JSONObject(builder.toString());
            data = json.has(key) ? json.get(key) : null;
        }catch (FileNotFoundException e){
            Logger.error(e);
        }catch (Exception e){
            Logger.error("Ошибка чтения безопасности из файла.",e);
        }
        return data;
    }

    /**
     * Сохранение информации о пользователе
     * @param user пользователь
     * @param time время записи
     * @return Возвращается true если все хорошо
     */
    private boolean saveUser(BasicUser user, Date time) {
        boolean result = false;
        BasicCredentials credentials = user.getCredentials();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(credentials.login + PART_FILENAME, MODE_PRIVATE)));
            JSONObject json = new JSONObject();
            json.put("userId", user.getUserId());
            json.put("claims", user.claims);
            json.put("time", DateUtil.convertDateToString(time));
            json.put("token", user.getCredentials().getToken());

            bw.write(json.toString());
            bw.close();
            result = true;
        }catch (FileNotFoundException e){
            Logger.error(e);
        }catch (Exception e){
            Logger.error("Ошибка записи безопасности в файл.", e);
        }

        return result;
    }
}

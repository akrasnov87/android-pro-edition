package com.mobwal.pro;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import com.mobwal.android.library.FieldNames;
import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.util.StringUtil;
import com.mobwal.pro.models.PointInfo;
import com.mobwal.pro.models.db.complex.PointItem;
import com.mobwal.pro.models.db.complex.ResultTemplate;
import com.mobwal.pro.models.RouteInfo;
import com.mobwal.pro.models.db.complex.RouteItem;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.models.db.Setting;
import com.mobwal.pro.models.db.Template;
import com.mobwal.android.library.util.DateUtil;
import com.mobwal.android.library.SimpleFileManager;

public class DataManager {
    private final Context mContext;

    public DataManager(Context context) {
        mContext = context;
    }

    /**
     * Получение списка маршрутов
     * @param search посковое слово
     * @return результат выборки
     */
    @Nullable
    public RouteItem[] getRoutes(@Nullable String search) {

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<RouteItem> collection;

        String query = "SELECT \n" +
                "\tr.id as ID, \n" +
                "\tr.c_name as C_NUMBER, \n" +
                "\t(select count(*) from cd_points as p where p.fn_route = r.id) as N_TASK, \n" +
                "\t(select count(*) from cd_points as p where p.fn_route = r.id and p.b_anomaly = 1) as N_ANOMALY, \n" +
                "\t(select count(*) from (select p.id from cd_points as p inner join cd_results as rr on rr.fn_point = p.id where p.fn_route = r.id and p.b_check = 1 and rr.b_disabled = 0 group by p.id) as t) as N_DONE, \n" +
                "\t(select count (*) from (select p.id from cd_points as p where p.fn_route = r.id and p.b_check = 0) as t) as N_FAIL, " +
                "\tr.d_date as D_DATE \n" +
                "from cd_routes as r" + (TextUtils.isEmpty(search) ? "" : " where r.c_name like '%' || ? || '%'");

        if(TextUtils.isEmpty(search)) {
            collection = sqlContext.select(query, null, RouteItem.class);
        } else {
            collection = sqlContext.select(query, new String[] { search }, RouteItem.class);
        }
        if(collection != null) {
            RouteItem[] array = collection.toArray(new RouteItem[0]);
            Arrays.sort(array, (entry1, entry2) -> {
                Long time1 = entry1.d_date.getTime();
                Long time2 = entry2.d_date.getTime();
                return time2.compareTo(time1);
            });

            return array;
        } else {
            return null;
        }
    }

    /**
     * Получение настроек маршрута
     * @return список настроек
     */
    @NotNull
    public Hashtable<String, String> getRouteSettings() {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Setting> settings = sqlContext.select("select * from cd_settings", null, Setting.class);

        Hashtable<String, String> hashtable = new Hashtable<>();
        if (settings != null) {
            for (Setting setting : settings) {
                hashtable.put(setting.c_key, setting.c_value);
            }
        } else {
            hashtable.put("geo", "false");
            hashtable.put("geo_quality", "LOW");
            hashtable.put("image", "true");
            hashtable.put("image_quality", "0.6");
            hashtable.put("image_height", "720");
        }
        return hashtable;
    }

    /**
     * Получение информации по маршруту
     * @param f_route идентификатор маршрута
     * @return данные по маршруту
     */
    public RouteInfo[][] getRouteInfo(@Nullable String f_route) {
        RouteInfo[][] results = new RouteInfo[2][];

        if(f_route == null) {
            return results;
        }

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Route> routeCollection = sqlContext.select("select * from cd_routes as r where r.id = ?", new String[] { f_route }, Route.class);

        List<RouteInfo> items = new ArrayList<>();

        if(routeCollection != null && routeCollection.size() > 0) {
            Route[] routes = routeCollection.toArray(new Route[0]);

            if(routes[0].d_date != null) {
                items.add(new RouteInfo(mContext, mContext.getString(R.string.in_work), DateUtil.toDateTimeString(routes[0].d_date)));
            }

            results[0] = items.toArray(new RouteInfo[0]);

            items.clear();
        }

        Collection<Setting> settingCollection = sqlContext.select("select * from cd_settings as s order by s.c_key", null, Setting.class);
        
        if(settingCollection != null && settingCollection.size() > 0) {
            for (Setting setting: settingCollection) {
                items.add(new RouteInfo(mContext, setting.toKeyName(mContext), setting.c_value));
            }

            results[1] = items.toArray(new RouteInfo[0]);
        } else {
            if(items.size() > 0) {
                results[1] = items.toArray(new RouteInfo[0]);
            } else {
                results[1] = null;
            }
        }

        return results;
    }

    /**
     * Получение маршрута
     * @param f_route идентификатор маршрута
     * @return маршрут
     */
    @Nullable
    public Route getRoute(@Nullable String f_route) {
        if(TextUtils.isEmpty(f_route)) {
            return null;
        }

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Route> routeCollection = sqlContext.select("SELECT * from cd_routes as r where r.id = ?", new String[] { f_route }, Route.class);

        if(routeCollection != null && routeCollection.size() > 0) {
            return routeCollection.toArray(new Route[0])[0];
        }

        return null;
    }

    /**
     * Создание точки
     * @param f_route идентификатор маршрута
     * @param name наименование точки
     * @param desc описание точки
     * @param location геолокация
     * @return результат создания
     */
    public boolean createPoint(@NotNull String f_route, @NotNull String name, @Nullable String desc, @Nullable Location location) {
        // тут нужно создать точку с самым низким приоритетом
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);

        Point point = new Point();
        point.fn_route = f_route;
        point.c_address = name;
        point.c_description = desc;
        if(location != null) {
            point.n_longitude = location.getLongitude();
            point.n_latitude = location.getLatitude();
        }

        Point maxPoint = getPointMaxOrder(f_route);
        if(maxPoint != null) {
            point.n_order = maxPoint.n_order + 1;
        } else {
            point.n_order = 1;
        }

        point.b_anomaly = true;

        return sqlContext.insertMany(new Point[] { point });
    }

    /**
     * Получение точки с максимальным order'ом
     * @param f_route иден. маршрута
     * @return точка
     */
    @Nullable
    private Point getPointMaxOrder(@NotNull String f_route) {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Point> pointCollection = sqlContext.select("SELECT * from cd_points as p where p.fn_route = ? order by p.n_order desc limit 1", new String[] { f_route }, Point.class);

        if(pointCollection != null && !pointCollection.isEmpty()) {
            return pointCollection.toArray(new Point[0])[0];
        }

        return null;
    }

    /**
     * Получен точек по маршруту
     * @param f_route идентификатор маршрута
     * @param search поисковый запрос
     * @return результат выборки
     */
    @Nullable
    public PointItem[] getPoints(@NotNull String f_route, @Nullable String search) {

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<PointItem> collection;

        String query = "SELECT \n" +
                "\tp.ID, \n" +
                "\tp.C_DESCRIPTION, \n" +
                "\tp.JB_DATA, \n" +
                "\tp.C_ADDRESS, \n" +
                "\t(select count(*) from cd_results as rr where rr.fn_point = p.id and rr.b_disabled = 0) > 0 as B_DONE, \n" +
                "\tp.B_ANOMALY, \n" +
                "\tp.B_CHECK, \n" +
                "\tp.B_SERVER \n" +
                "from cd_points as p where p.fn_route = ?" + (TextUtils.isEmpty(search) ? "" : " and p.c_address like '%' || ? || '%'") + " " +
                "order by p.n_order";

        if(TextUtils.isEmpty(search)) {
            collection = sqlContext.select(query, new String[] { f_route }, PointItem.class);
        } else {
            collection = sqlContext.select(query, new String[] { f_route, search }, PointItem.class);
        }
        if(collection != null) {
            return collection.toArray(new PointItem[0]);
        } else {
            return null;
        }
    }

    /**
     * Получение информации по точке
     * @param f_point идентифкатор точки
     * @return результат
     */
    @Nullable
    public PointInfo[] getPointInfo(@NotNull String f_point) {

        Point point = getPoint(f_point);
        if(point != null) {
            List<PointInfo> items = new ArrayList<>();
            items.add(new PointInfo(mContext, mContext.getString(R.string.address), point.c_address));
            items.add(new PointInfo(mContext, mContext.getString(R.string.description), point.c_description));


            WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
            Collection<ResultTemplate> collection =  sqlContext.select("select \n" +
                    "\tt.id as F_TEMPLATE, \n" +
                    "\tt.C_NAME as C_TEMPLATE, \n" +
                    "\tt.C_TEMPLATE as C_CONST, \n" +
                    "\tr.id as F_RESULT, \n" +
                    "\tr.d_date as D_DATE, \n" +
                    "\tr.b_server as B_SERVER \n" +
                    "from cd_results as r \n" +
                    "left join cd_templates as t on r.fn_template = t.id \n" +
                    "where r.b_disabled = 0 and r.fn_point = ? and r.id is not null", new String[] { f_point }, ResultTemplate.class);

            if(collection != null && !collection.isEmpty()) {
                ResultTemplate[] array = collection.toArray(new ResultTemplate[0]);

                Arrays.sort(array, (entry1, entry2) -> {
                    Long time1 = entry1.d_date.getTime();
                    Long time2 = entry2.d_date.getTime();
                    return time2.compareTo(time1);
                });

                for (ResultTemplate resultTemplate : array) {
                    if(resultTemplate.isExistsResult()) {
                        
                        PointInfo pointInfo = new PointInfo(mContext, DateUtil.toDateTimeString(resultTemplate.d_date), MessageFormat.format("{0} - {1}", mContext.getString(R.string.done), resultTemplate.c_template));
                        pointInfo.result = resultTemplate.f_result;
                        pointInfo.server = resultTemplate.b_server;
                        items.add(pointInfo);
                    }
                }
            }

            return items.toArray(new PointInfo[0]);
        }

        return null;
    }

    /**
     * Получен результата по точке
     * @param f_point идентификатор точки
     * @return результат по точке
     */
    @Nullable
    public Result[] getResults(@NotNull String f_point) {

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Result> collection = sqlContext.select("SELECT * from cd_results as r where r.fn_point = ? and r.b_disabled = 0", new String[] { f_point }, Result.class);

        if(collection != null) {
            Result[] array = collection.toArray(new Result[0]);

            Arrays.sort(array, (entry1, entry2) -> {
                Long time1 = entry1.d_date.getTime();
                Long time2 = entry2.d_date.getTime();
                return time2.compareTo(time1);
            });

            return array;
        } else {
            return null;
        }
    }

    /**
     * Получение списка шаблонов для точки маршрутов
     * @param f_route иден. маршрута
     * @param f_point идентификатор точки
     * @return списко шаблонов
     */
    @Nullable
    public ResultTemplate[] getResultTemplates(@NotNull String f_route, @NotNull String f_point) {

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);

        Collection<Route> routeCollection = sqlContext.select("select * from cd_routes as r where r.id = ?", new String[] { f_route }, Route.class);

        if(routeCollection != null && !routeCollection.isEmpty()) {
            Route route = routeCollection.toArray(new Route[0])[0];
            String[] templates = StringUtil.isEmptyOrNull(route.c_templates) ? new String[0] : route.c_templates.split(",");

            Collection<ResultTemplate> collection = sqlContext.select("select \n" +
                    "\tt.id as F_TEMPLATE, \n" +
                    "\tt.C_NAME as C_TEMPLATE, \n" +
                    "\tt.C_TEMPLATE as C_CONST, \n" +
                    "\t(select r.id from cd_results as r where r.fn_point = ? and r.fn_template = t.id and r.B_DISABLED = 0) as F_RESULT, \n" +
                    "\t(select r.D_DATE from cd_results as r where r.fn_point = ? and r.fn_template = t.id and r.B_DISABLED = 0) as D_DATE, \n" +
                    "\t(select r.B_SERVER from cd_results as r where r.fn_point = ? and r.fn_template = t.id and r.B_DISABLED = 0) as B_SERVER \n" +
                    "from cd_templates as t " +
                    "order by t.n_order", new String[] { f_point, f_point, f_point }, ResultTemplate.class);

            if(collection != null && !collection.isEmpty()) {
                List<ResultTemplate> resultTemplates = new ArrayList<>();
                for (ResultTemplate resultTemplate: collection) {
                    for (String s: templates) {
                        if(resultTemplate.c_const.equals(s)) {
                            resultTemplates.add(resultTemplate);
                        }
                    }
                }

                return resultTemplates.toArray(new ResultTemplate[0]);
            }
        }

        return null;
    }

    @Nullable
    public Template[] getTemplates() {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Template> resultCollection = sqlContext.select("SELECT * from cd_templates as t order by t.n_order", null, Template.class);

        if(resultCollection != null && resultCollection.size() > 0) {
            return resultCollection.toArray(new Template[0]);
        }

        return null;
    }

    /**
     * Результат по точки
     * @param f_result иден. точки
     * @return результат
     */
    @Nullable
    public Result getResult(@Nullable String f_result) {

        if(f_result == null) {
            return null;
        }

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Result> resultCollection = sqlContext.select("SELECT * from cd_results as r where r.id = ? and r.b_disabled = 0", new String[] { f_result }, Result.class);

        if(resultCollection != null && resultCollection.size() > 0) {
            return resultCollection.toArray(new Result[0])[0];
        }

        return null;
    }

    /**
     * получение точки
     * @param f_point иден. точки
     * @return точка
     */
    @Nullable
    public Point getPoint(@Nullable String f_point) {
        if(f_point == null) {
            return null;
        }

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Point> pointCollection = sqlContext.select("SELECT * from cd_points as p where p.id = ?", new String[] { f_point }, Point.class);

        if(pointCollection!= null && !pointCollection.isEmpty()) {
            return pointCollection.toArray(new Point[0])[0];
        }

        return null;
    }

    @Nullable
    public Attachment[] getAttachments(@Nullable String f_result) {
        if(f_result == null) {
            return null;
        }

        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);

        Collection<Attachment> collection = sqlContext.select("SELECT * from attachments as a where a.fn_result = ?", new String[] { f_result }, Attachment.class);
        if(collection != null) {
            Attachment[] array = collection.toArray(new Attachment[0]);

            Arrays.sort(array, (entry1, entry2) -> {
                Long time1 = entry1.d_date.getTime();
                Long time2 = entry2.d_date.getTime();
                return time1.compareTo(time2);
            });
            return array;
        } else {
            return null;
        }
    }

    /**
     * Обновление вложений
     * @param f_result идентификатор результата
     * @param attachments вложения
     * @return true - добавление прошло успешно
     */
    public boolean updateAttachments(@NotNull String f_result, @NotNull Attachment[] attachments) {
        // тут вначале удалем все вложения с f_result
        // потом сожаем текущие в БД
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);

        for (Attachment attachment : attachments) {
            attachment.fn_result = f_result;
        }

        return sqlContext.exec("delete from attachments where fn_result = ?;", new String[] { f_result }) &&
                sqlContext.insertMany(attachments);
    }

    public boolean delPoint(@NotNull String f_point) throws FileNotFoundException {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Attachment> collection = sqlContext.select("select * from attachments where fn_point = ?;", new String[] { f_point }, Attachment.class);

        if(collection != null) {
            Attachment[] array = collection.toArray(new Attachment[0]);
            if(array.length > 0) {
                SimpleFileManager fileManager = new SimpleFileManager(
                        mContext.getFilesDir(),
                        BasicAuthorizationSingleton.getInstance().getUser().getCredential());
                for (Attachment item: array) {
                    fileManager.deleteFile(item.c_name);
                }
            }
        }

        if(sqlContext.exec("delete from attachments where fn_point = ?;", new String[] { f_point })) {
            if(sqlContext.exec("delete from cd_results where fn_point = ?;", new String[] { f_point })) {
                return sqlContext.exec("delete from cd_points where id = ?;", new String[] { f_point });
            }
        }

        return false;
    }

    public boolean disabledPoint(@NotNull String f_point) {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        return sqlContext.exec("" +
                "update cd_points " +
                "set b_disabled = ?, " + FieldNames.IS_SYNCHRONIZATION + " = 0, " + FieldNames.OBJECT_OPERATION_TYPE + " = ? " +
                "where id = ?", new Object[] { true, DbOperationType.UPDATED, f_point });
    }

    public boolean delResult(@NotNull String f_result) {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Attachment> collection = sqlContext.select("select * from attachments where fn_result = ?;", new String[] { f_result }, Attachment.class);

        if(collection != null) {
            Attachment[] array = collection.toArray(new Attachment[0]);
            if(array.length > 0) {
                SimpleFileManager fileManager = new SimpleFileManager(mContext.getFilesDir(), BasicAuthorizationSingleton.getInstance().getUser().getCredential());
                for (Attachment item: array) {
                    fileManager.deleteFile(item.c_name);
                }
            }
        }

        if(sqlContext.exec("delete from attachments where fn_result = ?;", new String[] { f_result })) {
            return sqlContext.exec("delete from cd_results where id = ?;", new String[] { f_result });
        }

        return false;
    }

    public boolean disabledResult(@NotNull String f_result) {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        return sqlContext.exec("" +
                "update cd_results " +
                "set b_disabled = ?, " + FieldNames.IS_SYNCHRONIZATION + " = 0, " + FieldNames.OBJECT_OPERATION_TYPE + " = ? " +
                "where id = ?", new Object[] { true, DbOperationType.UPDATED, f_result });
    }

    /**
     * Добавление результата
     * @param item результат
     * @return true - результат вставки
     */
    public boolean addResult(@NotNull Result item) {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        return sqlContext.insertMany(new Result[] { item });
    }

    /**
     * Получение шаблона для результата
     * @param c_template имя шаблона
     * @return шаблон
     */
    @Nullable
    public Template getTemplate(@NotNull String c_template) {
        WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
        Collection<Template> resultCollection = sqlContext.select("SELECT * from cd_templates as t where t.c_template = ?", new String[] { c_template }, Template.class);

        if(resultCollection != null && resultCollection.size() > 0) {
            return resultCollection.toArray(new Template[0])[0];
        }

        return null;
    }

    /**
     * Обновление точки
     * @param f_point иден. точки
     * @param isCheck подтверждение
     * @param comment комментарий
     * @return true - результат обновления
     */
    public boolean updatePoint(@Nullable String f_point, boolean isCheck, String comment) {
        if(f_point == null) {
            return false;
        }
        Point point = getPoint(f_point);
        if(point != null) {
            point.b_check = isCheck;
            point.c_comment = comment;

            WalkerSQLContext sqlContext = WalkerApplication.getWalkerSQLContext(mContext);
            return sqlContext.insertMany(new Point[] { point });
        }

        return true;
    }
}

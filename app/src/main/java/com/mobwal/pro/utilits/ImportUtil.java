package com.mobwal.pro.utilits;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.util.DateUtil;
import com.mobwal.pro.DataManager;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.WalkerSQLContext;
import com.mobwal.pro.models.db.complex.PointItem;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.models.db.Setting;
import com.mobwal.pro.models.db.Template;

public class ImportUtil {

    /**
     * Преобразование массива данных из CSV в шаблоны
     * @param rows массив данных
     * @param f_route иден. маршрута
     * @return массив шаблонов для сохранения в БД
     */
    public static Template[] convertRowsToTemplates(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<Template> templates = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            if (row.length >= 2) {
                Template template = new Template();
                template.c_name = row[0];
                template.c_template = row[1];
                //template.f_route = f_route;
                template.n_order = i + 1;

                templates.add(template);
            }
        }

        return templates.toArray(new Template[0]);
    }

    /**
     * Преобразование массива данных из CSV в настройки
     * @param rows массив данных
     * @param f_route иден. маршрута
     * @return массив настроек для сохранения в БД
     */
    @Nullable
    public static Setting[] convertRowsToSettings(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<Setting> settings = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 2) {
                Setting setting = new Setting();
                setting.c_key = row[0].toLowerCase();
                setting.c_value = row[1].toLowerCase();
                //setting.f_route = f_route;

                settings.add(setting);
            }
        }

        return settings.toArray(new Setting[0]);
    }

    @Nullable
    public static PointItem[] convertPointsToPointItems(@Nullable Point[] points) {
        if(points == null) {
            return null;
        }

        PointItem[] items = new PointItem[points.length];
        for(int i = 0; i < points.length; i++) {
            PointItem pointItem = new PointItem(points[i]);
            items[i] = pointItem;
        }
        return items;
    }

    /**
     * Преобразование строки с данными в массив с точками
     * @param rows массив данных для обработки
     * @param f_route идентификатор маршрута
     * @return массив точек
     */
    @Nullable
    public static Point[] convertRowsToPoints(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<Point> points = new ArrayList<>();

        for(int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            if (row.length > 0) {
                Point point = convertRowToPoint(row);
                if(point == null) {
                    continue;
                }

                //point.f_route = f_route;
                point.n_order = i + 1;

                if (row.length > 5) {
                    point.jb_data = getPointData(row,5);
                }

                points.add(point);
            }
        }

        return points.toArray(new Point[0]);
    }

    /**
     * Преобразование массива данных из CSV в точки маршрута
     * @param rows массив данных для обработки
     * @param f_route иден. маршрута
     * @return массив точек
     */
    @Nullable
    public static Point[] convertRowsToPointsFromResults(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<Point> points = new ArrayList<>();

        for(int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            if (row.length > 0) {
                Point point = convertRowToPoint(row);
                if(point == null) {
                    continue;
                }

                if (row.length > 5) {
                    point.b_anomaly = row[5].equalsIgnoreCase("true");
                }

                if (row.length > 6) {
                    point.b_check = row[6].equalsIgnoreCase("true");
                }

                if (row.length > 7) {
                    point.c_comment = row[7];
                }

                //point.f_route = f_route;
                point.n_order = i + 1;

                if (row.length > 7) {
                    point.jb_data = getPointData(row,7);
                }

                points.add(point);
            }
        }

        return points.toArray(new Point[0]);
    }

    @Nullable
    private static Point convertRowToPoint(@NotNull String[] row) {
        if (row.length > 0) {
            if (row[0].equals("")) {
                return null;
            }

            Double n_latitude = null;
            if (row.length > 1) {
                try {
                    n_latitude = Double.valueOf(row[1]);
                } catch (NumberFormatException ignored) {

                }
            }

            Double n_longitude = null;
            if (row.length > 2) {
                try {
                    n_longitude = Double.valueOf(row[2]);
                } catch (NumberFormatException ignored) {

                }
            }

            Point point = new Point();
            point.c_address = row[0];
            point.n_latitude = n_latitude == null ? 0.0 : n_latitude;
            point.n_longitude = n_longitude == null ? 0.0 : n_longitude;

            if (row.length > 3) {
                point.c_description = row[3];
            }

            if (row.length > 4) {
                //point.c_imp_id = row[4];
            }

            return point;
        }

        return null;
    }

    /**
     * чтение дополнительных данных для точек маршрута
     * @param data массив данных
     * @param fromIdx иден. с которого требуется начать просмотр
     * @return строка в формате JSON
     */
    @Nullable
    public static String getPointData(String[] data, int fromIdx) {
        if (data.length > fromIdx) {

            JsonObject jsonObject = new JsonObject();

            for (int i = 0; i < data.length; i++) {
                int idx = i + fromIdx;
                if (idx >= data.length) {
                    break;
                }

                if (data[idx].equals("false") || data[idx].equals("true")) {
                    jsonObject.addProperty("{" + idx + "}", data[idx].equals("true"));
                } else {
                    jsonObject.addProperty("{" + idx + "}", data[idx]);
                }
            }

            return jsonObject.toString();
        }

        return null;
    }

    /**
     * Создание маршрута из текстового файла
     * @param context контекст
     * @param reader читатель
     * @param routeName имя маршрута
     * @return результат обработки, если она пустая, то ошибок нет
     */
    @Nullable
    public static String generateRouteFormCsv(@NotNull Context context, @NotNull CsvReader reader, @NotNull String routeName) {
        Route route = new Route();
        route.c_name = routeName;

        Point[] points = ImportUtil.convertRowsToPoints(reader.getRows(), route.id);
        WalkerSQLContext db = WalkerApplication.getWalkerSQLContext(context);

        if(db.insertMany(new Route[] { route })) {
            if(points != null && db.insertMany(points)) {
                Template template = new Template();
                template.setDefault(context, route.id);
                if(db.insertMany(new Template[] { template })) {
                    return null;
                }
            }
        }

        DataManager dataManager = new DataManager(context);
        if(!dataManager.delRoute(route.id)) {
            return context.getString(R.string.import_error1);
        }

        return context.getString(R.string.import_error2);
    }
}

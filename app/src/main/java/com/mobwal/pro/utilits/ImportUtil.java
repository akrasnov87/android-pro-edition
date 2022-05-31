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

import com.mobwal.pro.DataManager;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.WalkerSQLContext;
import com.mobwal.pro.models.DemoPlaceItem;
import com.mobwal.pro.models.db.complex.PointItem;
import com.mobwal.pro.models.db.attachments;
import com.mobwal.pro.models.db.cd_points;
import com.mobwal.pro.models.db.cd_results;
import com.mobwal.pro.models.db.cd_routes;
import com.mobwal.pro.models.db.cd_settings;
import com.mobwal.pro.models.db.cd_templates;

public class ImportUtil {

    /**
     * Преобразование массива данных из CSV в шаблоны
     * @param rows массив данных
     * @param f_route иден. маршрута
     * @return массив шаблонов для сохранения в БД
     */
    public static cd_templates[] convertRowsToTemplates(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<cd_templates> templates = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            if (row.length >= 2) {
                cd_templates template = new cd_templates();
                template.c_name = row[0];
                template.c_template = row[1];
                //template.f_route = f_route;
                template.n_order = i + 1;

                templates.add(template);
            }
        }

        return templates.toArray(new cd_templates[0]);
    }

    /**
     * Преобразование массива данных из CSV в настройки
     * @param rows массив данных
     * @param f_route иден. маршрута
     * @return массив настроек для сохранения в БД
     */
    @Nullable
    public static cd_settings[] convertRowsToSettings(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<cd_settings> settings = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 2) {
                cd_settings setting = new cd_settings();
                setting.c_key = row[0].toLowerCase();
                setting.c_value = row[1].toLowerCase();
                setting.f_route = f_route;

                settings.add(setting);
            }
        }

        return settings.toArray(new cd_settings[0]);
    }

    @Nullable
    public static PointItem[] convertPointsToPointItems(@Nullable cd_points[] points) {
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
    public static cd_points[] convertRowsToPoints(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<cd_points> points = new ArrayList<>();

        for(int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            if (row.length > 0) {
                cd_points point = convertRowToPoint(row);
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

        return points.toArray(new cd_points[0]);
    }

    /**
     * Преобразование массива данных из CSV в точки маршрута
     * @param rows массив данных для обработки
     * @param f_route иден. маршрута
     * @return массив точек
     */
    @Nullable
    public static cd_points[] convertRowsToPointsFromResults(@Nullable String[][] rows, @NotNull String f_route) {
        if(rows == null) {
            return null;
        }

        List<cd_points> points = new ArrayList<>();

        for(int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            if (row.length > 0) {
                cd_points point = convertRowToPoint(row);
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

        return points.toArray(new cd_points[0]);
    }

    @Nullable
    private static cd_points convertRowToPoint(@NotNull String[] row) {
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

            cd_points point = new cd_points();
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
        cd_routes route = new cd_routes();
        route.c_name = routeName;

        cd_points[] points = ImportUtil.convertRowsToPoints(reader.getRows(), route.id);
        WalkerSQLContext db = WalkerApplication.getWalkerSQLContext(context);

        if(db.insertMany(new cd_routes[] { route })) {
            if(points != null && db.insertMany(points)) {
                cd_templates template = new cd_templates();
                template.setDefault(context, route.id);
                if(db.insertMany(new cd_templates[] { template })) {
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

    /**
     * Создание демонстрационного маршрута
     * @param context контекст
     * @param places точки
     * @param routeName имя маршрута
     * @return данные по маршруту
     */
    @Nullable
    public static String generateRouteFormDemo(@NotNull Context context, @NotNull DemoPlaceItem[] places, @NotNull String routeName) {

        if(places.length == 0) {
            return context.getString(R.string.demo_route_empty);
        }

        cd_routes route = new cd_routes();
        //route.c_catalog = "demo";
        route.c_name = routeName;
        //route.b_export = true;
        //route.d_export = new Date();
        //route.c_readme = MessageFormat.format(context.getString(R.string.create_route_docs), Names.ROUTE_DOCS);

        List<cd_points> list = new ArrayList<>();
        int i = 0;
        for (DemoPlaceItem item: places) {
            cd_points point = new cd_points();
            //point.f_route = route.id;
            point.c_address = item.name;
            //point.c_description = item.kinds;
            point.n_latitude = item.latitude;
            point.n_longitude = item.longitude;
            point.n_order = ++i;
            list.add(point);
        }

        cd_points[] points = list.toArray(new cd_points[0]);
        WalkerSQLContext db = WalkerApplication.getWalkerSQLContext(context);

        if(db.insertMany(new cd_routes[] { route })) {
            if(db.insertMany(points)) {
                cd_templates template = new cd_templates();
                template.setDefault(context, route.id);
                template.n_order = 1;

                cd_templates template2 = new cd_templates();
                template2.setDemo(context, route.id);
                template2.n_order = 2;

                if(db.insertMany(new cd_templates[] { template, template2 })) {
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

    /**
     * Создание маршрута из zip - файла
     * @param context контекст
     * @param reader читатель
     * @param routeName имя маршрута
     * @param catalog каталог
     * @return результат обработки, если она пустая, то ошибок нет
     */
    @Nullable
    public static String generateRouteFromZip(@NotNull Context context, @NotNull ZipReader reader, @NotNull String routeName, @NotNull String catalog) {
        cd_routes route = new cd_routes();
        route.c_name = routeName;
        //route.c_catalog = catalog;
        route.b_check = reader.isCheckMode();

        String routeID = reader.getId(route.b_check);
        DataManager dataManager = new DataManager(context);
        if(!TextUtils.isEmpty(routeID)) {
            dataManager.delRoute(Objects.requireNonNull(routeID));
            route.id = routeID;
        }

        String readme = reader.getReadme(route.b_check);
        if(!TextUtils.isEmpty(readme)) {
            //route.c_readme = readme;
        }

        String[][] pointRows = reader.getPoints(route.b_check);
        if(pointRows != null) {
            cd_points[] points = route.b_check ? ImportUtil.convertRowsToPointsFromResults(pointRows, route.id)
                                            : ImportUtil.convertRowsToPoints(pointRows, route.id);
            WalkerSQLContext db = WalkerApplication.getWalkerSQLContext(context);

            if(db.insertMany(new cd_routes[] { route })) {
                if(points != null && db.insertMany(points)) {
                    cd_settings[] settings = ImportUtil.convertRowsToSettings(reader.getSettings(route.b_check), route.id);
                    // загрузка настроек
                    if(settings != null) {
                        db.insertMany(settings);
                    }

                    // поиск шаблонов
                    cd_templates[] templates = ImportUtil.convertRowsToTemplates(reader.getTags(route.b_check), route.id);
                    if(templates != null && templates.length > 0) {

                        for (cd_templates temp : templates) {
                            temp.c_layout = reader.getForm(temp.c_template, route.b_check);
                        }

                        db.insertMany(templates);
                    } else {
                        cd_templates template = new cd_templates();
                        template.setDefault(context, route.id);

                        db.insertMany(new cd_templates[] { template });
                    }

                    // загружаем результаты
                    if(route.b_check) {
                        FileManager fileManager = new FileManager(context.getFilesDir());
                        if(!fileManager.getRootCatalog(route.id).mkdirs()) {
                            return context.getString(R.string.unknown_error) + "ZIP4";
                        }

                        String[][] attachmentRows = reader.getArrayFromCSV("attachments.csv");
                        if(attachmentRows != null && attachmentRows.length > 0) {

                            List<attachments> attachmentItems = new ArrayList<>();
                            List<cd_results> results = new ArrayList<>();

                            Collection<cd_templates> templateCollection = db.select("select * from TEMPLATE as t where t.f_route = ?;", new String[] { route.id }, cd_templates.class);
                            if(templateCollection != null) {
                                cd_templates[] dbTemplates = templateCollection.toArray(new cd_templates[0]);

                                for (cd_templates template : dbTemplates) {
                                    String[][] rows = reader.getArrayFromCSV(template.c_template + ".csv");
                                    if(rows != null) {
                                        for (int i = 1; i < rows.length; i++) {
                                            String[] f_result_id = rows[i][0].split("-");

                                            if (f_result_id.length > 0) {
                                                int pointOrder = Integer.parseInt(f_result_id[0]);
                                                List<cd_points> pointOrderFilter = new ArrayList<>();

                                                for (cd_points point : points) {
                                                    if (point.n_order == pointOrder) {
                                                        pointOrderFilter.add(point);
                                                    }
                                                }

                                                if (pointOrderFilter.size() == 0) {
                                                    continue;
                                                }

                                                cd_results result = new cd_results();
                                                //result.f_route = route.id;
                                                //result.f_point = pointOrderFilter.get(0).id;
                                                //result.c_template = template.c_template;
                                                result.d_date = DateUtil.convertStringToSystemDate(rows[i][7]);
                                                if (result.d_date == null) {
                                                    continue;
                                                }
                                                //result.n_date = result.d_date.getTime();

                                                result.n_distance = TextUtils.isEmpty(rows[i][10]) ? -1 : Double.parseDouble(rows[i][10]);
                                                result.n_latitude = TextUtils.isEmpty(rows[i][8]) ? 0.0 : Double.parseDouble(rows[i][8]);
                                                result.n_longitude = TextUtils.isEmpty(rows[i][9]) ? 0.0 : Double.parseDouble(rows[i][9]);

                                                //14
                                                JsonObject dict = new JsonObject();
                                                for (int j = 14; j < rows[i].length; j++) {
                                                    if (rows[i][j].equals("false") || rows[i][j].equals("true")) {
                                                        dict.addProperty(rows[0][j], rows[i][j].equals("true"));
                                                    } else {
                                                        dict.addProperty(rows[0][j], rows[i][j]);
                                                    }
                                                }

                                                result.jb_data = dict.toString();

                                                // тут будем искать вложения
                                                List<String[]> atts = new ArrayList<>();
                                                for (String[] row : attachmentRows) {
                                                    if (row[0].equals(rows[i][0])) {
                                                        atts.add(row);
                                                    }
                                                }

                                                if (atts.size() > 0) {
                                                    // значит есть вложения
                                                    for (int f = 0; f < atts.size(); f++) {
                                                        String[] attItem = atts.get(f);

                                                        File attUrl = reader.getAttachmentUrl(attItem[1]);
                                                        if (attUrl != null) {
                                                            fileManager.copy(attUrl, new File(fileManager.getRootCatalog(route.id), attItem[1]));

                                                            attachments attachmentItem = new attachments();
                                                            //attachmentItem.fn_point = result.f_point;
                                                            //attachmentItem.fn_route = route.id;
                                                            //attachmentItem.fn_result = result.id;
                                                            //attachmentItem.c_name = attItem[1];

                                                            attachmentItem.n_latitude = TextUtils.isEmpty(attItem[2]) ? 0.0 : Double.parseDouble(attItem[2]);
                                                            attachmentItem.n_longitude = TextUtils.isEmpty(attItem[3]) ? 0.0 : Double.parseDouble(attItem[3]);
                                                            attachmentItem.d_date = DateUtil.convertStringToSystemDate(attItem[4]);
                                                            if (attachmentItem.d_date == null) {
                                                                continue;
                                                            }
                                                            //attachmentItem.n_distance = TextUtils.isEmpty(attItem[5]) ? -1 : Double.parseDouble(attItem[5]);

                                                            attachmentItems.add(attachmentItem);
                                                        }
                                                    }
                                                }

                                                results.add(result);
                                            }
                                        }
                                    }
                                }
                            }

                            if(results.size() > 0 && !db.insertMany(results.toArray(new cd_results[0]))) {
                                return context.getString(R.string.unknown_error) + "ZIP3";
                            }

                            if(attachmentItems.size() > 0 && !db.insertMany(attachmentItems.toArray(new attachments[0]))) {
                                return context.getString(R.string.unknown_error) + "ZIP2";
                            }

                            return null;
                        }
                    }

                    return null;
                }
            }
        }

        if(!dataManager.delRoute(route.id)) {
            return context.getString(R.string.unknown_error) + "ZIP1";
        }

        return context.getString(R.string.unknown_error) + "ZIP0";
    }
}

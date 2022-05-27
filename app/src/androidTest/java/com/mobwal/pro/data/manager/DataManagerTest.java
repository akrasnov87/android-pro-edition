/*package ru.mobnius.cic.data.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.data.storage.models.PointTypes;
import ru.mobnius.cic.data.storage.models.WorkLinks;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.LocationUtil;
import ru.mobnius.cic.ManagerGenerate;
import ru.mobnius.cic.data.storage.models.Attachments;
import ru.mobnius.cic.data.storage.models.Files;
import ru.mobnius.cic.data.storage.models.Points;
import ru.mobnius.cic.data.storage.models.ResultTypes;
import ru.mobnius.cic.data.storage.models.Results;
import ru.mobnius.cic.data.storage.models.RouteHistory;
import ru.mobnius.cic.data.storage.models.RouteStatuses;
import ru.mobnius.cic.data.storage.models.RouteTypes;
import ru.mobnius.cic.data.storage.models.Routes;
import ru.mobnius.cic.data.storage.models.UserPoints;
import ru.mobnius.cic.ui.model.PointFilter;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.PointResult;

import ru.mobnius.cic.ui.model.RouteInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DataManagerTest extends ManagerGenerate {
    private DataManager dataManager;
    private FileManager fileManager;

    @Before
    public void setUp() {
        dataManager = DataManager.createInstance(getDaoSession());
        BasicCredentials credentials = new BasicCredentials("inspector", "");
        fileManager = FileManager.createInstance(credentials, getContext());

        RouteTypes routeType = new RouteTypes();
        routeType.id = (long)1;
        routeType.c_name = "тип";
        getDaoSession().getRouteTypesDao().insertOrReplace(routeType);
    }

    @Test
    public void saveAttachment() throws IOException {
        String str = "Hello";
        byte[] bytes = str.getBytes();

        Attachments file = dataManager.saveAttachment("test.txt", 1, "", "", UUID.randomUUID().toString(), "", LocationUtil.getLocation(0, 0), bytes);

        Attachments dbFile = getDaoSession().getAttachmentsDao().load(file.id);
        assertEquals(file.c_name, dbFile.c_name);

        byte[] fileBytes = dataManager.getFileAttachment(file.id);

        assertEquals(new String(fileBytes), str);

        Attachments attachments = dataManager.getAttachment(file.id);
        assertNotNull(attachments);

        String fileId = attachments.fn_file;
        dataManager.removeAttachment(attachments.id);

        assertNull(dataManager.getAttachment(attachments.id));
        assertNull(dataManager.getFileBytes(fileId));
        assertNull(FileManager.getInstance().readPath(attachments.folder, attachments.c_name));
    }

    @Test
    public void updateAttachment() throws IOException {
        String str = "Hello";
        byte[] bytes = str.getBytes();

        Attachments file = dataManager.saveAttachment("test.txt", 1, "", "", UUID.randomUUID().toString(), "", LocationUtil.getLocation(0, 0), bytes);
        assertEquals(file.fn_type, 1);
        assertEquals(file.c_notice, "");

        Attachments attachments = dataManager.updateAttachment(file.id, 2, "notice");
        assertNotNull(attachments);

        assertEquals(attachments.c_notice, "notice");
        assertEquals(attachments.fn_type, 2);
    }

    @Test
    public void getCountDonePoints() {
        getDaoSession().getRoutesDao().deleteAll();
        getDaoSession().getPointsDao().deleteAll();
        getDaoSession().getUserPointsDao().deleteAll();

        Routes routes = new Routes();
        routes.id = UUID.randomUUID().toString();
        routes.c_number = "1";
        routes.d_date = DateUtil.convertDateToString(new Date());
        routes.f_type = 1;
        getDaoSession().getRoutesDao().insert(routes);

        Points points = new Points();
        points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        points.b_anomaly = false;
        getDaoSession().getPointsDao().insert(points);

        UserPoints userPoints = new UserPoints();
        userPoints.id = UUID.randomUUID().toString();
        userPoints.fn_user = 4;
        userPoints.fn_type = 1;
        userPoints.fn_point = points.id;
        userPoints.fn_route = routes.id;
        getDaoSession().getUserPointsDao().insert(userPoints);

        points = new Points();
        points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        points.b_anomaly = false;
        getDaoSession().getPointsDao().insert(points);

        userPoints = new UserPoints();
        userPoints.id = UUID.randomUUID().toString();
        userPoints.fn_user = 4;
        userPoints.fn_type = 1;
        userPoints.fn_point = points.id;
        userPoints.fn_route = routes.id;
        getDaoSession().getUserPointsDao().insert(userPoints);

        points = new Points();
        points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        points.b_anomaly = false;
        getDaoSession().getPointsDao().insert(points);

        PointTypes types = new PointTypes();
        types.id = 0L;
        types.c_name = "name";
        getDaoSession().getPointTypesDao().insertOrReplace(types);

        assertEquals(dataManager.getCountDonePoints(routes.id), 2);

        assertEquals(dataManager.getPoints(routes.id, PointFilter.ALL).size(), 3);
        assertEquals(dataManager.getPoints(routes.id, PointFilter.DONE).size(), 2);
        assertEquals(dataManager.getPoints(routes.id, PointFilter.UNDONE).size(), 1);
        assertEquals(dataManager.getPoints(routes.id, PointFilter.UNDONE).get(0).id, points.id);

        assertEquals(dataManager.getPointItems(routes.id, PointFilter.ALL).size(), 3);
        assertEquals(dataManager.getPointItems(routes.id, PointFilter.DONE).size(), 2);
        assertEquals(dataManager.getPointItems(routes.id, PointFilter.UNDONE).size(), 1);
        assertEquals(dataManager.getPointItems(routes.id, PointFilter.UNDONE).get(0).id, points.id);
    }

    @Test
    public void getRoutes() {
        getDaoSession().getRoutesDao().deleteAll();

        Routes route = new Routes();
        route.id = UUID.randomUUID().toString();
        route.c_number = "1";
        route.d_date = DateUtil.convertDateToString(new Date());
        route.d_date_start = DateUtil.convertDateToString(new GregorianCalendar(2019, 11, 16).getTime());
        route.d_date_end = DateUtil.convertDateToString(new GregorianCalendar(2019, 11, 24).getTime());
        route.f_type = 1;

        getDaoSession().getRoutesDao().insert(route);

        route = new Routes();
        route.id = UUID.randomUUID().toString();
        route.c_number = "2";
        route.d_date = DateUtil.convertDateToString(new Date());
        route.d_date_start = DateUtil.convertDateToString(new GregorianCalendar(3000, 11, 16).getTime());
        route.d_date_end = DateUtil.convertDateToString(new GregorianCalendar(3000, 11, 24).getTime());
        route.f_type = 1;

        getDaoSession().getRoutesDao().insert(route);

        route = new Routes();
        route.id = UUID.randomUUID().toString();
        route.c_number = "3";
        route.d_date = DateUtil.convertDateToString(new Date());
        Calendar cal = Calendar.getInstance();
        route.d_date_start = DateUtil.convertDateToString(new GregorianCalendar(cal.get(Calendar.YEAR), 0, 1).getTime());
        route.d_date_end = DateUtil.convertDateToString(new GregorianCalendar(cal.get(Calendar.YEAR), 11, 31).getTime());
        route.f_type = 1;

        getDaoSession().getRoutesDao().insert(route);

        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.ALL).size(), 3);
        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.ACTIVE).size(), 1);
        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.ACTIVE).get(0).c_number, "3");
        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.PAST).size(), 1);
        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.PAST).get(0).c_number, "1");
        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.FUTURE).size(), 1);
        assertEquals(dataManager.getRoutes(DataManager.RouteFilter.FUTURE).get(0).c_number, "2");

        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.ALL).size(), 3);
        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.ACTIVE).size(), 1);
        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.ACTIVE).get(0).number, "3");
        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.PAST).size(), 1);
        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.PAST).get(0).number, "1");
        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.FUTURE).size(), 1);
        assertEquals(dataManager.getRouteItems(DataManager.RouteFilter.FUTURE).get(0).number, "2");
    }

    @Test
    public void getPointState() {
        getDaoSession().getRoutesDao().deleteAll();
        getDaoSession().getPointsDao().deleteAll();
        getDaoSession().getUserPointsDao().deleteAll();
        getDaoSession().getResultsDao().deleteAll();
        getDaoSession().getAttachmentsDao().deleteAll();
        getDaoSession().getFilesDao().deleteAll();

        Routes routes = new Routes();
        routes.id = UUID.randomUUID().toString();
        routes.c_number = "1";
        routes.d_date = DateUtil.convertDateToString(new Date());
        routes.f_type = 1;
        getDaoSession().getRoutesDao().insert(routes);

        Points points = new Points();
        String firstPointId = points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        getDaoSession().getPointsDao().insert(points);

        UserPoints userPoints = new UserPoints();
        userPoints.id = UUID.randomUUID().toString();
        userPoints.fn_user = 4;
        userPoints.fn_type = 1;
        userPoints.fn_point = points.id;
        userPoints.fn_route = routes.id;
        userPoints.isSynchronization = true;
        getDaoSession().getUserPointsDao().insert(userPoints);

        Results results = new Results();
        results.id = UUID.randomUUID().toString();
        results.fn_type = 1;
        results.fn_user = 1;
        results.fn_user_point = userPoints.id;
        getDaoSession().getResultsDao().insert(results);

        Files files = new Files();
        files.id = UUID.randomUUID().toString();
        getDaoSession().getFilesDao().insert(files);

        Attachments attachments = new Attachments();
        attachments.id = UUID.randomUUID().toString();
        attachments.fn_file = files.id;
        attachments.fn_result = results.id;
        attachments.fn_type = 1;
        attachments.fn_point = points.id;
        getDaoSession().getAttachmentsDao().insert(attachments);

        points = new Points();
        String secondPointId = points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        getDaoSession().getPointsDao().insert(points);

        userPoints = new UserPoints();
        userPoints.id = UUID.randomUUID().toString();
        userPoints.fn_user = 4;
        userPoints.fn_type = 1;
        userPoints.fn_point = points.id;
        userPoints.fn_route = routes.id;
        userPoints.isSynchronization = true;
        getDaoSession().getUserPointsDao().insert(userPoints);

        results = new Results();
        results.id = UUID.randomUUID().toString();
        results.fn_type = 1;
        results.fn_user = 1;
        results.fn_user_point = userPoints.id;
        results.isSynchronization = true;
        getDaoSession().getResultsDao().insert(results);

        files = new Files();
        files.id = UUID.randomUUID().toString();
        files.isSynchronization = true;
        getDaoSession().getFilesDao().insert(files);

        attachments = new Attachments();
        attachments.id = UUID.randomUUID().toString();
        attachments.fn_file = files.id;
        attachments.fn_result = results.id;
        attachments.fn_type = 1;
        attachments.fn_point = points.id;
        attachments.isSynchronization = true;
        getDaoSession().getAttachmentsDao().insert(attachments);

        points = new Points();
        String threePointId = points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        getDaoSession().getPointsDao().insert(points);

        assertFalse(dataManager.getPointState(threePointId).isDone());
        assertFalse(dataManager.getPointState(threePointId).isSync());

        assertTrue(dataManager.getPointState(firstPointId).isDone());
        assertFalse(dataManager.getPointState(firstPointId).isSync());

        assertTrue(dataManager.getPointState(secondPointId).isDone());
        assertTrue(dataManager.getPointState(secondPointId).isSync());
    }

    @Test
    public void getPointResults() {
        getDaoSession().getResultTypesDao().deleteAll();
        getDaoSession().getRoutesDao().deleteAll();
        getDaoSession().getRouteTypesDao().deleteAll();
        getDaoSession().getPointsDao().deleteAll();
        getDaoSession().getUserPointsDao().deleteAll();
        getDaoSession().getResultsDao().deleteAll();
        getDaoSession().getPointTypesDao().deleteAll();
        getDaoSession().getWorkLinksDao().deleteAll();

        PointTypes pointTypes = new PointTypes();
        pointTypes.id = (long)1;
        pointTypes.c_name = "Документ 1";
        pointTypes.c_const = "";
        pointTypes.n_order = 900;
        getDaoSession().getPointTypesDao().insert(pointTypes);

        WorkLinks workLinks = new WorkLinks();
        workLinks.id = 1L;
        workLinks.f_point_type = 1L;
        workLinks.f_route_type = 1L;
        workLinks.f_result_type = 1L;
        getDaoSession().getWorkLinksDao().insert(workLinks);

        workLinks = new WorkLinks();
        workLinks.id = 2L;
        workLinks.f_point_type = 1L;
        workLinks.f_route_type = 1L;
        workLinks.f_result_type = 2L;
        getDaoSession().getWorkLinksDao().insert(workLinks);

        workLinks = new WorkLinks();
        workLinks.id = 3L;
        workLinks.f_point_type = 1L;
        workLinks.f_route_type = 1L;
        workLinks.f_result_type = 3L;
        getDaoSession().getWorkLinksDao().insert(workLinks);

        ResultTypes resultTypes = new ResultTypes();
        resultTypes.id = (long)1;
        resultTypes.c_name = "Документ 1";
        resultTypes.n_order = 900;
        resultTypes.c_const = Names.ANOMALY;
        getDaoSession().getResultTypesDao().insert(resultTypes);

        resultTypes = new ResultTypes();
        resultTypes.id = (long)2;
        resultTypes.c_name = "Документ 2";
        resultTypes.n_order = 1000;
        resultTypes.c_const = "Еще одна константа";
        getDaoSession().getResultTypesDao().insert(resultTypes);

        resultTypes = new ResultTypes();
        resultTypes.id = (long)3;
        resultTypes.c_name = "Документ 3";
        resultTypes.n_order = 800;
        resultTypes.c_const = "И еще одна";
        getDaoSession().getResultTypesDao().insert(resultTypes);

        RouteTypes routeType = new RouteTypes();
        routeType.id = (long)1;
        routeType.c_const = "MIXED";
        routeType.c_name = "";
        getDaoSession().getRouteTypesDao().insert(routeType);

        Routes routes = new Routes();
        routes.id = UUID.randomUUID().toString();
        routes.c_number = "1";
        routes.d_date = DateUtil.convertDateToString(new Date());
        routes.f_type = routeType.id;
        getDaoSession().getRoutesDao().insert(routes);

        Points points = new Points();
        String firstPointId = points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        points.f_type = pointTypes.id;
        getDaoSession().getPointsDao().insert(points);

        UserPoints userPoints = new UserPoints();
        userPoints.id = UUID.randomUUID().toString();
        userPoints.fn_user = 4;
        userPoints.fn_type = 1;
        userPoints.fn_point = points.id;
        userPoints.fn_route = routes.id;
        userPoints.isSynchronization = true;
        getDaoSession().getUserPointsDao().insert(userPoints);

        Results results = new Results();
        results.id = UUID.randomUUID().toString();
        results.fn_type = 1;
        results.fn_user = 1;
        results.fn_point = firstPointId;
        results.fn_route = routes.id;
        results.fn_user_point = userPoints.id;
        getDaoSession().getResultsDao().insert(results);

        points = new Points();
        String secondPointId = points.id = UUID.randomUUID().toString();
        points.f_route = routes.id;
        points.f_type = pointTypes.id;
        getDaoSession().getPointsDao().insert(points);

        List<PointResult> pointResults = dataManager.getPointDocuments(firstPointId);
        assertEquals(3, pointResults.size());
        assertEquals(pointResults.get(0).getName(), "Документ 2");
        assertTrue(pointResults.get(1).isExists());

        pointResults = dataManager.getPointDocuments(secondPointId);
        assertEquals(3, pointResults.size());
        assertFalse(pointResults.get(0).isExists());
        assertFalse(pointResults.get(1).isExists());
    }

    @Test
    public void getRouteInfo() {
        getDaoSession().getRoutesDao().deleteAll();
        getDaoSession().getRouteHistoryDao().deleteAll();
        getDaoSession().getRouteTypesDao().deleteAll();
        getDaoSession().getRouteStatusesDao().deleteAll();

        RouteStatuses routeStatuses = new RouteStatuses();
        routeStatuses.id = (long)1;
        routeStatuses.c_name = "Cоздан";
        getDaoSession().getRouteStatusesDao().insert(routeStatuses);
        routeStatuses = new RouteStatuses();
        routeStatuses.id = (long)2;
        routeStatuses.c_name = "Завершен";
        getDaoSession().getRouteStatusesDao().insert(routeStatuses);

        RouteTypes routeType = new RouteTypes();
        routeType.id = (long)1;
        routeType.c_name = "Обход";

        Routes routes = new Routes();
        routes.id = UUID.randomUUID().toString();
        routes.c_number = "1";
        routes.d_date = "2020-01-15T07:00:00.000";
        routes.d_date_start = "2020-01-15T08:00:00.000";
        routes.d_date_end = "2020-01-16T08:00:00.000";
        routes.c_notice = "Примечание к маршруту";
        routes.f_type = 1;
        getDaoSession().getRoutesDao().insert(routes);

        RouteHistory routeHistory = new RouteHistory();
        routeHistory.id = UUID.randomUUID().toString();
        routeHistory.c_notice = "Начало маршрута";
        routeHistory.fn_status = 1;
        routeHistory.fn_route = routes.id;
        routeHistory.d_date = "2020-01-15T07:00:00.000";
        getDaoSession().getRouteHistoryDao().insert(routeHistory);

        routeHistory = new RouteHistory();
        routeHistory.id = UUID.randomUUID().toString();
        routeHistory.c_notice = "Завершение маршрута";
        routeHistory.fn_status = 2;
        routeHistory.fn_route = routes.id;
        routeHistory.d_date = "2020-01-15T22:00:00.000";
        getDaoSession().getRouteHistoryDao().insert(routeHistory);

        RouteInfo routeInfo = dataManager.getRouteInfo(routes.id);
        assertNotNull(routeInfo);
        assertEquals(routeInfo.getNotice(), "Примечание к маршруту");
        assertFalse(routeInfo.isExtended());
        assertEquals(routeInfo.getHistories().length, 2);
        assertEquals(routeInfo.getHistories()[1].getStatus(), "Завершен");
    }

    @Test
    public void getPointInfo() {
        getDaoSession().getPointsDao().deleteAll();

        String c_address = "адрес";

        Points point = new Points();
        point.id = UUID.randomUUID().toString();
        point.jb_data = "{ \"c_address\": \""+c_address+"\"}";
        point.b_anomaly = false;
        getDaoSession().getPointsDao().insert(point);

        PointItem info = dataManager.getPointInfo(point.id);
        assertNotNull(info);

        assertEquals(info.address, c_address);

        info = dataManager.getPointInfo("sss");
        assertNull(info);
    }

    @Test
    public void setRouteFinish() {
        Authorization.createInstance(getContext());
        Authorization.getInstance().setUser(getBasicUser());

        getDaoSession().getRoutesDao().deleteAll();
        getDaoSession().getRouteHistoryDao().deleteAll();
        getDaoSession().getRouteStatusesDao().deleteAll();

        RouteStatuses routeStatuses = new RouteStatuses();
        routeStatuses.id = (long)1;
        routeStatuses.c_name = "Cоздан";
        routeStatuses.c_const = "CREATED";
        getDaoSession().getRouteStatusesDao().insert(routeStatuses);

        routeStatuses = new RouteStatuses();
        routeStatuses.id = (long)2;
        routeStatuses.c_name = "Завершен";
        routeStatuses.c_const = "DONED";
        getDaoSession().getRouteStatusesDao().insert(routeStatuses);

        Routes routes = new Routes();
        routes.id = UUID.randomUUID().toString();
        routes.c_number = "1";
        routes.d_date = "2020-01-15T07:00:00.000";
        routes.d_date_start = "2020-01-15T08:00:00.000";
        routes.d_date_end = "2020-01-16T08:00:00.000";
        routes.c_notice = "Примечание к маршруту";
        routes.f_type = 1;
        getDaoSession().getRoutesDao().insert(routes);

        RouteHistory routeHistory = new RouteHistory();
        routeHistory.id = UUID.randomUUID().toString();
        routeHistory.c_notice = "Начало маршрута";
        routeHistory.fn_status = 1;
        routeHistory.fn_route = routes.id;
        routeHistory.d_date = "2020-01-15T07:00:00.000";
        routeHistory.isSynchronization = false;
        getDaoSession().getRouteHistoryDao().insert(routeHistory);

        dataManager.setRouteFinish(routes.id);

        assertTrue(dataManager.isRouteFinish(routes.id));
        assertTrue(dataManager.isRevertRouteFinish(routes.id));
        dataManager.revertRouteFinish(routes.id);
        assertFalse(dataManager.isRouteFinish(routes.id));
    }

    @After
    public void tearDown() {
        getDaoSession().getRouteTypesDao().deleteAll();
        fileManager.clearUserFolder();
    }
}*/
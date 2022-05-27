/*package ru.mobnius.cic.data.manager;

import ru.mobnius.cic.ManagerGenerate;

public class DocumentManagerTest extends ManagerGenerate {/*
    private DocumentManager mDocumentManager;
    private FileManager fileManager;

    @Before
    public void setUp() {
        String routeId = UUID.randomUUID().toString();
        String pointId = UUID.randomUUID().toString();
        BasicUser basicUser = getBasicUser();
        Authorization.createInstance(getContext()).setUser(basicUser);

        mDocumentManager = new DocumentManager(getDaoSession(), routeId, pointId);
        fileManager = FileManager.createInstance(basicUser.getCredentials(), getContext());
    }

    @After
    public void tearDown() {
        getDaoSession().getResultsDao().deleteAll();
        getDaoSession().getUserPointsDao().deleteAll();
        getDaoSession().getPointTypesDao().deleteAll();
        fileManager.clearUserFolder();
    }

    @Test
    public void createResult() {
        long outputTypeId = 1;
        String userPointId = UUID.randomUUID().toString();
        String notice = "";

        String resultId = mDocumentManager.createResult(outputTypeId, userPointId, notice, null, true);
        Results result = getDaoSession().getResultsDao().load(resultId);

        assertEquals(result.fn_type, outputTypeId);
        assertEquals(result.fn_user_point, userPointId);
        assertEquals(result.c_notice, notice);
        assertTrue(result.b_warning);
        notice = "Hello";

        mDocumentManager.updateResult(resultId, notice, null, false);
        assertEquals(result.c_notice, notice);
        assertFalse(result.b_warning);
    }

    @Test
    public void createUserPoint() {
        PointTypes pointType = new PointTypes();
        pointType.id = (long)1;
        pointType.c_const = "STANDART";
        getDaoSession().getPointTypesDao().insert(pointType);

        String userPointId = mDocumentManager.createUserPoint("",0, 0, null, 1);
        UserPoints userPoint = getDaoSession().getUserPointsDao().load(userPointId);

        assertNotNull(userPoint);

        mDocumentManager.updateUserPoint(userPointId, "", "{\"data\":\"\"}");
        userPoint = getDaoSession().getUserPointsDao().load(userPointId);
        assertEquals(userPoint.jb_data, "{\"data\":\"\"}");
    }
}*/
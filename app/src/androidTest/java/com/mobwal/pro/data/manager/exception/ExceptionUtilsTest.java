/*package ru.mobnius.cic.data.manager.exception;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.exception.ExceptionModel;
import ru.mobnius.core.data.exception.FileExceptionManager;
import ru.mobnius.core.data.exception.IExceptionGroup;
import ru.mobnius.core.data.exception.IFileExceptionManager;
import ru.mobnius.cic.ManagerGenerate;

import ru.mobnius.cic.data.storage.models.ClientErrors;
import ru.mobnius.cic.data.storage.models.ClientErrorsDao;
import ru.mobnius.cic.data.storage.models.DaoSession;

public class ExceptionUtilsTest extends ManagerGenerate {
    private IFileExceptionManager fileExceptionManager;
    private DaoSession daoSession;

    @Before
    public void setUp() {
        Authorization.createInstance(getContext());
        Authorization.getInstance().setUser(new BasicUser(getCredentials(), 1, ""));
        fileExceptionManager = FileExceptionManager.getInstance(getContext());
        daoSession = getDaoSession();
        fileExceptionManager.deleteFolder();
        daoSession.getClientErrorsDao().deleteAll();
    }

    @Test
    public void saveLocalExceptionTest() {
        ExceptionModel model = ExceptionModel.getInstance(new Date(), "Ошибка", IExceptionGroup.NONE, IExceptionCode.ALL);
        String str = model.toString();
        String fileName = model.getFileName();
        fileExceptionManager.writeBytes(fileName, str.getBytes());
        ExceptionUtils.saveLocalException(getContext(), -1, daoSession);

        List<ClientErrors> list = daoSession.getClientErrorsDao().queryBuilder().where(ClientErrorsDao.Properties.ObjectOperationType.eq(DbOperationType.CREATED)).list();
        Assert.assertEquals(list.size(), 1);
    }

    @Test
    public void saveExceptionTest(){
        ExceptionUtils.saveException(getContext(), daoSession, new Exception("тест"), IExceptionGroup.NONE, IExceptionCode.ALL);

        List<ClientErrors> list = daoSession.getClientErrorsDao().queryBuilder().where(ClientErrorsDao.Properties.ObjectOperationType.eq(DbOperationType.CREATED)).list();
        Assert.assertEquals(list.size(), 1);
    }

    @Test
    public void codeToStringTest(){
        Assert.assertEquals(ExceptionModel.codeToString(2), "002");
        Assert.assertEquals(ExceptionModel.codeToString(21), "021");
        Assert.assertEquals(ExceptionModel.codeToString(215), "215");
        Assert.assertEquals(ExceptionModel.codeToString(2158), "2158");
    }
}
*/
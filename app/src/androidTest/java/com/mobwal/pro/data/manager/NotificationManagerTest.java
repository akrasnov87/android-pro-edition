/*package ru.mobnius.cic.data.manager;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.NotificationManager;
import ru.mobnius.cic.ManagerGenerate;

import static org.junit.Assert.*;

public class NotificationManagerTest extends ManagerGenerate {

    private NotificationManager mNotificationManager;

    @Before
    public void setUp() {
        mNotificationManager = new NotificationManager(GlobalSettings.getConnectUrl(), getBasicUser().getCredentials().getToken());
    }

    @Test
    public void getNewMessageCountTest() throws IOException {
        int count = mNotificationManager.getNewMessageCount();
        assertTrue(count >= 0);
    }

    @Test
    public void changeStatusTest() throws IOException {
        assertTrue(mNotificationManager.changeStatus(new String[] {"56354788-4369-4582-aea9-837f2f86623c"}));
    }

    @Test
    public void sendedTest() throws IOException {
        assertTrue(mNotificationManager.sended());
    }
}*/
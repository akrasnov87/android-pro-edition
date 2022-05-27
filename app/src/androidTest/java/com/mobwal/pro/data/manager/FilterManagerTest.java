/*package ru.mobnius.cic.data.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.cic.DbGenerate;
import ru.mobnius.cic.data.storage.models.Tracking;
import ru.mobnius.cic.data.storage.models.TrackingDao;
import ru.mobnius.cic.ui.model.FilterItem;

import static org.junit.Assert.*;

public class FilterManagerTest extends DbGenerate {

    private final String mKey = "test";
    private TrackingFilterManager mFilterManager;
    private FilterItem mFilterItem;
    private String mUuid;

    @Before
    public void setUp() {
        mFilterManager = new TrackingFilterManager(mKey);
        mUuid = UUID.randomUUID().toString();
        mFilterItem = new FilterItem(TrackingDao.Properties.Id.name, mUuid);
        mFilterManager.addItem(mFilterItem);
        String networkStatus = "LTE";
        mFilterManager.addItem(new FilterItem(TrackingDao.Properties.C_network_status.name, networkStatus));
    }

    @After
    public void tearDown() {
        getDaoSession().getTrackingDao().deleteAll();
    }

    @Test
    public void addFilterItem() {
        assertEquals(mFilterManager.getItems().length, 2);
    }

    @Test
    public void removeFilterItem() {
        mFilterManager.removeItem(mFilterItem);
        assertEquals(mFilterManager.getItems().length, 1);
        assertEquals(mFilterManager.getItems()[0].getName(), TrackingDao.Properties.C_network_status.name);
    }

    @Test
    public void updateFilterItem() {
        String uuid = UUID.randomUUID().toString();
        mFilterManager.updateItem(TrackingDao.Properties.Id.name, uuid);

        FilterItem filterItem = mFilterManager.getItem(TrackingDao.Properties.Id.name);
        assertEquals(filterItem.getLowerValue(), uuid);
    }

    @Test
    public void toFilters() {
        for(int i =0 ; i < 10; i++) {
            Tracking tracking = new Tracking();
            if(i == 0) {
                tracking.id = mUuid;
                tracking.c_network_status = "LTE";
            }else {
                tracking.id = UUID.randomUUID().toString();
                tracking.c_network_status = "NONE";
            }

            getDaoSession().getTrackingDao().insert(tracking);
        }

        Tracking[] trackings = getDaoSession().getTrackingDao().loadAll().toArray(new Tracking[0]);
        Tracking[] results = mFilterManager.toFilters(trackings);
        assertEquals(results.length , 1);
    }

    @Test
    public void serialize() throws ParseException {
        String txt = mFilterManager.serialize();
        assertEquals(txt, "{\"mDate\":\""+ DateUtil.convertDateToString(mFilterManager.getDate()) +"\",\"mItems\":[{\"mName\":\"id\",\"mType\":\"TEXT\",\"mValue\":\""+mUuid+"\"},{\"mName\":\"c_network_status\",\"mType\":\"TEXT\",\"mValue\":\"LTE\"}],\"mKey\":\"test\"}");
    }

    @Test
    public void deSerialize() {
        String txt = mFilterManager.serialize();
        TrackingFilterManager trackingFilterManager = new TrackingFilterManager(mKey);
        trackingFilterManager.deSerialize(txt);
        assertEquals(trackingFilterManager.getItems().length, 2);
    }

    @Test
    public void setFilter() {
        MobniusPreferencesManager.createInstance(getContext(), "login");
        MobniusPreferencesManager preferencesManager = MobniusPreferencesManager.getInstance();
        preferencesManager.clear();
        String txt = mFilterManager.serialize();
        preferencesManager.setFilter(mKey, txt);

        String result = preferencesManager.getFilter(mKey);
        assertEquals(txt, result);
    }

    static class TrackingFilterManager extends FilterManager<Tracking> {

        public TrackingFilterManager(String key) {
            super(key);
        }

        @Override
        public Tracking[] toFilters(Tracking[] items) {
            List<Tracking> results = new ArrayList<>();
            for(Tracking tracking : items) {
                if(isAppend(tracking, null)) {
                    results.add(tracking);
                }
            }
            return results.toArray(new Tracking[0]);
        }
    }
}*/
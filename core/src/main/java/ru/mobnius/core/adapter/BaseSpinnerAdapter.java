package ru.mobnius.core.adapter;


import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.mobnius.core.Names;
import ru.mobnius.core.R;
import ru.mobnius.core.utils.LongUtil;

public abstract class BaseSpinnerAdapter extends SimpleAdapter {
    protected ArrayList<Map<String, Object>> mMaps;

    public BaseSpinnerAdapter(Context context, ArrayList<Map<String, Object>> items, String[] from, int[] to) {
        super(context, items, R.layout.simple_type_item, from, to);
        mMaps = items;
    }

    public long getId(int position) {
        HashMap m = (HashMap)getItem(position);
        return LongUtil.convertToLong(m.get(Names.ID));
    }

    public String getStringId(int position) {
        HashMap m = (HashMap)getItem(position);
        return (String)m.get(Names.ID);
    }

    public ArrayList<Map<String, Object>> getMaps() {
        return mMaps;
    }

    public String getStringValue(int position) {
        HashMap m = (HashMap) getItem(position);
        return String.valueOf(m.get(Names.NAME));
    }

    public int getPositionById(Long id) {
        if(id != null) {
            for (int i = 0; i < getCount(); i++) {
                HashMap m = (HashMap) mMaps.get(i);
                long resultId = LongUtil.convertToLong(m.get(Names.ID));
                if (resultId == id) {
                    return i;
                }
            }
        }

        return  -1;
    }

    public int getPositionById(String id) {
        for(int i = 0; i < getCount(); i++) {
            HashMap m = (HashMap)mMaps.get(i);
            String resultId = (String)m.get(Names.ID);
            if(resultId.equals(id)) {
                return i;
            }
        }

        return  -1;
    }

    protected void addItem(long id, String name) {
        Map<String, Object> m = new HashMap<>();
        m.put(Names.NAME, name);
        m.put(Names.ID, id);

        mMaps.add(m);
    }

    protected void addItem(String id, String name) {
        Map<String, Object> m = new HashMap<>();
        m.put(Names.NAME, name);
        m.put(Names.ID, id);

        mMaps.add(m);
    }
}

package ru.mobnius.core.ui;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.RequestManager;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.rpc.FilterItem;
import ru.mobnius.core.data.rpc.QueryData;
import ru.mobnius.core.data.rpc.RPCResult;

public class DigestsAsyncTask extends AsyncTask<String, Void, String> {

    private final OnDigestsLoadedListener mListener;

    public DigestsAsyncTask(OnDigestsLoadedListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... versions) {
        List<String> items = new ArrayList<>();
        try {
            QueryData query = new QueryData();
            if(versions.length > 0 && versions[0] != null) {
                FilterItem filterItem = new FilterItem("c_version", versions[0]);
                Object[] filters = new Object[1];
                filters[0] = filterItem;
                query.filter = filters;
                query.select = "c_version, c_description";
                query.limit = 1;
            }

            RPCResult[] results = RequestManager.rpc(GlobalSettings.getConnectUrl(),
                    Authorization.getInstance().getUser().getCredentials().getToken(),
                    "sd_digests",
                    "Query",
                    query);
            if(results != null) {
                if (results[0].isSuccess() && results[0].result.total > 0) {
                    for (JsonObject jsonObject : results[0].result.records) {
                        items.add(jsonObject.get("c_description").getAsString());
                    }
                } else{
                    if (!results[0].isSuccess()) {
                        items.add(Objects.requireNonNull(results)[0].meta.msg);
                        Logger.error(new Exception(Objects.requireNonNull(results)[0].meta.msg));
                    } else {
                        items.add("Информация об обновлении отсутствует.");
                    }
                }
            }
        } catch (Exception e) {
            items.add(e.getMessage());
            Logger.error(e);
        }
        return items.get(0);
    }

    @Override
    protected void onPostExecute(String html) {
        super.onPostExecute(html);

        mListener.onDigestsLoaded(html);
    }

    public interface OnDigestsLoadedListener {
        void onDigestsLoaded(String html);
    }
}

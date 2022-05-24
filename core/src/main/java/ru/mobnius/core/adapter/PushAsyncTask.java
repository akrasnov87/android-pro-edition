package ru.mobnius.core.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.data.NotificationManager;
import ru.mobnius.core.data.RequestManager;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.rpc.SingleItemQuery;
import ru.mobnius.core.model.PushItemModel;
import ru.mobnius.core.utils.ContentUtil;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.VersionUtil;

/**
 * Асинхронная операция по получению уведомлений от сервера
 */
public class PushAsyncTask extends AsyncTask<Void, Void, List<PushItemModel>> {

    private OnPushListener mListener;
    private Exception mLastException;
    private final String mBaseUrl;
    private final WeakReference<Context> mWeakReference;

    public static final String TABLENAME = "cf_mui_cd_notifications";

    public PushAsyncTask(String baseUrl, Context weakReference) {
        mBaseUrl = baseUrl;
        mWeakReference = new WeakReference<>(weakReference);

        if (weakReference instanceof PushAsyncTask.OnPushListener) {
            mListener = (PushAsyncTask.OnPushListener) weakReference;
        }
    }

    @Override
    protected List<PushItemModel> doInBackground(Void... voids) {
        List<PushItemModel> list = new ArrayList<>();

        BasicUser basicUser = Authorization.getInstance().getUser();
        if(basicUser != null) {
            SingleItemQuery queryData = new SingleItemQuery(basicUser.getUserId(), VersionUtil.getVersionName(mWeakReference.get()));
            try {
                RPCResult[] results = RequestManager.rpc(mBaseUrl, basicUser.getCredentials().getToken(), TABLENAME, "Select", queryData);
                if (results != null && results.length > 0 && results[0].isSuccess()) {
                    for (JsonObject obj : results[0].result.records) {
                        PushItemModel itemModel = new PushItemModel();
                        itemModel.id = obj.get("id").getAsString();
                        itemModel.c_message = Html.fromHtml(obj.get("c_message").getAsString());
                        itemModel.c_title = obj.get("c_title").getAsString();
                        itemModel.d_date = DateUtil.convertStringToDate(obj.get("d_date").getAsString());
                        list.add(itemModel);
                    }
                }
                NotificationManager notificationManager = new NotificationManager(mBaseUrl, basicUser.getCredentials().getToken());
                notificationManager.changeStatusAll();

            } catch (IOException | ParseException e) {
                mLastException = e;
                Logger.error(e);
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PushItemModel> pushItemModels) {
        super.onPostExecute(pushItemModels);
        if (mListener != null) {
            if (mLastException != null) {
                mListener.onPushFailed(mLastException);
            }
            mListener.onPushLoaded(pushItemModels);
        }
    }

    public interface OnPushListener {
        void onPushLoaded(List<PushItemModel> list);

        void onPushFailed(Exception e);
    }
}
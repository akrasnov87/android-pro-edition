package ru.mobnius.core.data.mail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.mobwal.android.library.authorization.Authorization;

import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.PackageCreateUtils;

public abstract class BaseMail {
    @Expose
    public String id;

    public BaseMail() {
        fn_user_from = Authorization.getInstance().getUser().getUserId();
        id = UUID.randomUUID().toString();
        d_date = DateUtil.convertDateToString(new Date());
        c_title = "Уведомление";
    }

    @Expose
    public String c_title;

    @Expose
    public String c_group;

    @Expose
    public long fn_user_from;

    @Expose
    public long fn_user_to;

    @Expose
    public String d_date;

    public String toJsonString() {
        Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return json.toJson(this);
    }

    public byte[] getBytes() {
        PackageCreateUtils utils = new PackageCreateUtils(false);
        RPCItem item = RPCItem.addItem("cd_notifications", this);
        utils.addTo(item);
        try {
            return utils.generatePackage(UUID.randomUUID().toString());
        } catch (IOException e) {
            Logger.error(e);
            return null;
        }
    }
}

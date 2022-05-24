package ru.mobnius.core.data.mail;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.utils.PackageReadUtils;

public class StringMail extends BaseMail {

    public static StringMail[] getInstance(byte[] buffer) {
        PackageReadUtils readUtils = new PackageReadUtils(buffer, PreferencesManager.getInstance().getZip());
        try {
            RPCResult[] results = readUtils.getFromResult();
            if(results.length > 0) {
                List<StringMail> mailList = new ArrayList<>();
                if(results[0].isSuccess() && results[0].result.total > 0) {
                    for(JsonObject jsonObject : results[0].result.records) {
                        mailList.add(new Gson().fromJson(jsonObject.toString(), StringMail.class));
                    }

                    return mailList.toArray(new StringMail[0]);
                } else {
                    Logger.debug(results[0].meta.msg);
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }

    public StringMail(String message, int to, String group) {
        super();
        c_message = message;
        fn_user_to = to;
        c_group = group;
    }

    @Expose
    public String c_message;
}

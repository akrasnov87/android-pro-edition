package ru.mobnius.core.data.configuration;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.BaseTest;
import ru.mobnius.core.data.rpc.RPCRecords;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.rpc.RPCResultMeta;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ConfigurationSettingUtilTest extends BaseTest {
    private RPCResult success;

    @Before
    public void setUp() {
        List<ConfigurationSetting> configurationSettings = new ArrayList<>();
        ConfigurationSetting configurationSetting = new ConfigurationSetting();
        configurationSetting.key = "int";
        configurationSetting.value = "10";
        configurationSetting.type = ConfigurationSetting.INTEGER;
        configurationSettings.add(configurationSetting);

        configurationSetting = new ConfigurationSetting("real", "2.52", "label real", "summary real", ConfigurationSetting.REAL);
        configurationSettings.add(configurationSetting);

        configurationSetting = new ConfigurationSetting("text", "Hello", ConfigurationSetting.TEXT);
        configurationSettings.add(configurationSetting);

        configurationSetting = new ConfigurationSetting("boolean", "1", ConfigurationSetting.BOOLEAN);
        configurationSettings.add(configurationSetting);

        JsonObject[] array = new JsonObject[1];
        array[0] = new JsonObject();
        for(int i =0; i < 4; i++) {

            ConfigurationSetting item = configurationSettings.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("key", item.key);
            jsonObject.addProperty("value", item.value);
            jsonObject.addProperty("label", item.label);
            jsonObject.addProperty("summary", item.summary);
            jsonObject.addProperty("type", item.type);

            array[0].add(item.key, jsonObject);
        }

        success = new RPCResult();
        success.meta = new RPCResultMeta();
        success.meta.success = true;
        success.meta.msg = "";

        success.result = new RPCRecords();
        success.result.records = array;

        RPCResult failed = new RPCResult();
        failed.meta = new RPCResultMeta();
        failed.meta.success = false;
        failed.meta.msg = "failed";
    }

    @Test
    public void getConfigurationSettings() {
        List<ConfigurationSetting> configurationSettings = ConfigurationSettingUtil.getConfigurationSettings(success.result.records[0]);
        assertEquals(configurationSettings.size(), 4);
    }
}

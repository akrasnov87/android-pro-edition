package ru.mobnius.core.data.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConfigurationSettingUtilTest {

    @Test
    public void getIntegerValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting();
        configurationSetting.key = "int";
        configurationSetting.value = "10";
        configurationSetting.type = ConfigurationSetting.INTEGER;

        Integer integer = ConfigurationSettingUtil.getIntegerValue(configurationSetting);
        assertNotNull(integer);
        assertEquals(integer.intValue(), 10);

        configurationSetting = new ConfigurationSetting();
        configurationSetting.key = "int";
        configurationSetting.value = "test";
        configurationSetting.type = ConfigurationSetting.INTEGER;
        integer = ConfigurationSettingUtil.getIntegerValue(configurationSetting);
        assertNull(integer);
    }

    @Test
    public void getDoubleValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting("real", "2.52", "label real", "summary real", ConfigurationSetting.REAL);
        Double aDouble = ConfigurationSettingUtil.getDoubleValue(configurationSetting);
        assertNotNull(aDouble);
        assertEquals(aDouble, 2.52, 0);

        configurationSetting.value = "test";
        aDouble = ConfigurationSettingUtil.getDoubleValue(configurationSetting);
        assertNull(aDouble);
    }

    @Test
    public void getStringValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting("text", "Hello", ConfigurationSetting.TEXT);
        String s = ConfigurationSettingUtil.getStringValue(configurationSetting);

        assertNotNull(s);
        assertEquals(s, "Hello");

        configurationSetting.value = null;
        s = ConfigurationSettingUtil.getStringValue(configurationSetting);
        assertNull(s);
    }

    @Test
    public void getBooleanValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting("boolean", "1", ConfigurationSetting.BOOLEAN);
        Boolean aBoolean = ConfigurationSettingUtil.getBooleanValue(configurationSetting);

        assertNotNull(aBoolean);
        assertTrue(aBoolean);

        configurationSetting.value = "0";
        aBoolean = ConfigurationSettingUtil.getBooleanValue(configurationSetting);
        assertFalse(aBoolean);

        configurationSetting.value = "5";
        aBoolean = ConfigurationSettingUtil.getBooleanValue(configurationSetting);
        assertNull(aBoolean);
    }
}
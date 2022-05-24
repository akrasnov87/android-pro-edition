package ru.mobnius.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlTest {
    private final String mUrl1 = "https://demo.it-serv.ru/mobnius-core";
    private final String mUrl2 = "https://demo.it-serv.ru/mobnius-core/net";

    @Test
    public void getDomainUrl() {
        String domain = "https://demo.it-serv.ru";
        assertEquals(UrlUtil.getDomainUrl(mUrl1), domain);
        assertEquals(UrlUtil.getDomainUrl(mUrl2), domain);
    }

    @Test
    public void getPathUrl() {
        assertEquals(UrlUtil.getPathUrl(mUrl1), "/mobnius-core");
        assertEquals(UrlUtil.getPathUrl(mUrl2), "/mobnius-core/net");
    }
}
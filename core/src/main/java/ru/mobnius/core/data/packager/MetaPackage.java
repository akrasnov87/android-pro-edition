package ru.mobnius.core.data.packager;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class MetaPackage {
    @Expose
    public MetaAttachment[] attachments;
    @Expose
    public int binarySize;
    @Expose
    public int bufferBlockFromLength;
    @Expose
    public int bufferBlockToLength;
    @Expose
    public String dataInfo;
    @Expose
    public final String id;
    @Expose
    public int stringSize;
    @Expose
    public boolean transaction;
    @Expose
    public String version;

    public MetaPackage(String tid) {
        this.id = tid;
    }

    public String toJsonString() {
        return new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }
}

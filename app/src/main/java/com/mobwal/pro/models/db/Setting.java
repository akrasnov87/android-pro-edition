package com.mobwal.pro.models.db;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

import com.mobwal.android.library.annotation.FieldMetaData;
import com.mobwal.pro.R;
import com.mobwal.android.library.annotation.TableMetaData;

@TableMetaData(name = "cd_settings", schema = "core", to = false, pKey = "c_key")
public class Setting {

    public String c_key;

    public String c_value;

    public String c_type;

    public String toKeyName(@NotNull Context context) {
        switch (c_key.toLowerCase(Locale.ROOT)) {
            case "geo":
                return context.getString(R.string.location);

            case "geo_quality":
                return context.getString(R.string.geo_quality);

            case "image":
                return context.getString(R.string.attach);

            case "image_quality":
                 return context.getString(R.string.image_quality);

            case "image_height":
                 return context.getString(R.string.image_height);

            default:
                return context.getString(R.string.params);
        }
    }
}

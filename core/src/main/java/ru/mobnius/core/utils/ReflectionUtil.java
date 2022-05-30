package ru.mobnius.core.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

import dalvik.system.DexFile;

public class ReflectionUtil {
    public static Class<?> getClassFromName(Context context, String itemClassName) {
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
                String name = iter.nextElement();
                if(name.contains(itemClassName)) {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

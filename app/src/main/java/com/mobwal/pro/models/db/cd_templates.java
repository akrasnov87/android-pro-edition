package com.mobwal.pro.models.db;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.mobwal.pro.CustomLayoutManager;
import com.mobwal.pro.R;

public class cd_templates {
    public cd_templates() {
        id = UUID.randomUUID().toString();
        n_order = 0;
    }

    public String id;

    public String c_name;

    public String c_template;

    public String c_layout;

    public int n_order;

    public void setDefault(@NotNull Context context, @NotNull String f_route) {
        c_name = context.getString(R.string.template_default);

        CustomLayoutManager customLayoutManager = new CustomLayoutManager(context);

        c_template = customLayoutManager.getDefaultLayoutName();
        n_order = 1;

        c_layout = customLayoutManager.getDefaultLayout();
    }

    public void setDemo(@NotNull Context context, @NotNull String f_route) {
        c_name = context.getString(R.string.profile);
        c_template = "PROFILE";
        n_order = 1;
        c_layout = "layout 'vbox'\n" +
                "\ttextfield name 'Name'\n" +
                "\tdatefield birthday 'Birth day'\n" +
                "\tswitchfield male 'Male'\n" +
                "\ttextfield notice Notice";
    }
}

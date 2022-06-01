package com.mobwal.pro.shared;

import com.mobwal.android.library.annotation.FieldMetaData;
import com.mobwal.android.library.annotation.TableMetaData;

import java.util.Date;

@TableMetaData(name = "cd_profiles")
public class Profile {
    public long id;
    @FieldMetaData(name = "c_name")
    public String name;
    public Date d_date;
    public boolean b_male;
    public Integer n_age;
    @FieldMetaData(name = "n_year")
    public int year;
    @FieldMetaData(name = "n_sum")
    public double sum;
}

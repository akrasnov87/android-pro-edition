package com.mobwal.pro.shared;

import com.mobwal.pro.annotation.TableMetaData;

import java.util.Date;

@TableMetaData(name = "cd_profiles")
public class Profile {
    public long id;
    public String c_name;
    public Date d_date;
    public boolean b_male;
    public Integer n_age;
    public int n_year;
    public double n_sum;
}

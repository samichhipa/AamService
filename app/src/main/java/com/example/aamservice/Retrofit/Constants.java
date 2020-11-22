package com.example.aamservice.Retrofit;

import android.location.Location;


public class Constants {

    private static final String URL = "https://scissile-efficiency.000webhostapp.com/";

    public static String[] category={"Real Estate","Electronic Appliances","Garments & Fabrics","Vehicle","Hardware & Sanitory","Household Utility","Operator Services"};

    public static ApiInterface GetAPI() {

        return RetrofitClient.getClient(URL).create(ApiInterface.class);


    }

}

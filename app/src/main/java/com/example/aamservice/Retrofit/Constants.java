package com.example.aamservice.Retrofit;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.aamservice.R;

import java.io.File;


public class Constants {

    public static final String URL = "http://192.168.3.111:8080/AamService/";

    public static String[] category = {"Real Estate", "Electronic Appliances", "Garments & Fabrics", "Vehicle", "Hardware & Sanitory", "Household Utility", "Operator Services"};

    public static String getPath(Context context) {

        File directory = new File(android.os.Environment.getExternalStorageState() + File.separator + context.getResources().getString(R.string.app_name) + File.separator);
        ;
        if (!directory.exists())
            directory.mkdir();
        return directory.getPath() + File.separator;

    }

    public static File getURL(Context context){
        File directory = new File(android.os.Environment.getExternalStorageState() + File.separator + context.getResources().getString(R.string.app_name) + File.separator);
        return directory;
    }

    public static ApiInterface GetAPI() {

        return RetrofitClient.getClient(URL).create(ApiInterface.class);
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

package com.example.aamservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.example.aamservice.services.JSONParser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    Button login_register_btn, login_login_btn;
    MaterialEditText txt_username, txt_pass, txt_contact;
    ProgressDialog progressDialog;
    String HttpURL = "https://scissile-efficiency.000webhostapp.com/signin.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_STATUS = "status";

    public static final String contact_key="contact";
    public static final String email_key="email";

    SharedPreferences sharedPreferences;

    String str;

    ApiInterface mService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mService = Constants.GetAPI();
        sharedPreferences=getSharedPreferences("PREF",MODE_PRIVATE);

        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");


        login_register_btn = findViewById(R.id.login_register_btn);
        login_login_btn = findViewById(R.id.login_login_btn);
        txt_username = findViewById(R.id.login_username);
        txt_pass = findViewById(R.id.login_password);


        if (!TextUtils.isEmpty(sharedPreferences.getString("id","")) && !TextUtils.isEmpty(sharedPreferences.getString("contact","")) && !TextUtils.isEmpty(sharedPreferences.getString("purpose",""))){

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        login_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txt_username.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_pass.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {

                    if (Constants.isNetworkAvailable(LoginActivity.this)) {

                        if (txt_username.getText().toString().substring(0, 2).equals("92")) {

                            //   new AttemptSignInWithContact().execute();
                            progressDialog.show();
                            mService.SignInWithContactPass(txt_username.getText().toString(), txt_pass.getText().toString()).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {

                                        progressDialog.dismiss();
                                        str = "";
                                        try {
                                            str = ((ResponseBody) response.body()).string();
                                            JSONObject jSONObject = new JSONObject(str);


                                            if (jSONObject.getString("status").equals("Successful")){

                                                SharedPreferences.Editor editor = getSharedPreferences("PREF", MODE_PRIVATE).edit();
                                                editor.putString("id", jSONObject.getString("id"));
                                                editor.putString("fname", jSONObject.getString("fname"));
                                                editor.putString("lname", jSONObject.getString("lname"));
                                                editor.putString("email", jSONObject.getString("email"));
                                                editor.putString("contact", jSONObject.getString("contact"));
                                                editor.putString("address", jSONObject.getString("address"));
                                                editor.putString("city", jSONObject.getString("city"));
                                                editor.putString("age", jSONObject.getString("age"));
                                                editor.putString("gender", jSONObject.getString("gender"));
                                                editor.putString("purpose", jSONObject.getString("purpose"));
                                                editor.commit();

                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                finish();

                                                Toast.makeText(LoginActivity.this, ""+jSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                                            }else{

                                                Toast.makeText(LoginActivity.this, ""+jSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                                            }


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        try {
                                            Toast.makeText(LoginActivity.this, "" + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                    progressDialog.dismiss();
                                }
                            });
                        } else {


                            progressDialog.show();
                            mService.SignInWithEmailPass(txt_username.getText().toString(), txt_pass.getText().toString())
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) {

                                                progressDialog.dismiss();
                                                str = "";
                                                try {
                                                    str = ((ResponseBody) response.body()).string();
                                                    JSONObject jSONObject = new JSONObject(str);

                                                    if (jSONObject.getString("status").equals("Successful")) {
                                                        SharedPreferences.Editor editor = getSharedPreferences("PREF", MODE_PRIVATE).edit();
                                                        editor.putString("id", jSONObject.getString("id"));
                                                        editor.putString("fname", jSONObject.getString("fname"));
                                                        editor.putString("lname", jSONObject.getString("lname"));
                                                        editor.putString("email", jSONObject.getString("email"));
                                                        editor.putString("contact", jSONObject.getString("contact"));
                                                        editor.putString("address", jSONObject.getString("address"));
                                                        editor.putString("city", jSONObject.getString("city"));
                                                        editor.putString("age", jSONObject.getString("age"));
                                                        editor.putString("gender", jSONObject.getString("gender"));
                                                        editor.putString("purpose", jSONObject.getString("purpose"));
                                                        editor.commit();

                                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                        finish();
                                                        Toast.makeText(LoginActivity.this, "" + jSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }else{

                                                        Toast.makeText(LoginActivity.this, "" + jSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                progressDialog.dismiss();
                                                str = "";
                                                try {
                                                    str = ((ResponseBody) response.errorBody()).string();
                                                    JSONObject jSONObject = new JSONObject(str);
                                                    Toast.makeText(LoginActivity.this, "" + jSONObject.getString("status"), Toast.LENGTH_SHORT).show();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }else{
                        Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        login_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (
                ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

        ) {

            Dexter.withActivity(LoginActivity.this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA


                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {

                            if (report.areAllPermissionsGranted()) {
                                //Toast.makeText(HomeActivity.this, "All Permission Granted", Toast.LENGTH_SHORT).show();
                            } else if (report.isAnyPermissionPermanentlyDenied()) {
                                return;
                            } else if (report.isAnyPermissionPermanentlyDenied()) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Permission Denied")
                                        .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                        .setNegativeButton("Cancel", null)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            }
                                        })
                                        .show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                            for (PermissionRequest permissionRequest : permissions) {
                                token.continuePermissionRequest();

                            }
                        }
                    }).check();
            return;
        }

    }




}
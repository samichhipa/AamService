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

    JSONParser jsonParser = new JSONParser();

    String str;

    ApiInterface mService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mService = Constants.GetAPI();
        sharedPreferences=getSharedPreferences("PREF",MODE_PRIVATE);

        login_register_btn = findViewById(R.id.login_register_btn);
        login_login_btn = findViewById(R.id.login_login_btn);
        txt_username = findViewById(R.id.login_username);
        txt_pass = findViewById(R.id.login_password);

        login_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txt_username.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_pass.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }  else {

                   if (txt_username.getText().toString().substring(0,3).equals("92")){

                    //   new AttemptSignInWithContact().execute();

                       mService.SignInWithContactPass(txt_username.getText().toString(),txt_pass.getText().toString()).enqueue(new Callback<ResponseBody>() {
                           @Override
                           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                               if (response.isSuccessful()){

                                   str = "";
                                   try {
                                       str = ((ResponseBody) response.body()).string();
                                       JSONObject jSONObject = new JSONObject(str);


                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                               }else{
                                   try {
                                       Toast.makeText(LoginActivity.this, ""+response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                               }
                           }

                           @Override
                           public void onFailure(Call<ResponseBody> call, Throwable t) {

                           }
                       });
                   }else{

                      // new AttemptSignInWithEmail().execute();

                       mService.SignInWithEmailPass(txt_username.getText().toString(),txt_pass.getText().toString())
                               .enqueue(new Callback<ResponseBody>() {
                                   @Override
                                   public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                       if (response.isSuccessful()){

                                           str = "";
                                           try {
                                               str = ((ResponseBody) response.body()).string();
                                               JSONObject jSONObject = new JSONObject(str);



                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           } catch (JSONException e) {
                                               e.printStackTrace();
                                           }
                                       }else{
                                           try {
                                               Toast.makeText(LoginActivity.this, ""+response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }

                                   @Override
                                   public void onFailure(Call<ResponseBody> call, Throwable t) {

                                   }
                               });
                   }

                }
            }
        });
        login_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
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

    class AttemptSignInWithContact extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("contact", txt_username.getText().toString()));
            params.add(new BasicNameValuePair("password", txt_pass.getText().toString()));


            JSONObject json = jsonParser.makeHttpRequest(HttpURL, "GET", params);
            // checking log for json response
            Log.d("Login attempt", json.toString());

            try {
                String status = json.getString(TAG_STATUS);

                if (status.equals("Successful")) {


                    Toast.makeText(LoginActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                    return json.getString(TAG_MESSAGE);

                } else {


                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "exception" + e, Toast.LENGTH_LONG).show();
            }


            return null;
        }

        protected void onPostExecute(String message1) {
            super.onPostExecute(message1);
            if (message1 != null) {
                Toast.makeText(LoginActivity.this, message1, Toast.LENGTH_LONG).show();
            }
        }

    }
    class AttemptSignInWithEmail extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", txt_username.getText().toString()));
            params.add(new BasicNameValuePair("password", txt_pass.getText().toString()));


            JSONObject json = jsonParser.makeHttpRequest(HttpURL, "GET", params);
            // checking log for json response
            Log.d("Login attempt", json.toString());

            try {
                String status = json.getString(TAG_STATUS);

                if (status.equals("Successful")) {


                    Toast.makeText(LoginActivity.this, "" + status, Toast.LENGTH_SHORT).show();
                    return json.getString(TAG_MESSAGE);

                } else {


                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "exception" + e, Toast.LENGTH_LONG).show();
            }


            return null;
        }

        protected void onPostExecute(String message1) {
            super.onPostExecute(message1);
            if (message1 != null) {
                Toast.makeText(LoginActivity.this, message1, Toast.LENGTH_LONG).show();
            }
        }

    }
}
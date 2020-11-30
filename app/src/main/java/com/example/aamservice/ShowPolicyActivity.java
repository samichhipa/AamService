package com.example.aamservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPolicyActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    String post_id;

    String str = "";

    ProgressDialog progressDialog;

    TextView txt_owner_name, txt_add_name, txt_location, txt_amount, txt_duration, txt_gurantee, txt_gurantee_amount;
    ImageView show_post_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_policy);

        progressDialog=new ProgressDialog(ShowPolicyActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            //The key argument here must match that used in the other activity
        }
        post_id= extras.getString("post_idd");

        apiInterface = Constants.GetAPI();
        txt_owner_name = findViewById(R.id.show_policy_owner_name);
        txt_add_name = findViewById(R.id.show_policy_post_title);
        txt_location = findViewById(R.id.show_policy_post_location);
        txt_amount = findViewById(R.id.show_policy_post_amount);
        txt_duration = findViewById(R.id.show_policy_post_duration);
        txt_gurantee = findViewById(R.id.show_policy_post_gurantee);
        txt_gurantee_amount = findViewById(R.id.show_policy_post_gurantee_amount);
        show_post_cancel = findViewById(R.id.show_post_cancel);


        if (Constants.isNetworkAvailable(ShowPolicyActivity.this) && post_id != null) {

            showPolicy();
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        show_post_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void showPolicy() {

        progressDialog.show();
        apiInterface.ShowPolicy(post_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    progressDialog.dismiss();

                    str = "";
                    try {
                        str = ((ResponseBody) response.body()).string();
                        JSONObject object = new JSONObject(str);

                        if (object.getString("status").equals("Successful")) {

                            txt_add_name.setText(object.getString("ad_name"));
                            txt_amount.setText("Rs. " + object.getString("ad_amount"));
                            txt_duration.setText(object.getString("duration"));
                            txt_gurantee.setText(object.getString("guarantee"));
                            txt_gurantee_amount.setText(object.getString("guarantee_amount"));
                            txt_owner_name.setText(object.getString("owner_name"));
                            txt_location.setText(object.getString("location"));


                        } else {
                            Toast.makeText(ShowPolicyActivity.this, object.getString("status"), Toast.LENGTH_SHORT).show();
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
                        JSONObject object = new JSONObject(str);

                        Toast.makeText(ShowPolicyActivity.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ShowPolicyActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
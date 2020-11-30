package com.example.aamservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetails extends AppCompatActivity {


    private ImageView mPostDetailBack;
    private ImageView mPostDetailImg;
    private TextView mPostDetailTitle;
    private TextView mPostDetailAmount;
    private TextView mPostDetailDescription;
    private TextView mPostDetailStartDate;
    private TextView mPostDetailEndDate;
    private TextView mPostDetailLocation;
    private TextView mPostDetailContact;
    private TextView mPostDetailCategory;
    TextView txt_show_post;
    CheckBox checkbox_policy;

    String post_id;
    String str = "";

    SharedPreferences preferences;
    String tenant_fname, tenant_lname;
    Button confirm_policy_btn;

    String post_date, return_date;


    ApiInterface apiInterface;


    JSONObject object = null;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        initView();

        preferences = getSharedPreferences("PREF", MODE_PRIVATE);

        tenant_fname = preferences.getString("fname", "");
        tenant_lname = preferences.getString("lname", "");

        apiInterface = Constants.GetAPI();

        txt_show_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetails.this, ShowPolicyActivity.class);
                intent.putExtra("post_idd", post_id);
                startActivity(intent);
            }
        });

        confirm_policy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkbox_policy.isChecked()) {
                    Toast.makeText(PostDetails.this, "Please Accept Policy", Toast.LENGTH_SHORT).show();
                } else {

                    if (Constants.isNetworkAvailable(PostDetails.this)) {

                        progressDialog.show();
                        apiInterface.ConfirmPolicy(post_id, "tenant", tenant_fname + tenant_lname, post_date, return_date, "Yes").enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    str = "";
                                    try {
                                        str = ((ResponseBody) response.body()).string();
                                        JSONObject object = new JSONObject(str);

                                        if (object.getString("status").equals("Successful")) {
                                            Toast.makeText(PostDetails.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();

                                            progressDialog.setMessage("Generating Invoice....");
                                            apiInterface.CreateInvoice(post_id).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                                    if (response.isSuccessful()) {

                                                        str = "";
                                                        try {
                                                            str = ((ResponseBody) response.body()).string();
                                                            JSONObject object = new JSONObject(str);

                                                            if (object.getString("status").equals("Successful")) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(PostDetails.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(PostDetails.this, ShowPolicyActivity.class);
                                                                intent.putExtra("post_idd", post_id);
                                                                startActivity(intent);


                                                            } else {

                                                            }


                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        str = "";
                                                        try {
                                                            str = ((ResponseBody) response.errorBody()).string();
                                                            JSONObject object = new JSONObject(str);

                                                            Toast.makeText(PostDetails.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(PostDetails.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(PostDetails.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();

                                        }


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {

                                    str = "";
                                    try {
                                        str = ((ResponseBody) response.errorBody()).string();
                                        JSONObject object = new JSONObject(str);

                                        Toast.makeText(PostDetails.this, "" + object.getString("status"), Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(PostDetails.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else{

                        Toast.makeText(PostDetails.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }

    public void initView() {

        progressDialog = new ProgressDialog(PostDetails.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        try {
            String str = getIntent().getExtras().getString("post_obj");
            object = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        confirm_policy_btn = findViewById(R.id.confirm_policy_btn);
        mPostDetailBack = findViewById(R.id.post_detail_back);
        mPostDetailImg = findViewById(R.id.post_detail_img);
        mPostDetailTitle = findViewById(R.id.post_detail_title);
        mPostDetailAmount = findViewById(R.id.post_detail_amount);
        mPostDetailDescription = findViewById(R.id.post_detail_description);
        mPostDetailStartDate = findViewById(R.id.post_detail_start_date);
        mPostDetailEndDate = findViewById(R.id.post_detail_end_date);
        mPostDetailLocation = findViewById(R.id.post_detail_location);
        mPostDetailContact = findViewById(R.id.post_detail_contact);
        mPostDetailCategory = findViewById(R.id.post_detail_category);
        txt_show_post = findViewById(R.id.txt_show_post);
        checkbox_policy = findViewById(R.id.checkbox_policy);


        mPostDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {


            Picasso.get().load(Constants.URL+"/uploads/"+object.getString("imagepath")).into(mPostDetailImg);
            mPostDetailTitle.setText(object.getString("title"));
            mPostDetailAmount.setText(object.getString("amount"));
            mPostDetailDescription.setText(object.getString("description"));
            post_date = object.getString("post_date");
            return_date = object.getString("expiry_date");
            mPostDetailStartDate.setText(post_date);
            mPostDetailEndDate.setText(return_date);
            mPostDetailContact.setText(object.getString("contact"));
            mPostDetailLocation.setText(object.getString("location"));
            mPostDetailCategory.setText(object.getString("category"));
            post_id = object.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
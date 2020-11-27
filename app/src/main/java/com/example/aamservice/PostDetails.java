package com.example.aamservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

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


    JSONObject object=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        initView();
    }

    public void initView() {

        try {
            String str=getIntent().getExtras().getString("post_obj");
            object=new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPostDetailBack=findViewById(R.id.post_detail_back);
        mPostDetailImg=findViewById(R.id.post_detail_img);
        mPostDetailTitle=findViewById(R.id.post_detail_title);
        mPostDetailAmount=findViewById(R.id.post_detail_amount);
        mPostDetailDescription=findViewById(R.id.post_detail_description);
        mPostDetailStartDate=findViewById(R.id.post_detail_start_date);
        mPostDetailEndDate=findViewById(R.id.post_detail_end_date);
        mPostDetailLocation=findViewById(R.id.post_detail_location);
        mPostDetailContact=findViewById(R.id.post_detail_contact);
        mPostDetailCategory=findViewById(R.id.post_detail_category);


        mPostDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {

            String url="http://scissile-efficiency.000webhostapp.com/uploads/"+object.getString("imagepath");
            Glide.with(PostDetails.this).load(url).into(mPostDetailImg);
            mPostDetailTitle.setText(object.getString("title"));
            mPostDetailAmount.setText(object.getString("amount"));
            mPostDetailDescription.setText(object.getString("description"));
            mPostDetailStartDate.setText(object.getString("post_date"));
            mPostDetailEndDate.setText(object.getString("expiry_date"));
            mPostDetailContact.setText(object.getString("contact"));
            mPostDetailLocation.setText(object.getString("location"));
            mPostDetailCategory.setText(object.getString("category"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
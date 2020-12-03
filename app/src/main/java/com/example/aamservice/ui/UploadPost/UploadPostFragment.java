package com.example.aamservice.ui.UploadPost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.aamservice.HomeActivity;
import com.example.aamservice.R;
import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.example.aamservice.Retrofit.FilePath;
import com.example.aamservice.Retrofit.ProgressRequestBody;
import com.example.aamservice.Retrofit.UploadCallback;
import com.example.aamservice.services.AndroidMultiPartEntity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

import static android.app.Activity.RESULT_OK;

public class UploadPostFragment extends Fragment {
    Uri image_uri = null;
    ImageView add_post_image;
    EditText add_post_title, add_post_description, add_post_msg, add_post_location, add_post_contact, add_post_amount;
    TextView add_post_start_time, add_post_end_time;
    Spinner add_post_category;
    Button add_post_btn;

    String selectedCategory;
    String start_date = "";
    String end_date = "";
    String str = "";
    long starttime_long;

    ApiInterface mService;
    String str_uri;
    Bitmap bitmap = null;

    ProgressDialog progressDialog;
    SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_upload_post, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        preferences = getActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE);

        mService = Constants.GetAPI();
        add_post_image = root.findViewById(R.id.add_post_image);

        add_post_category = root.findViewById(R.id.add_post_category);
        add_post_title = root.findViewById(R.id.add_post_title);
        add_post_description = root.findViewById(R.id.add_post_description);
        add_post_msg = root.findViewById(R.id.add_post_msg);
        add_post_location = root.findViewById(R.id.add_post_location);
        add_post_contact = root.findViewById(R.id.add_post_contact);
        add_post_amount = root.findViewById(R.id.add_post_amount);
        add_post_btn = root.findViewById(R.id.add_post_post_Btn);

        add_post_start_time = root.findViewById(R.id.add_post_start_time);
        add_post_end_time = root.findViewById(R.id.add_post_end_time);
        ArrayAdapter semester_adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                Constants.category);
        add_post_category.setAdapter(semester_adapter);

        selectedCategory = add_post_category.getSelectedItem().toString();

        add_post_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString();

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(UploadPostFragment.this.getContext(), UploadPostFragment.this);

            }
        });
        add_post_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start_date.isEmpty()) {
                    Toast.makeText(getActivity(), "Select Start Date", Toast.LENGTH_SHORT).show();
                } else {
                    Select_End_DateDialog();
                }

            }
        });


        add_post_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Start_DateDialog();


            }
        });


        add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(add_post_title.getText().toString())) {

                    Toast.makeText(getActivity(), "Enter Title", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(add_post_description.getText().toString())) {

                    Toast.makeText(getActivity(), "Enter Description", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(add_post_amount.getText().toString())) {

                    Toast.makeText(getActivity(), "Enter Amount", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(add_post_msg.getText().toString())) {

                    Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(add_post_contact.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter Contact No", Toast.LENGTH_SHORT).show();


                } else if (TextUtils.isEmpty(add_post_location.getText().toString())) {

                    Toast.makeText(getActivity(), "Enter Location", Toast.LENGTH_SHORT).show();

                } else if (start_date.isEmpty()) {

                    Toast.makeText(getActivity(), "Select Start Time", Toast.LENGTH_SHORT).show();

                } else if (end_date.isEmpty()) {

                    Toast.makeText(getActivity(), "Select End Time", Toast.LENGTH_SHORT).show();

                } else if (image_uri == null && bitmap == null) {
                    Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_SHORT).show();
                } else if (preferences.getString("purpose", "").equals("tenant")) {
                    Toast.makeText(getActivity(), "Only Owner Can Upload Post...", Toast.LENGTH_SHORT).show();

                } else {

                    if (Constants.isNetworkAvailable(getActivity())) {

                        progressDialog.show();
                        mService.AddPost(add_post_title.getText().toString(), add_post_amount.getText().toString(), imagetostr(bitmap), add_post_description.getText().toString()
                                , selectedCategory, add_post_location.getText().toString(), start_date, end_date, add_post_contact.getText().toString(), preferences.getString("id", "")).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    progressDialog.dismiss();
                                    str = "";

                                    try {
                                        str = ((ResponseBody) response.body()).string();
                                        JSONObject jsonObject = new JSONObject(str);
                                        if (jsonObject.getString("status").equals("Successful")) {

                                            Toast.makeText(getActivity(), ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


                                            PolicyFragment policyFragment=new PolicyFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("post_id", jsonObject.getString("post_id"));
                                            bundle.putString("ad_name", add_post_title.getText().toString());
                                            bundle.putString("ad_amount", add_post_amount.getText().toString());
                                            bundle.putString("duration", start_date + " - " + end_date);
                                            bundle.putString("location", add_post_location.getText().toString());
                                            policyFragment.setArguments(bundle);

                                            getFragmentManager().beginTransaction().replace(R.id.main_fragment, policyFragment).commit();
                                            //setFragment(new PolicyFragment(),bundle);

                                        }else{

                                            Toast.makeText(getActivity(), ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                        }
                                        Log.d("TAG", jsonObject.toString());
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
                                        JSONObject jsonObject = new JSONObject(str);
                                        Log.d("TAG", jsonObject.toString());

                                        Toast.makeText(getActivity(), ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return root;
    }



    private String imagetostr(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }





    private void Select_Start_DateDialog() {

        Calendar calendar = Calendar.getInstance();
        int c_year = calendar.get(Calendar.YEAR);
        int c_month = calendar.get(Calendar.MONTH);
        int c_day = calendar.get(Calendar.DAY_OF_MONTH);
        final int mHour, mMinute;
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {


                start_date = year + "-" + (month + 1) + "-" + day;


                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.my_dialog_theme,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {


                                String start_time = hourOfDay + ":" + minute + ":00";
                                start_date = start_date + " " + start_time;
                                if (!start_date.equals("")) {


                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = null;

                                    try {
                                        date = format.parse(start_date);
                                        starttime_long = date.getTime();
                                        add_post_start_time.setText(format.format(date));


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        }, c_year, c_month, c_day);

        datePickerDialog.show();
        //Disabled Previous Date//

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


    }


    private void Select_End_DateDialog() {

        Calendar calendar = Calendar.getInstance();
        int c_year = calendar.get(Calendar.YEAR);
        int c_month = calendar.get(Calendar.MONTH);
        int c_day = calendar.get(Calendar.DAY_OF_MONTH);
        final int mHour, mMinute;
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {


                end_date = year + "-" + (month + 1) + "-" + day;


                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.my_dialog_theme,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {


                                String end_time = hourOfDay + ":" + minute + ":00";
                                end_date = end_date + " " + end_time;
                                if (!end_date.equals("")) {


                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = null;

                                    try {
                                        date = format.parse(end_date);
                                        long endTime = date.getTime();
                                        add_post_end_time.setText(format.format(date));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        }, c_year, c_month, c_day);


        datePickerDialog.show();
        //Disabled Previous Date//
        datePickerDialog.getDatePicker().setMinDate(starttime_long - 1000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 203) {
            if (resultCode == RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                image_uri = result.getUri();
                Glide.with(getContext()).load(result.getUri()).into(add_post_image);
// by this point we have the camera photo on disk
                bitmap = BitmapFactory.decodeFile(image_uri.getPath());
                //add_post_image.setImageBitmap(bitmap);


            } else{
                Toast.makeText(getActivity(), "Closed", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
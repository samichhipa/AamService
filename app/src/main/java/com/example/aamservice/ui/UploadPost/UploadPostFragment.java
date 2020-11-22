package com.example.aamservice.ui.UploadPost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.TextUtils;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.aamservice.HomeActivity;
import com.example.aamservice.R;
import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.example.aamservice.Retrofit.FilePath;
import com.example.aamservice.Retrofit.ProgressRequestBody;
import com.example.aamservice.Retrofit.UploadCallback;
import com.example.aamservice.services.AndroidMultiPartEntity;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_upload_post, container, false);

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
/*

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

 */

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

                } else if (image_uri == null) {
                    Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_SHORT).show();
                } else {
                    new UploadFileToServer().execute();
                    //String file=FilePath.getPath(getActivity(),image_uri);

                    /*
                    File file = new File(image_uri.getPath());


                    RequestBody create = RequestBody.create(MediaType.parse("image/*"), file);

                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), create);

                    mService.SaveImage(add_post_title.getText().toString(),add_post_amount.getText().toString(),body,add_post_description.getText().toString()
                    ,add_post_msg.getText().toString(),add_post_location.getText().toString(),start_date,end_date,selectedCategory).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            if (response.isSuccessful()){

                                try {
                                    str=response.body().string();
                                    JSONObject jsonObject=new JSONObject(str);
                                    Log.d(str,jsonObject.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                     */
                }
            }
        });


        return root;
    }

    private class UploadFileToServer extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = null;

            HttpClient httpClient = new DefaultHttpClient();
//Important for android version 9 pie/
            httpClient.getConnectionManager().getSchemeRegistry().register(
                    new Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
            );
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://scissile-efficiency.000webhostapp.com/ad_post.php");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                            }
                        });

                File sourceFile = new File(str_uri);


                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                entity.addPart("title", new StringBody(add_post_title.getText().toString()));
                entity.addPart("amount", new StringBody(add_post_amount.getText().toString()));
                entity.addPart("description", new StringBody(add_post_description.getText().toString()));
                entity.addPart("message", new StringBody(add_post_msg.getText().toString()));
                entity.addPart("location", new StringBody(add_post_location.getText().toString()));
                entity.addPart("category", new StringBody(selectedCategory));
                entity.addPart("start_time", new StringBody(add_post_start_time.getText().toString()));
                entity.addPart("end_time", new StringBody(add_post_end_time.getText().toString()));
                entity.addPart("contact", new StringBody(add_post_contact.getText().toString()));

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();


                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {

                    responseString = EntityUtils.toString(r_entity);
                    // Server response
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Post Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();

        }


        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://scissile-efficiency.000webhostapp.com/ad_post.php");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                            }
                        });

                String str_uri = FilePath.getPath(getActivity(), image_uri);
                File sourceFile = new File(str_uri);


                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                entity.addPart("title", new StringBody(add_post_title.getText().toString()));
                entity.addPart("amount", new StringBody(add_post_amount.getText().toString()));
                entity.addPart("description", new StringBody(add_post_description.getText().toString()));
                entity.addPart("message", new StringBody(add_post_msg.getText().toString()));
                entity.addPart("location", new StringBody(add_post_location.getText().toString()));
                entity.addPart("category", new StringBody(selectedCategory));
                entity.addPart("start_time", new StringBody(add_post_start_time.getText().toString()));
                entity.addPart("end_time", new StringBody(add_post_end_time.getText().toString()));

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    Toast.makeText(getActivity(), "Post Uploaded", Toast.LENGTH_SHORT).show();

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("TAG", "Response from server: " + result);


            super.onPostExecute(result);
        }

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
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                image_uri = result.getUri();
                //add_post_image.setImageURI(image_uri);
                str_uri = FilePath.getPath(getActivity(), image_uri);

                //Glide.with(getContext()).load(this.imageUri).into(this.item_image);
            } else if (resultCode == 204) {
                Toast.makeText(getContext(), result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }


}
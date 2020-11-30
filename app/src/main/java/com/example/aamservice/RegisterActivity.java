package com.example.aamservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.example.aamservice.services.JSONParser;
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

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText txt_email, txt_pass, txt_conf_pass, txt_contact, txt_address, txt_age, txt_first_name, txt_last_name;
    Spinner gender_spinner, purpose_spinner, city_spinner;
    String[] gender = {"Male", "Female"};
    String[] purpose = {"tenant", "owner"};
    String[] city = {"Karachi", "Lahore", "Islamabad", "Rawalpindi", "Hyderabad", "Quetta", "Peshawar"};
    Button btnRegister;

    String selectedGender, selectedPurpose, selectedCity;

    JSONParser jsonParser = new JSONParser();

    ProgressDialog progressDialog;
    String HttpURL = "https://scissile-efficiency.000webhostapp.com/signup.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_MESSAGE = "message";

    ApiInterface mService;
    String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog=new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        init();
        mService = Constants.GetAPI();


        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gender);
        ArrayAdapter<String> purposerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, purpose);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, city);

        purpose_spinner.setAdapter(purposerAdapter);
        gender_spinner.setAdapter(genderAdapter);
        city_spinner.setAdapter(cityAdapter);

        selectedCity = city_spinner.getSelectedItem().toString();
        selectedGender = gender_spinner.getSelectedItem().toString();
        selectedPurpose = purpose_spinner.getSelectedItem().toString();


        purpose_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedPurpose = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedCity = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txt_first_name.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(txt_last_name.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(txt_email.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_contact.getText().toString())) {

                    Toast.makeText(RegisterActivity.this, "Enter Contact No", Toast.LENGTH_SHORT).show();
                }else if (!txt_contact.getText().toString().substring(0,2).equals("92")){
                    Toast.makeText(RegisterActivity.this, "Enter Phone Number like 92XXXXXXXX", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(txt_pass.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_conf_pass.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!txt_conf_pass.getText().toString().equals(txt_pass.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Password Mismatch", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_address.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Address", Toast.LENGTH_SHORT).show();
                } else if (Integer.valueOf(txt_age.getText().toString()) < 0 || Integer.valueOf(txt_age.getText().toString()) > 100) {
                    Toast.makeText(RegisterActivity.this, "Age must be must 0-100", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_age.getText().toString())) {

                    Toast.makeText(RegisterActivity.this, "Enter Age", Toast.LENGTH_SHORT).show();
                } else {

                    if(Constants.isNetworkAvailable(RegisterActivity.this)) {
                        progressDialog.show();
                        mService.SignUp(txt_first_name.getText().toString(), txt_last_name.getText().toString(), txt_email.getText().toString(), txt_pass.getText().toString()
                                , txt_conf_pass.getText().toString(), txt_contact.getText().toString(), txt_address.getText().toString(),
                                selectedCity, txt_age.getText().toString(), selectedGender, selectedPurpose).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    progressDialog.dismiss();
                                    str = "";
                                    try {
                                        str = response.body().string();
                                        JSONObject object = new JSONObject(str);

                                        if (object.getString("status").equals("Successful")){
                                            Toast.makeText(RegisterActivity.this, "" + object.getString("message"), Toast.LENGTH_SHORT).show();

                                            txt_contact.setText("");
                                            txt_address.setText("");
                                            txt_age.setText("");
                                            txt_conf_pass.setText("");
                                            txt_email.setText("");
                                            txt_first_name.setText("");
                                            txt_last_name.setText("");
                                            txt_pass.setText("");

                                        }else{

                                            Toast.makeText(RegisterActivity.this, "" + object.getString("message"), Toast.LENGTH_SHORT).show();


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
                                        str = response.errorBody().string();
                                        JSONObject object = new JSONObject(str);

                                        Toast.makeText(RegisterActivity.this, "" + object.getString("message"), Toast.LENGTH_SHORT).show();


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
                                Toast.makeText(RegisterActivity.this, "" +t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else{

                        Toast.makeText(RegisterActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


    }

    private void init() {
        txt_email = findViewById(R.id.reg_email);
        txt_contact = findViewById(R.id.reg_contact);
        txt_pass = findViewById(R.id.reg_passwrod);
        txt_conf_pass = findViewById(R.id.reg_confirm_pass);
        txt_address = findViewById(R.id.reg_address);
        txt_age = findViewById(R.id.reg_age);

        gender_spinner = findViewById(R.id.reg_gender);
        city_spinner = findViewById(R.id.reg_city);
        purpose_spinner = findViewById(R.id.reg_purpose);
        btnRegister = findViewById(R.id.reg_reg_btn);

        txt_first_name = findViewById(R.id.reg_first_name);
        txt_last_name = findViewById(R.id.reg_lastname);
    }

    class AttemptSignUp extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first_name", txt_first_name.getText().toString()));
            params.add(new BasicNameValuePair("last_name", txt_last_name.getText().toString()));
            params.add(new BasicNameValuePair("email", txt_email.getText().toString()));
            params.add(new BasicNameValuePair("password",txt_pass.getText().toString()));
            params.add(new BasicNameValuePair("confirm_password", txt_conf_pass.getText().toString()));
            params.add(new BasicNameValuePair("contact", txt_contact.getText().toString()));
            params.add(new BasicNameValuePair("address",txt_address.getText().toString()));
            params.add(new BasicNameValuePair("city", selectedCity));
            params.add(new BasicNameValuePair("age", txt_age.getText().toString()));
            params.add(new BasicNameValuePair("gender", selectedGender));
            params.add(new BasicNameValuePair("purpose", selectedPurpose));



            JSONObject json = jsonParser.makeHttpRequest(HttpURL, "GET", params);
            // checking log for json response
            Log.d("Login attempt", json.toString());

            try {
                String status = json.getString(TAG_STATUS);

                if (status.equals("Successful")) {


                    Toast.makeText(RegisterActivity.this, ""+status, Toast.LENGTH_SHORT).show();
                    return json.getString(TAG_MESSAGE);

                } else {



                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                Toast.makeText(RegisterActivity.this, "exception" + e, Toast.LENGTH_LONG).show();
            }


            return null;
        }

        protected void onPostExecute(String message1) {
            super.onPostExecute(message1);
            if (message1 != null) {
                Toast.makeText(RegisterActivity.this, message1, Toast.LENGTH_LONG).show();
            }
        }

    }
}
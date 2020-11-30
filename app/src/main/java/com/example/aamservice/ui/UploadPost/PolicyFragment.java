package com.example.aamservice.ui.UploadPost;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aamservice.R;
import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.example.aamservice.ui.posts.PostsFragment;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PolicyFragment extends Fragment {


    View view;

    String post_id, ad_name, owner_name, ad_amount, duration,location;

    SharedPreferences preferences;

    MaterialEditText txt_guarantee, txt_gurantee_amount;
    Button submit_policy_btn;

    TextView policy_duration;
    ApiInterface apiInterface;

    ProgressDialog progressDialog;

    String str="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiInterface= Constants.GetAPI();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        preferences = getActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE);

        post_id = getArguments().getString("post_id");
        ad_name = getArguments().getString("ad_name");
        owner_name = preferences.getString("owner_name", "");
        ad_amount = getArguments().getString("ad_amount");
        duration = getArguments().getString("duration");
        location=getArguments().getString("location");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_policy, container, false);


        txt_guarantee = view.findViewById(R.id.policy_gurantee);
        txt_gurantee_amount = view.findViewById(R.id.policy_gurantee_amount);
        submit_policy_btn = view.findViewById(R.id.submit_policy_btn);
        policy_duration = view.findViewById(R.id.policy_duration);

        policy_duration.setText(duration);


        submit_policy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txt_guarantee.getText().toString())) {
                    txt_guarantee.setError("Enter Gurantee");
                } else if (TextUtils.isEmpty(txt_gurantee_amount.getText().toString())) {
                    txt_gurantee_amount.setError("Enter Gurantee Amount");
                }else {

                    if (Constants.isNetworkAvailable(getActivity())) {

                        progressDialog.show();
                        apiInterface.CreatePolicy(post_id, owner_name, ad_name, location, ad_amount, duration, txt_guarantee.getText().toString(), txt_gurantee_amount.getText().toString()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                progressDialog.dismiss();
                                if (response.isSuccessful()) {

                                    str = "";
                                    try {
                                        str = ((ResponseBody) response.body()).string();
                                        JSONObject jsonObject = new JSONObject(str);

                                        if (jsonObject.getString("message").equals("Policy Inserted")) {

                                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                            setFragment(new PostsFragment());


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
                                        JSONObject jsonObject = new JSONObject(str);


                                        Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getActivity(), "" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }else{

                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return view;


    }

    public void setFragment(Fragment fragment){


        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment,fragment);
        fragmentTransaction.commit();

    }
}
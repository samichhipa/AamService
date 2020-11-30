package com.example.aamservice.ui.UploadPost;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aamservice.Adapter.OperatorAdapter;
import com.example.aamservice.Adapter.ShowPostAdapter;
import com.example.aamservice.R;
import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OperatorFragment extends Fragment {


    RecyclerView operator_recyclerview;
    ApiInterface apiInterface;
    String str="";
    JSONArray jsonArray;

    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_operator, container, false);

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        apiInterface= Constants.GetAPI();

        operator_recyclerview=view.findViewById(R.id.operator_recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        operator_recyclerview.setLayoutManager(linearLayoutManager);
        if (Constants.isNetworkAvailable(getActivity())) {
            getOperators();
        }else{
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void getOperators() {

        progressDialog.show();

        apiInterface.Operators().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    try {
                        str = ((ResponseBody) response.body()).string();
                        JSONObject jsonObject = new JSONObject(str);
                        jsonArray = null;
                        jsonArray = jsonObject.getJSONArray("Operators");


                        if (jsonArray.length()>0) {

                            OperatorAdapter operatorAdapter = new OperatorAdapter(jsonArray, getActivity());
                            operator_recyclerview.setAdapter(operatorAdapter);
                            operator_recyclerview.setVisibility(View.VISIBLE);
                            operatorAdapter.notifyDataSetChanged();

                        }else{
                            progressDialog.dismiss();
                            operator_recyclerview.setVisibility(View.GONE);
                        }


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
            }
        });
    }
}
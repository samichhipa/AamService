package com.example.aamservice.ui.posts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class PostsFragment extends Fragment {


    RecyclerView recyclerView;
    Spinner category_spinner;
    ApiInterface apiInterface;
    String selected_category;
    String str = "";
    JSONArray cat_list=new JSONArray();
    JSONArray jsonArray;



    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        apiInterface = Constants.GetAPI();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        recyclerView = root.findViewById(R.id.showpost_recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        category_spinner = root.findViewById(R.id.category_spinner);

        ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, Constants.category);

        category_spinner.setAdapter(category_adapter);


        selected_category = category_spinner.getSelectedItem().toString();


        getPostByCategory(selected_category);
       category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               getPostByCategory(parent.getItemAtPosition(position).toString());
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });


        return root;
    }

    private void getPostByCategory(String selected_category) {

        progressDialog.show();
        apiInterface.ShowAllPosts(selected_category).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    str = "";

                    try {
                        str = ((ResponseBody) response.body()).string();
                        JSONObject jsonObject = new JSONObject(str);
                         jsonArray=null;
                         jsonArray=jsonObject.getJSONArray("Posts");





                        ShowPostAdapter showPostAdapter=new ShowPostAdapter(jsonArray,getActivity());
                        recyclerView.setAdapter(showPostAdapter);
                        showPostAdapter.notifyDataSetChanged();



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
                Log.d("TAG", t.getMessage());
            }
        });
    }
}
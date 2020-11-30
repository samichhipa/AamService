package com.example.aamservice.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aamservice.InvoiceActivity;
import com.example.aamservice.R;
import com.example.aamservice.Retrofit.ApiInterface;
import com.example.aamservice.Retrofit.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.ViewHolder> {
    JSONArray jsonArray;
    Context context;
    SharedPreferences preferences;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    String operator_id;
    String str="";

    public OperatorAdapter(JSONArray jsonArray, Context context) {
        this.jsonArray = jsonArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.operator_layout,parent,false);
        return new OperatorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        preferences=context.getSharedPreferences("PREF",Context.MODE_PRIVATE);
        apiInterface=Constants.GetAPI();
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        try {
            final JSONObject object=jsonArray.getJSONObject(position);


            operator_id=object.getString("id");
            holder.txt_lname.setText(object.getString("last_name"));
            holder.txt_fname.setText(object.getString("first_name"));
            holder.txt_city.setText(object.getString("city"));
            holder.txt_charge.setText(object.getString("charge_per_hour"));
            holder.txt_prof.setText(object.getString("profession"));
            holder.txt_services.setText(object.getString("services"));



        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btn_confirm_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (preferences.getString("purpose","").equals("tenant")) {

                    if (Constants.isNetworkAvailable(context)) {


                        progressDialog.show();

                        apiInterface.Invoice("Invoice", operator_id, preferences.getString("id", "")).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    str = "";
                                    try {
                                        str = ((ResponseBody) response.body()).string();
                                        JSONObject object = new JSONObject(str);

                                        if (object.getString("status").equals("Successful")) {

                                            Intent intent = new Intent(context, InvoiceActivity.class);
                                            intent.putExtra("first_name", object.getString("first_name"));
                                            intent.putExtra("last_name", object.getString("last_name"));
                                            intent.putExtra("email", object.getString("email"));
                                            intent.putExtra("contact_number", object.getString("contact_number"));
                                            intent.putExtra("address", object.getString("address"));
                                            intent.putExtra("charge_per_hour", object.getString("charge_per_hour"));
                                            intent.putExtra("city", object.getString("city"));
                                            intent.putExtra("age", object.getString("age"));
                                            context.startActivity(intent);

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });


                    } else {

                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Only tenant can hire Operator", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_fname,txt_lname,txt_services,txt_prof,txt_charge,txt_city;
        Button btn_confirm_operator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_fname=itemView.findViewById(R.id.operator_fname);
            txt_lname=itemView.findViewById(R.id.perator_lname);
            txt_services=itemView.findViewById(R.id.operator_services);
            txt_prof=itemView.findViewById(R.id.operator_profession);
            txt_charge=itemView.findViewById(R.id.operator_charge_per_hr);
            txt_city=itemView.findViewById(R.id.opeartor_city);
            btn_confirm_operator=itemView.findViewById(R.id.confirm_operator_btn);

        }
    }
}

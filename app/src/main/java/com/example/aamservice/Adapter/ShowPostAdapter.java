package com.example.aamservice.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aamservice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowPostAdapter extends RecyclerView.Adapter<ShowPostAdapter.ViewHolder> {

    JSONArray jsonArray;
    Context context;

    public ShowPostAdapter(JSONArray jsonArray, Context context) {
        this.jsonArray = jsonArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_layout,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            JSONObject object=jsonArray.getJSONObject(position);

            holder.txt_post_title.setText(object.getString("title"));
            holder.txt_post_amount.setText("Rs. "+object.getString("amount"));

            Glide.with(context).load("https://scissile-efficiency.000webhostapp.com/uploads/"+object.getString("imagepath")).into(holder.post_img);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        ImageView post_img;
        TextView txt_post_title,txt_post_amount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_img=itemView.findViewById(R.id.post_image);
            txt_post_title=itemView.findViewById(R.id.post_title);
            txt_post_amount=itemView.findViewById(R.id.post_amount_layout);

        }
    }
}

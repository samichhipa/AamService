package com.example.aamservice.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("signin.php")
    Call<ResponseBody> SignInWithEmailPass(@Query("email") String email, @Query("password") String pass);

    @GET("signin.php")
    Call<ResponseBody> SignInWithContactPass(@Query("contact") String email, @Query("password") String pass);

    @FormUrlEncoded
    @POST("signup.php")
    Call<ResponseBody> SignUp(@Field("first_name")String fname,@Field("last_name") String lname,@Field("email") String email, @Field("password") String pass,@Field("confirm_password") String conf_pass,@Field("contact") String contact, @Field("address") String address,@Field("city") String city, @Field("age") String age,@Field("gender") String gender, @Field("purpose") String purpse);


    @FormUrlEncoded
    @POST("ad_post.php")
    Call<ResponseBody> SaveImage(@Field("title") String title, @Field("amount") String amount, @Field("image") String image,@Field("description") String description,@Field("category") String category,@Field("location") String location, @Field("start_time") String start_time,@Field("end_time") String end_time
    ,@Field("contact") String contact,@Field("owner_id") String owner_id);

    @GET("show_all_post.php")
    Call<ResponseBody> ShowAllPosts(@Query("category") String category);



}
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
    @Multipart
    Call<ResponseBody> SaveImage(@Part("title") String title, @Part("amount") String amount, @Part MultipartBody.Part img_file,@Part("description") String description, @Part("message") String message,@Part("location") String location, @Part("start_time") String start_time,@Part("end_time") String end_time,@Part("category") String category);

   // @POST("upload_image.php")
   // Call<ResponseBody> SaveImage(@Body RequestBody requestBody);






}
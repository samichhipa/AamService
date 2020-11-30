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
    Call<ResponseBody> AddPost(@Field("title") String title, @Field("amount") String amount, @Field("image") String image,@Field("description") String description,@Field("category") String category,@Field("location") String location, @Field("start_time") String start_time,@Field("end_time") String end_time
    ,@Field("contact") String contact,@Field("owner_id") String owner_id);

    @GET("show_all_post.php")
    Call<ResponseBody> ShowAllPosts(@Query("category") String category);

    @GET("confirm_policy.php")
    Call<ResponseBody> ConfirmPolicy(@Query("post_id") String post_id,@Query("tenant") String tenant,@Query("tenant_name") String tenant_name,@Query("lend_time") String lend_time,
                                     @Query("return_time") String return_time,@Query("tenant_confirm") String tenant_confirm);

    @GET("show_policy.php")
    Call<ResponseBody> ShowPolicy(@Query("post_id") String post_id);


    @GET("create_invoice.php")
    Call<ResponseBody> CreateInvoice(@Query("post_id") String post_id);

    @GET("operator.php")
    Call<ResponseBody> Operators();

    @GET("invoice.php")
    Call<ResponseBody> Invoice(@Query("invoice") String invoice,@Query("operator_id") String operator_id,@Query("tenant_id") String tenant_id);


    @GET("invoice.php")
    Call<ResponseBody> InvoiceForTenant(@Query("post_id") String post_id,@Query("invoice") String invoice,@Query("purpose") String purpose);

    @GET("policy.php")
    Call<ResponseBody> CreatePolicy(@Query("post_id") String post_id,@Query("owner_name") String owner_name,@Query("ad_name") String ad_name,@Query("location") String location
    ,@Query("ad_amount") String ad_amount,@Query("duration") String duration,@Query("guarantee") String guarantee,@Query("guarantee_amount") String guarantee_amount);
}
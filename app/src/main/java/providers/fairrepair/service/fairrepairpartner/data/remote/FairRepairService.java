package providers.fairrepair.service.fairrepairpartner.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import providers.fairrepair.service.fairrepairpartner.data.model.SignInResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by admin on 11/21/2016.
 */

public interface FairRepairService {
    String ENDPOINT = "http://fairrepair.onsisdev.info/providerapi/";

    @POST("signup")
    @Multipart
    Call<SignInResponse> signUp(@PartMap Map<String, RequestBody> requestMap);

    @POST("login")
    @FormUrlEncoded
    Call<SignInResponse> login(@FieldMap Map<String, String> params);

    @POST("forgotpassword")
    @FormUrlEncoded
    Call<SignInResponse> forgotPassword(@FieldMap Map<String, String> params);

    @POST("logout")
    @FormUrlEncoded
    Call<SignInResponse> logout(@FieldMap Map<String, String> params);

    @POST("getprofile")
    @FormUrlEncoded
    Call<SignInResponse> getProfile(@FieldMap Map<String, String> params);

    @POST("editprofile")
    @Multipart
    Call<SignInResponse> editProfile(@PartMap Map<String, RequestBody> params);

    @POST("changepassword")
    @FormUrlEncoded
    Call<SignInResponse> resetPassword(@FieldMap Map<String, String> params);

    @POST("staticpages")
    @FormUrlEncoded
    Call<SignInResponse> getStaticPages(@FieldMap Map<String, String> params);

    @POST("getservicetype")
    @FormUrlEncoded
    Call<SignInResponse> getServiceType(@FieldMap Map<String, String> params);

    @POST("changeAvailability")
    @FormUrlEncoded
    Call<SignInResponse> changeAvailability(@FieldMap Map<String, String> params);

    @POST("requestaccept")
    @FormUrlEncoded
    Call<SignInResponse> acceptRequest(@FieldMap Map<String, String> requestMap);

    @POST("cancelrequest")
    @FormUrlEncoded
    Call<SignInResponse> cancelRequest(@FieldMap Map<String, String> requestMap);

    @POST("billing")
    @FormUrlEncoded
    Call<SignInResponse> generateBill(@FieldMap Map<String, String> requestMap);

    @POST("completerequest")
    @FormUrlEncoded
    Call<SignInResponse> completeRequest(@FieldMap Map<String, String> requestMap);

    /********
     * Factory class that sets up a new ribot services
     *******/
    class Factory {

        public static FairRepairService makeFairRepairService(Context context) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .addInterceptor(new UnauthorisedInterceptor(context))
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FairRepairService.ENDPOINT)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit.create(FairRepairService.class);
        }
    }
}

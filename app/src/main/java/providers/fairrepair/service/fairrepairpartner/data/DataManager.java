package providers.fairrepair.service.fairrepairpartner.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.LoginActivity;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.app.RegisterActivity;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.data.model.Service;
import providers.fairrepair.service.fairrepairpartner.data.model.SignInResponse;
import providers.fairrepair.service.fairrepairpartner.data.model.UserInfo;
import providers.fairrepair.service.fairrepairpartner.data.remote.FairRepairService;
import providers.fairrepair.service.fairrepairpartner.fragment.AboutFragment;
import providers.fairrepair.service.fairrepairpartner.fragment.MyProfileFragment;
import providers.fairrepair.service.fairrepairpartner.fragment.TermsNConditionDialogFragment;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;
import providers.fairrepair.service.fairrepairpartner.utils.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 11/25/2016.
 */

public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();
    private FairRepairService mApiService;
    private PrefsHelper prefsHelper;
    private Context mContext;
    private RequestCallback mCallback = null;
    public DataManager(Context context) {
        mContext = context;
        mApiService = FairRepairService.Factory.makeFairRepairService(context);
        prefsHelper = new PrefsHelper(context);
    }

    public interface RequestCallback{
        void Data(Object data);
    }
    public void setCallback(RequestCallback mCallback){
        this.mCallback = mCallback;
    }

    public void signUp(Map<String, RequestBody> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.signUp(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    /*UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.HOURLY_CHARGES, userInfo.getHourlyServiceCharge());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.PERSONAL_DESC, userInfo.getPersonalDescription());
                    prefsHelper.savePref(ApplicationMetadata.SERVICE_TYPE, userInfo.getServiceType());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_TOKEN, userInfo.getStripeToken());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());
                    prefsHelper.savePref(ApplicationMetadata.LOGIN, true);*/
                    //launch home screen activity
                    DialogFactory.createRegisterSuccessDialog(mContext,R.string.title_success, "You have registered successfully. Please Login").show();
                    /*Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((RegisterActivity) mContext).finish();*/
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //login
    public void login(final Map<String, String> loginRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.login(loginRequest);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String sessionToken = response.body().getResponseData().getSessTok();
                    prefsHelper.savePref(ApplicationMetadata.SESSION_TOKEN, sessionToken);
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.HOURLY_CHARGES, userInfo.getHourlyServiceCharge());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.PERSONAL_DESC, userInfo.getPersonalDescription());
                    prefsHelper.savePref(ApplicationMetadata.SERVICE_TYPE, userInfo.getServiceType());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_TOKEN, userInfo.getStripeToken());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());
                    prefsHelper.savePref(ApplicationMetadata.LOGIN, true);

                    //launch home screen activity
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((LoginActivity) mContext).finish();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //forgot password
    public void forgotPassword(final Map<String, String> forgotPasswordRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.forgotPassword(forgotPasswordRequest);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_password_changed, response.body().getResponseMsg()).show();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //Logout user
    public void logout(final Map<String, String> logoutRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.logout(logoutRequest);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    PrefsHelper prefsHelper = new PrefsHelper(mContext);
                    String deviveToken = prefsHelper.getPref(ApplicationMetadata.DEVICE_TOKEN);
                    prefsHelper.clearAllPref();
                    prefsHelper.savePref(ApplicationMetadata.DEVICE_TOKEN, deviveToken);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //get profile of the user
    public void getProfile(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getProfile(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.HOURLY_CHARGES, userInfo.getHourlyServiceCharge());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.PERSONAL_DESC, userInfo.getPersonalDescription());
                    prefsHelper.savePref(ApplicationMetadata.SERVICE_TYPE, userInfo.getServiceType());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());

                    Fragment newFragment = MyProfileFragment.newInstance(2,userInfo.getServiceList());
                    ((MainActivity)mContext).addFragmentToStack(newFragment, "my_profile");
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //get profile of the user
    public void editProfile(Map<String, RequestBody> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.editProfile(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.HOURLY_CHARGES, userInfo.getHourlyServiceCharge());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.PERSONAL_DESC, userInfo.getPersonalDescription());
                    prefsHelper.savePref(ApplicationMetadata.SERVICE_TYPE, userInfo.getServiceType());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());

                    Fragment newFragment = MyProfileFragment.newInstance(2,userInfo.getServiceList());
                    ((MainActivity)mContext).addFragmentToStack(newFragment, "my_profile");
                    ((MainActivity)mContext).loadData();

                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //reset password
    public void resetPassword(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.resetPassword(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();

                    Intent intent = new Intent(mContext,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //get static content
    public void getStaticPages(Map<String, String> requestMap, final String type) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getStaticPages(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String content = response.body().getResponseData().getStaticContent();

                    if (type.equals(ApplicationMetadata.ABOUT_MECH)) {
                        //launch about us fragment
                        Fragment newFragment = AboutFragment.newInstance(content);
                        ((MainActivity)mContext).addFragmentToStack(newFragment, "about");
                    } else if (type.equals(ApplicationMetadata.TNC_MECH)) {
                        //show tnc dialog
                        DialogFragment customerDetailFragment = TermsNConditionDialogFragment.newInstance(content);
                        customerDetailFragment.show(((RegisterActivity)mContext).getSupportFragmentManager(), "terms_n_condition");
                    }
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //get services for mechanic
    public void getServiceType(final Map<String, String> requestMap, final String selectedServicesId) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getServiceType(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    progressDialog.dismiss();
                    List<Service> serviceList = response.body().getResponseData().getServiceList();
                    DialogFactory.createMultipleChoiceDialog(mContext,serviceList,selectedServicesId).show();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //change availability
    public void changeAvailability(final Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        if(FairRepairApplication.isVisible)
            progressDialog.show();
        Call<SignInResponse> call = mApiService.changeAvailability(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    //DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.status, response.body().getResponseMsg()).show();
                    mCallback.Data(new Object());
                } else {
                    if(FairRepairApplication.isVisible)
                        DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                if(FairRepairApplication.isVisible)
                    DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //accept request
    public void acceptRequest(final Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        Log.i("sdfsf","1");
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.acceptRequest(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.status, response.body().getResponseMsg()).show();
                    mCallback.Data(ApplicationMetadata.SUCCESS_RESPONSE_STATUS);
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                    mCallback.Data(ApplicationMetadata.FAILURE_RESPONSE_STATUS);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //cancel request
    public void cancelRequest(final Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.cancelRequest(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.status, response.body().getResponseMsg()).show();
                    //mCallback.Data(new Object());
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //cancel request
    public void generateBill(final Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.generateBill(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.status, response.body().getResponseMsg()).show();
                    //mCallback.Data(new Object());
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //Complete request
    public void completeRequest(final Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.completeRequest(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.status, response.body().getResponseMsg()).show();
                    //mCallback.Data(new Object());
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }
}

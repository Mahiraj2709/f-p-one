package providers.fairrepair.service.fairrepairpartner.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.data.model.Service;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.CommonMethods;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;

import static java.lang.Float.parseFloat;

public class RegisterActivity extends BaseActivity {
    private static final int LOGIN_PERMISSIONS_REQUEST = 10;
    @BindView(R.id.viewPagerVertical) ViewPager viewPager;
    private ViewPagerAdapter adapter;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.ll_serviceContainer)
    LinearLayout ll_serviceContainer;
    @BindView(R.id.et_name) EditText et_name;
    @BindView(R.id.et_emailId) EditText et_emailId;
    @BindView(R.id.et_address) EditText et_address;
    @BindView(R.id.et_password) EditText et_password;
    @BindView(R.id.et_confirmPasswd) EditText et_confirmPasswd;
    @BindView(R.id.et_phoneNo) EditText et_phoneNo;
    @BindView(R.id.et_hourlyCharge) EditText et_hourlyCharge;
    @BindView(R.id.et_about_you) EditText et_about_you;
    @BindView(R.id.cb_term_n_condition) CheckBox cb_term_n_condition;
    @BindView(R.id.tv_terms_n_condition) TextView tv_terms_n_condition;
    private Uri imageUri = null;
    private boolean isOnNextPage = false;
    private String selectedServices ="" ;

    private String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_register));
        ButterKnife.bind(this);
        FairRepairApplication.getBus().register(this);
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        tv_terms_n_condition.setPaintFlags(tv_terms_n_condition.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        //this is to prepopulate all text fields
        //setTestData();

        if (hasPermission(permissions[1])) {
            //nothing
        } else {
            requestPermissions(permissions,LOGIN_PERMISSIONS_REQUEST);
        }
    }
    @OnClick(R.id.tv_login)
    public void goToLogin() {
        isOnNextPage = false;
        onBackPressed();
    }

    @OnClick(R.id.tv_terms_n_condition)
    public void showTermsNCondition() {
        PrefsHelper prefsHelper = new PrefsHelper(this);
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.PAGE_IDENTIFIER, ApplicationMetadata.TNC_MECH);
        requestParams.put(ApplicationMetadata.LANGUAGE,  "en");

        DataManager dataManager = new DataManager(this);
        dataManager.getStaticPages(requestParams,ApplicationMetadata.TNC_MECH);
    }

    @OnClick(R.id.btn_next)
    public void goToNext() {

        if (validFields()) {
            viewPager.setCurrentItem(1);
            isOnNextPage = true;
        }
    }

    @OnClick(R.id.btn_register)
    public void registerUser() {

        if (hasPermission(permissions[0])) {
            registerCurrentUser();
        } else {
            requestPermissions(permissions,LOGIN_PERMISSIONS_REQUEST);
        }


    }

    private void registerCurrentUser() {
        if (validSecondPage()) {

            Map<String,RequestBody> requestmap = new HashMap();

            requestmap.put("name",RequestBody.create(MediaType.parse("text/plain"), getString(et_name)));
            requestmap.put("email",RequestBody.create(MediaType.parse("text/plain"), getString(et_emailId)));
            requestmap.put("phone_no",RequestBody.create(MediaType.parse("text/plain"), getString(et_phoneNo)));
            requestmap.put("address",RequestBody.create(MediaType.parse("text/plain"), getString(et_address)));
            requestmap.put("hourly_service_charge",RequestBody.create(MediaType.parse("text/plain"), getString(et_hourlyCharge)));
            requestmap.put("password",RequestBody.create(MediaType.parse("text/plain"), getString(et_password)));
            requestmap.put("personal_description",RequestBody.create(MediaType.parse("text/plain"), getString(et_about_you)));
            requestmap.put("service_type",RequestBody.create(MediaType.parse("text/plain"), selectedServices));
            if (super.currentLocatoin != null) {
                requestmap.put("latitude",RequestBody.create(MediaType.parse("text/plain"),super.currentLocatoin.getLatitude()+""));
                requestmap.put("longitude",RequestBody.create(MediaType.parse("text/plain"), super.currentLocatoin.getLongitude()+""));
            } else {
                requestmap.put("latitude",RequestBody.create(MediaType.parse("text/plain"),"0.0"));
                requestmap.put("longitude",RequestBody.create(MediaType.parse("text/plain"), "0.0"));
            }

            requestmap.put("user_type",RequestBody.create(MediaType.parse("text/plain"), "1"));
            requestmap.put("device_token",RequestBody.create(MediaType.parse("text/plain"), (new PrefsHelper(this).getPref(ApplicationMetadata.DEVICE_TOKEN) != null)?(String)new PrefsHelper(this).getPref(ApplicationMetadata.DEVICE_TOKEN):"df"));
            requestmap.put("device_id",RequestBody.create(MediaType.parse("text/plain"), CommonMethods.getDeviceId(this)));
            requestmap.put("device_type",RequestBody.create(MediaType.parse("text/plain"), "1"));
            requestmap.put("language",RequestBody.create(MediaType.parse("text/plain"), "en"));

            if (imageUri != null) {
                File imageFile = new File(imageUri.getPath());
                requestmap.put("profile_pic\"; filename=\"pp.png\" ",RequestBody.create(MediaType.parse("image/*"), imageFile));
            }

            DataManager manager = new DataManager(this);
            manager.signUp(requestmap);
        }
    }

    private String getString(EditText editText) {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }
    private boolean validSecondPage() {

        if(et_about_you.getText().toString().trim().isEmpty()){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.msg_empty_about).show();
            return false;
        } else if(selectedServices.trim().isEmpty()){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.msg_select_service).show();
            return false;
        } else if(!cb_term_n_condition.isChecked()){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.msg_accept_tnc).show();
            return false;
        }
        return true;
    }

    private boolean validFields() {
        float hourlyCharges = 0.0f;
        try {
            hourlyCharges = Float.parseFloat(et_hourlyCharge.getText().toString());
        } catch (NumberFormatException ne) {

        }
        if (et_name.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this,R.string.title_attention,R.string.msg_invalid_name).show();
            return false;
        } else if (!CommonMethods.isEmailValid(this,et_emailId.getText().toString().trim())) {
            return false;
        } else if (!CommonMethods.isValidPhoneNo(this,et_phoneNo.getText().toString())) {
            return false;
        } else if(et_phoneNo.getText().toString().trim().isEmpty()){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.msg_invalid_phone_no).show();
            return false;
        } else if(et_address.getText().toString().trim().isEmpty()){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.valid_msg_invalid_address).show();
            return false;
        } else if(et_hourlyCharge.getText().toString().trim().isEmpty()){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.valid_msg_hourly_charges).show();
            return false;
        }else if(hourlyCharges < 0.01f){
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.valid_msg_hourly_charges_less_than_zero).show();
            return false;
        }else if (et_password.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.valid_msg_empty_password).show();
            return false;
        } else if (et_password.getText().toString().length() < 6) {
            DialogFactory.createSimpleOkErrorDialog(this,R.string.title_attention,R.string.msg_password_lenght).show();
            return false;
        } else if (et_confirmPasswd.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this,R.string.title_attention,R.string.msg_empty_confirm_password).show();
            return false;
        } else if (et_confirmPasswd.getText().toString().length() < 6) {
            DialogFactory.createSimpleOkErrorDialog(this,R.string.title_attention,R.string.msg_password_lenght).show();
            return false;
        } else if (!et_confirmPasswd.getText().toString().equals(et_password.getText().toString())) {
            DialogFactory.createSimpleOkErrorDialog(this,R.string.title_attention,R.string.password_not_match).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.tv_selectServiceType)
    public void openServiceTypeDialog() {
        PrefsHelper prefsHelper = new PrefsHelper(this);
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.LANGUAGE, "en");

        DataManager dataManager = new DataManager(this);
        dataManager.getServiceType(requestParams,selectedServices);
    }

    @Override
    public void onBackPressed() {
        if (isOnNextPage) {
            viewPager.setCurrentItem(0);
            isOnNextPage = false;
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.image_profile)
    public void getProfileImage() {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @Subscribe
    public void getSelectedItems(ArrayList<Service> selectedTypes) {
        selectedServices ="" ;
        ll_serviceContainer.removeAllViews();

        for (Service service: selectedTypes) {
            if(!service.getName().equals("no_service")) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView tv_serviceName = new TextView(this);
                tv_serviceName.setText(service.getName());
                int horizontalPadding = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                int verticalPadding = (int) getResources().getDimension(R.dimen.vertical_padding);
                tv_serviceName.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
                ll_serviceContainer.addView(tv_serviceName);

                selectedServices = selectedServices +service.getId()+ ",";
            }
        }
    }
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Bitmap thePic = null;
                try {
                    //imagePath = picUri.getPath();
                    thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image_profile.setImageBitmap(thePic);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.register_one;
                    break;
                case 1:
                    resId = R.id.register_two;
                    break;
            }
            return findViewById(resId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOGIN_PERMISSIONS_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    DialogFactory.createSimpleOkErrorDialog(this,
                            R.string.title_permissions,
                            R.string.permission_not_accepted_read_phone_state).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setTestData() {
        et_name.setText("Mahiraj");
        et_emailId.setText("mahiraj@onsinteractive.com");
        et_phoneNo.setText("7065257289");
        et_address.setText("Onsis noida");
        et_hourlyCharge.setText("20");
        et_password.setText("mahiraj");
        et_confirmPasswd.setText("mahiraj");
        et_about_you.setText("my nickname is Maahi");
        cb_term_n_condition.setChecked(true);
    }
}

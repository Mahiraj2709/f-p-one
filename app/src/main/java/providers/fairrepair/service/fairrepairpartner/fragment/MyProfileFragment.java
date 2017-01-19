package providers.fairrepair.service.fairrepairpartner.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.data.model.Service;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.CommonMethods;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;
import providers.fairrepair.service.fairrepairpartner.utils.ViewUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 11/22/2016.
 */

public class MyProfileFragment extends Fragment {
    private static final String TAG = MyProfileFragment.class.getSimpleName();
    private MainActivity activity;
    @BindView(R.id.viewPagerVertical)
    ViewPager viewPager;
    private ViewPagerAdapter adapter;
    @BindView(R.id.ll_serviceContainer)
    LinearLayout ll_serviceContainer;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_emailId)
    EditText et_emailId;
    @BindView(R.id.et_phoneNo)
    EditText et_phoneNo;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.et_hourlyCharge)
    EditText et_hourlyCharge;
    @BindView(R.id.et_about_you)
    EditText et_about_you;
    @BindView(R.id.btn_save)
    Button btn_save;

    private EditText tempEditText = null;
    private Uri imageUri = null;
    private String selectedServices = "";

    public static MyProfileFragment newInstance(int args, ArrayList<Service> services) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle data = new Bundle();
        data.putInt("args", args);
        data.putSerializable("service_list", services);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((TextView) ((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_my_profile));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile_fragment, container, false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        loadDataData();
        return view;
    }

    @OnClick(R.id.btn_next)
    public void goToNext() {
        // moving the screen to next pager item i.e otp screen
        if (validFirstPage()) {

            viewPager.setCurrentItem(1);
        }
        //isOnNextPage = true;
    }

    private boolean validFirstPage() {
        float hourlyCharges = 0.0f;
        try {
            hourlyCharges = Float.parseFloat(et_hourlyCharge.getText().toString());
        } catch (NumberFormatException ne) {

        }
        if (et_name.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_invalid_name).show();
            return false;
        } else if (!CommonMethods.isValidPhoneNo(getContext(), et_phoneNo.getText().toString())) {
            return false;
        } else if (et_phoneNo.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_invalid_phone_no).show();
            return false;
        } else if (et_address.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.valid_msg_invalid_address).show();
            return false;
        } else if (et_hourlyCharge.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.valid_msg_hourly_charges).show();
            return false;
        }else if(hourlyCharges < 0.01f){
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention,R.string.valid_msg_hourly_charges_less_than_zero).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_save)
    public void saveDetail() {

        if (!validFields()) {
            return;
        }
        Map<String, RequestBody> requestmap = new HashMap();

        PrefsHelper prefsHelper = new PrefsHelper(getContext());

        Log.i("ssdesfsf", prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestmap.put(ApplicationMetadata.SESSION_TOKEN, RequestBody.create(MediaType.parse("text/plain"), prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, "")));
        requestmap.put("name", RequestBody.create(MediaType.parse("text/plain"), getString(et_name)));
        requestmap.put("phone_no", RequestBody.create(MediaType.parse("text/plain"), getString(et_phoneNo)));
        requestmap.put("address", RequestBody.create(MediaType.parse("text/plain"), getString(et_address)));
        requestmap.put("hourly_service_charge", RequestBody.create(MediaType.parse("text/plain"), getString(et_hourlyCharge)));
        requestmap.put("personal_description", RequestBody.create(MediaType.parse("text/plain"), getString(et_about_you)));
        requestmap.put("service_type", RequestBody.create(MediaType.parse("text/plain"), selectedServices));
        requestmap.put("language", RequestBody.create(MediaType.parse("text/plain"), "en"));

        if (imageUri != null) {
            File imageFile = new File(imageUri.getPath());
            Log.i(TAG,imageUri.getPath().toString());
            requestmap.put("profile_pic\"; filename=\"pp.png\" ", RequestBody.create(MediaType.parse("image/*"), imageFile));
        }
        //for testing
        prefsHelper.savePref(ApplicationMetadata.TEST_SELECT_TYPES, selectedServices);

        DataManager manager = new DataManager(getContext());
        manager.editProfile(requestmap);
    }

    private boolean validFields() {
        if(et_about_you.getText().toString().trim().isEmpty()){
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention,R.string.msg_empty_about).show();
            return false;
        }else if (selectedServices == null && selectedServices.isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_select_service).show();
            return false;
        }
        return true;
    }

    private String getString(EditText editText) {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }

    @OnClick(R.id.image_profile)
    public void getProfileImage() {
        if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(getActivity());
        }
    }

    @Subscribe
    public void getSelectedItems(ArrayList<Service> selectedTypes) {
        selectedServices = "";
        ll_serviceContainer.removeAllViews();

        for (Service serviceName : selectedTypes) {
            if (!serviceName.getName().equals("no_service")) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView tv_serviceName = new TextView(getContext());
                tv_serviceName.setText(serviceName.getName());
                int horizontalPadding = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                int verticalPadding = (int) getResources().getDimension(R.dimen.vertical_padding);
                tv_serviceName.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
                ll_serviceContainer.addView(tv_serviceName);

                selectedServices = selectedServices + serviceName.getId() + ",";
            }
        }
    }

    @OnClick(R.id.tv_selectServiceType)
    public void openServiceTypeDialog() {
        PrefsHelper prefsHelper = new PrefsHelper(getContext());
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.LANGUAGE, "en");

        DataManager dataManager = new DataManager(getContext());
        dataManager.getServiceType(requestParams,selectedServices);
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
                    resId = R.id.my_profile_one;
                    break;
                case 1:
                    resId = R.id.my_profile_two;
                    break;
            }
            return collection.findViewById(resId);
        }
    }

    private void loadDataData() {
        PrefsHelper prefsHelper = new PrefsHelper(getContext());
        et_name.setText(prefsHelper.getPref(ApplicationMetadata.USER_NAME, ""));
        et_emailId.setText(prefsHelper.getPref(ApplicationMetadata.USER_EMAIL, ""));
        et_phoneNo.setText(prefsHelper.getPref(ApplicationMetadata.USER_MOBILE, ""));
        et_address.setText(prefsHelper.getPref(ApplicationMetadata.ADDRESS, ""));
        et_hourlyCharge.setText(prefsHelper.getPref(ApplicationMetadata.HOURLY_CHARGES, ""));
        et_about_you.setText(prefsHelper.getPref(ApplicationMetadata.PERSONAL_DESC, ""));

        //load image with glide
        Glide.with(this)
                .load(ApplicationMetadata.IMAGE_BASE_URL + prefsHelper.getPref(ApplicationMetadata.USER_IMAGE))
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image_profile);

        //test data for selected types
        getSelectedItems((ArrayList<Service>) getArguments().getSerializable("service_list"));
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.i(TAG, "image picker");
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getContext(), imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "image cropped");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Bitmap thePic = null;
                try {
                    //imagePath = picUri.getPath();
                    thePic = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
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
                .start(getActivity());
    }

    @OnClick(R.id.iv_editName)
    public void editName() {
        disableLastEditText(et_name);
        et_name.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_name);
    }

    @OnClick(R.id.iv_editPhoneNo)
    public void editPhoneNo() {
        disableLastEditText(et_phoneNo);
        et_phoneNo.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_phoneNo);
    }

    @OnClick(R.id.iv_editAddress)
    public void editAddress() {
        disableLastEditText(et_address);
        et_address.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_address);
    }

    @OnClick(R.id.iv_editHourlyCharges)
    public void editHourlyCharges() {
        disableLastEditText(et_hourlyCharge);
        et_hourlyCharge.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_hourlyCharge);
    }

    @OnClick(R.id.iv_editAboutUs)
    public void editAboutUs() {
        disableLastEditText(et_about_you);
        et_about_you.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_about_you);
    }

    private void disableLastEditText(EditText currentEditText) {
        if (tempEditText != null) {
            tempEditText.setEnabled(false);
        }
        tempEditText = currentEditText;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FairRepairApplication.getBus().unregister(this);
    }
}

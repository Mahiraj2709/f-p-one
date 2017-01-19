package providers.fairrepair.service.fairrepairpartner.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.fragment.mech_on_way.MechOnWayFragment;
import providers.fairrepair.service.fairrepairpartner.interfaces.AvailabilityCallback;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;
import providers.fairrepair.service.fairrepairpartner.utils.LocationUtils;

/**
 * Created by admin on 11/22/2016.
 */

public class HomeFragment extends Fragment implements AvailabilityCallback{
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 12;
    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.sb_availability)
    SwitchButton sb_availability;
    @BindView(R.id.fl_availability)
    FrameLayout fl_availability;
    private static View view;
    private boolean setChecked = false;
    private GoogleMap mMap;
    private MapFragment mapFragment = null;
    @BindView(R.id.iv_currentLocation)
    ImageView iv_currentLocation;
    private MainActivity activity;
    private Customer customer;

    public static HomeFragment newInstance(int args) {
        HomeFragment fragment = new HomeFragment();
        Bundle data = new Bundle();
        data.putInt("args", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        String notificationData = getActivity().getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        int notificationType = getActivity().getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE,-1);
        if (notificationData != null && notificationType == 1) { // NEW OFFER FROM CUSTOMER
            customer = new Gson().fromJson(notificationData, Customer.class);
            CustomerDetailFragment customerDetailFragment = CustomerDetailFragment.newInstance(customer);
            customerDetailFragment.show(getActivity().getSupportFragmentManager(), "customer_detail");
        } else if(notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_OFFER_ACCEPTED){// OFFER ACCEPTED BY CUSTOMER

            Fragment fragment = MechOnWayFragment.newInstance(notificationData);
            ((MainActivity)context).addFragmentToStack(fragment,"mech_on_way");
        }
        //set availability listener
        ((MainActivity)context).setOnAvailabilityChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.content_main, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        ButterKnife.bind(this, view);
        if (FairRepairApplication.isAvailable) {
            sb_availability.setChecked(true);
        }
        initGoogleMap();
        fl_availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefsHelper prefsHelper = new PrefsHelper(getContext());
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
                requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));

                if (sb_availability.isChecked()) {
                    requestParams.put(ApplicationMetadata.APP_STATUS, ApplicationMetadata.NOT_AVAILABLE);
                    setChecked = false;
                } else {
                    requestParams.put(ApplicationMetadata.APP_STATUS, ApplicationMetadata.AVAILABLE);
                    setChecked = true;
                }
                DataManager dataManager = new DataManager(getContext());
                dataManager.setCallback(new DataManager.RequestCallback() {
                    @Override
                    public void Data(Object data) {
                        sb_availability.setChecked(setChecked);
                        FairRepairApplication.isAvailable = setChecked;
                    }
                });
                dataManager.changeAvailability(requestParams);
            }
        });
        return view;
    }

    private void initGoogleMap() {
        //permission for accessing the location
        LocationUtils locationUtils = new LocationUtils(getActivity());
        locationUtils.showSettingDialog();
        mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        //show current location on the map
    }

    @OnClick(R.id.iv_currentLocation)
    public void moveToMyLocation() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude())));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    //hide the default location button on the map
                    //show myLocation Button here
                    iv_currentLocation.setVisibility(View.VISIBLE);
                } else {
                    DialogFactory.createSimpleOkErrorDialog(getActivity(),
                            R.string.title_permissions,
                            R.string.permission_not_accepted_access_location).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void changeAvailability() {
        sb_availability.setChecked(FairRepairApplication.isAvailable);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}

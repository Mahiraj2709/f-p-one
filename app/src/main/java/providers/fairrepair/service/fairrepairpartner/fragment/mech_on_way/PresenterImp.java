package providers.fairrepair.service.fairrepairpartner.fragment.mech_on_way;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.fragment.BillingFragment;
import providers.fairrepair.service.fairrepairpartner.model.OfferAccepted;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;

import static android.R.attr.fragment;

/**
 * Created by admin on 1/2/2017.
 */

public class PresenterImp implements Presenter, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = PresenterImp.class.getSimpleName();
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private String mLastUpdateTime;
    private MechOnWayView view = null;
    private FragmentActivity activity = null;
    private Context context = null;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    private OfferAccepted offerAccepted = null;
    private Timer timer = new Timer();
    private LatLng mLatLng = null;
    private Map<String,String> requestMap = new HashMap<>();
    private Bundle customerData = null;

    public PresenterImp(MechOnWayView view, FragmentActivity fragmentActivity, Context context) {
        if (view == null) throw new NullPointerException("view can not be NULL");
        if (fragmentActivity == null)
            throw new NullPointerException("AppCompactActivity can not be NULL");
        if (context == null) throw new NullPointerException("context can not be NULL");

        this.view = view;
        activity = fragmentActivity;
        this.context = context;
        prefsHelper = new PrefsHelper(context);
        dataManager = new DataManager(context);
        buildGoogleApiClient();
        this.view.generateMap();
    }

    @Override
    public void onMapReady() {

    }

    @Override
    public void setOffer(Bundle bundle) {
        this.customerData = bundle;
        OfferAccepted offer = new OfferAccepted();
        offer.profile_pic = bundle.getString(ApplicationMetadata.USER_IMAGE);
        offer.latitude = bundle.getString(ApplicationMetadata.LATITUDE);
        offer.longitude = bundle.getString(ApplicationMetadata.LONGITUDE);
        offer.request_id = bundle.getString(ApplicationMetadata.REQUEST_ID);
        offer.message = bundle.getString(ApplicationMetadata.MESSAGE);
        offer.customer_id = bundle.getString(ApplicationMetadata.CUSTOMER_ID);
        offer.phone_no = bundle.getString(ApplicationMetadata.USER_MOBILE);
        offer.location = bundle.getString(ApplicationMetadata.LOCATION);
        this.offerAccepted = offer;
        view.setView(this.offerAccepted);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.getLatitude() + " longitude " + location.getLongitude());
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.i(TAG, "last update time for location is" + mLastUpdateTime);

        //move map to the current location
        //moveToLatLng();
        LatLng mechCurrentLoc = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
        mLatLng = mechCurrentLoc;
        view.setMap(mechCurrentLoc,new LatLng(Double.parseDouble(offerAccepted.latitude), Double.parseDouble(offerAccepted.longitude)));
        upldateLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
        //got the the current location for the first time
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }


    @Override
    public void connectToGoogleApiClient() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        timer.cancel();
    }

    @Override
    public void onPause() {
        stopLocationUpdates();
    }

    @Override
    public void iHaveArrived() {
        /*requestMap.clear();
        requestMap.put(ApplicationMetadata.REQUEST_ID,offerAccepted.request_id);
        requestMap.put(ApplicationMetadata.APP_CUSTOMER_ID,offerAccepted.customer_id);
        requestMap.put(ApplicationMetadata.SESSION_TOKEN,prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        if (mLatLng == null) {
            return;
        }
        requestMap.put(ApplicationMetadata.LATITUDE,mLatLng.latitude+"");
        requestMap.put(ApplicationMetadata.LONGITUDE,mLatLng.longitude+"");
        requestMap.put(ApplicationMetadata.LANGUAGE,"en");
        dataManager.arrived(requestMap);
        dataManager.setmArricedCallback(new DataManager.ArrivedCallback() {
            @Override
            public void arrived() {
            }
        });*/
        view.iHaveArrived();

    }

    @Override
    public void finishTask() {
        requestMap.clear();
        requestMap.put(ApplicationMetadata.REQUEST_ID,offerAccepted.request_id);
        requestMap.put(ApplicationMetadata.APP_CUSTOMER_ID,offerAccepted.customer_id);
        requestMap.put(ApplicationMetadata.SESSION_TOKEN,prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        if (mLatLng == null) {
            return;
        }
        requestMap.put(ApplicationMetadata.LATITUDE,mLatLng.latitude+"");
        requestMap.put(ApplicationMetadata.LONGITUDE,mLatLng.longitude+"");
        requestMap.put(ApplicationMetadata.LANGUAGE,"en");
        dataManager.arrived(requestMap);
        dataManager.setmArricedCallback(new DataManager.ArrivedCallback() {
            @Override
            public void arrived(String serviceCharge) {
                timer.cancel();
                customerData.putString(ApplicationMetadata.SERVICE_CHARGE,serviceCharge);
                Fragment fragment = BillingFragment.newInstance(customerData);
                ((MainActivity)activity).addFragmentToStack(fragment,"billing_fragment");
            }
        });


    }

    private void upldateLocation() {

        requestMap.clear();
        requestMap.put(ApplicationMetadata.CUSTOMER_ID,offerAccepted.customer_id);
        requestMap.put(ApplicationMetadata.SESSION_TOKEN,prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        if (mLatLng == null) {
            return;
        }

        requestMap.put(ApplicationMetadata.LATITUDE,mLatLng.latitude+"");
        requestMap.put(ApplicationMetadata.LONGITUDE,mLatLng.longitude+"");
        requestMap.put(ApplicationMetadata.LANGUAGE,"en");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //call update location every 5 minutes
                dataManager.updateLatLng(requestMap);
                dataManager.setLocationUpdateListener(new DataManager.LocationUpdateListener() {
                    @Override
                    public void locationUpdated() {

                    }
                });

            }
        },0,2 * 60 * 1000);
    }

}

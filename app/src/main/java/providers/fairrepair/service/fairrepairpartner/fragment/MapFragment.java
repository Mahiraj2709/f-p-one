package providers.fairrepair.service.fairrepairpartner.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;

/**
 * Created by MAHIRAJ on 3/11/2016.
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener,CustomerDetailFragment.RequestCompletedCallback {
    private static final String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private String mLastUpdateTime;
    //send LatLng to the calling class
    private LatLngListener latLngListener = null;
    private static final long INTERVAL = 1000 * 60 * 1 * 60; //60 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1 * 60; // 60 minute
    private Customer customer = null;


    //private void setMapFirstTime


    public interface LatLngListener {
        void sendLatLng(LatLng latLng);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildGoogleApiClient();
        getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.i(TAG, "map is ready");
        if (customer != null)
            showCustomerOnMap(customer);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
        moveToLatLng(new LatLng(location.getLatitude(), location.getLongitude()), "NYC");
        showCustomerOnMap(customer);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void initCamera(Location location) {

        if (location == null) {
            return;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void moveToLatLng() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    private void moveToLatLng(LatLng latLng, String placeName) {
        if (mMap != null) {
            mMap.clear();
            //add inner circle
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(150)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            //add outer circle
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(300)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_indicator_red)));
            /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    DialogFragment customerDetailFragment = CustomerDetailFragment.newInstance(1);
                    customerDetailFragment.show(getActivity().getSupportFragmentManager(), "customer_detail");
                    return false;
                }
            });*/
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    private String getAddressFromLatLong(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity());
        String address = "";
        String state = "", city = "", pincode = "";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
            state = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getLocality();
            city = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAdminArea();
            pincode = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getPostalCode();
            Log.i("ADDRESS", address + "---" + state + "-----" + city + "=====" + pincode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
    //this method set the current position on the map in the edit text box of the activity

    private void setLatLangToCurrentPosition(LatLng latLng) {
        //mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

    }


    //set map to the entered address in inputbox
    public void showLatLngOnMap(LatLng latLng, String name) {
        // Clears all the existing markers
        mMap.clear();
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        // Getting a place from the places list
        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker
        markerOptions.title(name);

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);

        // Locate the first location

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        //this method set the current location on map to lat and long edit text in activity
        setLatLangToCurrentPosition(latLng);

    }

    public void currenLocation() {
        // Clears all the existing markers
        if (mLocation != null) {
            //pass the lat lng to the calling class
            latLngListener.sendLatLng(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }
        mMap.clear();
        if (mLocation == null) {
            Toast.makeText(getActivity(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Getting a place from the places list

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);

        // Locate the first location

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        //this method set the current location on map to lat and long edit text in activity
        setLatLangToCurrentPosition(latLng);
    }

    public void showCustomerOnMap(final Customer customer) {
        this.customer = customer;

        if (customer == null) {
            if (mLocation != null && mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()), ApplicationMetadata.MAP_ZOOM_VALUE));
            }
            return;
        }

        LatLng customerLatLng = new LatLng(Double.parseDouble(customer.latitude), Double.parseDouble(customer.longitude));
        if (mMap != null) {
            mMap.clear();
            //add inner circle
            mMap.addCircle(new CircleOptions()
                    .center(customerLatLng)
                    .radius(150)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            //add outer circle
            mMap.addCircle(new CircleOptions()
                    .center(customerLatLng)
                    .radius(300)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            mMap.addMarker(new MarkerOptions().position(customerLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_indicator_red)));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    CustomerDetailFragment customerDetailFragment = CustomerDetailFragment.newInstance(customer);
                    customerDetailFragment.show(getActivity().getSupportFragmentManager(), "customer_detail");
                    customerDetailFragment.setRequestCompletedCallback(MapFragment.this);
                    return false;
                }
            });
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    //listener for the latlng
    public void setLatLngListener(LatLngListener listener) {
        this.latLngListener = listener;
    }

    @Override
    public void reqeustCompleted() {
        Toast.makeText(getContext(), "Request Completed", Toast.LENGTH_SHORT).show();
        if (mMap != null) {
            mMap.clear();
        }
    }
}
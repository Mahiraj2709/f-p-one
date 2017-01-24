package providers.fairrepair.service.fairrepairpartner.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.fragment.CustomerDetailFragment;
import providers.fairrepair.service.fairrepairpartner.fragment.HomeFragment;
import providers.fairrepair.service.fairrepairpartner.fragment.ResetPasswordFragment;
import providers.fairrepair.service.fairrepairpartner.fragment.mech_on_way.MechOnWayFragment;
import providers.fairrepair.service.fairrepairpartner.interfaces.AvailabilityCallback;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.CommonMethods;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;
import providers.fairrepair.service.fairrepairpartner.utils.ViewUtil;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CircleImageView image_profile;
    private TextView tv_userName;
    private int mStackLevel = 1;
    DataManager dataManager = null;
    private PrefsHelper prefsHelper = null;
    private Map<String, String> requestParams = new HashMap<>();
    private AvailabilityCallback availabilityCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FairRepairApplication.getBus().register(this);
        prefsHelper = new PrefsHelper(this);
        dataManager = new DataManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tv_toolbarHeader = (TextView) findViewById(R.id.tv_toolbarHeader);
        tv_toolbarHeader.setText(getString(R.string.title_home));

        //if user click notifiction then hide nofitication
        CommonMethods.cancelNotification(this, 101);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tv_userName = (TextView) header.findViewById(R.id.tv_userName);
        image_profile = (CircleImageView) header.findViewById(R.id.image_profile);

        //load initial data from shared prefereces
        loadData();
        //make mechanic available
        setMechAvailable();

        if (savedInstanceState == null) {
            Fragment newFragment = HomeFragment.newInstance(mStackLevel);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fl_container, newFragment, "map_fragment").commit();
        } else {
            mStackLevel = savedInstanceState.getInt("level");
        }
    }

    private void setMechAvailable() {
        requestParams.clear();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.APP_STATUS, ApplicationMetadata.AVAILABLE);

        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                FairRepairApplication.isAvailable = true;
                if (availabilityCallback != null) {
                    availabilityCallback.changeAvailability();
                }
            }
        });
        dataManager.changeAvailability(requestParams);
    }

    public void loadData() {
        //set name
        tv_userName.setText(prefsHelper.getPref(ApplicationMetadata.USER_NAME, "NO NAME"));

        Glide.with(this)
                .load(ApplicationMetadata.IMAGE_BASE_URL + prefsHelper.getPref(ApplicationMetadata.USER_IMAGE))
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image_profile);
    }

    private Fragment lastFragment = null;

    @SuppressWarnings("ResourceType")
    public void addFragmentToStack(Fragment fragment, String tag) {
        //hide soft keyboard if visible
        ViewUtil.hideKeyboard(this);
        mStackLevel++;
        // Add the fragment to the activity, pushing this transaction
        // on to the back stack.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (lastFragment != null)
            transaction.remove(lastFragment);
        transaction.replace(R.id.fl_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();

        lastFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        ((TextView) findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_home));
        Log.i("back_pressed", getSupportFragmentManager().getBackStackEntryCount() + "");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                DialogFactory.createExitDialog(this);
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                    for (int i = 0; i < backStackCount; i++) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(i).getId(), getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                    }
                } else
                    super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < backStackCount; i++) {
                    getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(i).getId(), getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                }
            }
        } else if (id == R.id.nav_myProfile) {
            //DialogFactory.createComingSoonDialog(this);
            requestParams.clear();
            requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
            requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
            dataManager.getProfile(requestParams);

        } else if (id == R.id.nav_serviceHistory) {
            DialogFactory.createComingSoonDialog(this);
            /*Fragment newFragment = ServiceHistoryFragment.newInstance(3);
            addFragmentToStack(newFragment, "service_history");*/
        } else if (id == R.id.nav_resetPassword) {
            //DialogFactory.createComingSoonDialog(this);
            Fragment newFragment = ResetPasswordFragment.newInstance(4);
            addFragmentToStack(newFragment, "reset_passord");
        } else if (id == R.id.nav_about) {
//            DialogFactory.createComingSoonDialog(this);
            requestParams.clear();
            requestParams.put(ApplicationMetadata.PAGE_IDENTIFIER, ApplicationMetadata.ABOUT_MECH);
            requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
            dataManager.getStaticPages(requestParams, ApplicationMetadata.ABOUT_MECH);
        } else if (id == R.id.nav_logout) {
            DialogFactory.createLogoutDialog(this, R.string.logout, R.string.logout_confirm).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //make mechanic unavailable
        requestParams.clear();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.APP_STATUS, ApplicationMetadata.NOT_AVAILABLE);

        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                FairRepairApplication.isAvailable = false;
            }
        });
        dataManager.changeAvailability(requestParams);
    }

    public void setOnAvailabilityChangeListener(AvailabilityCallback listener) {
        this.availabilityCallback = listener;
    }

    @Subscribe
    public void receiveNotification(Customer customer) {
        Log.i("firebase","inside subscirove");
        int notificationType = Integer.parseInt(customer.notification_type);
        if (notificationType == ApplicationMetadata.NOTIFICATION_NEW_OFFER) {
            //new offer notification for the customer
            CustomerDetailFragment customerDetailFragment = CustomerDetailFragment.newInstance(customer);
            customerDetailFragment.show(getSupportFragmentManager(), "customer_detail");
        } else if (notificationType == ApplicationMetadata.NOTIFICATION_OFFER_ACCEPTED) {
            //request has been accepted
            Log.i("firebase","inside accepted request");
            Bundle bundle = new Bundle();
            bundle.putString(ApplicationMetadata.LATITUDE,customer.latitude);
            bundle.putString(ApplicationMetadata.LONGITUDE,customer.longitude);
            bundle.putString(ApplicationMetadata.REQUEST_ID,customer.request_id);
            bundle.putString(ApplicationMetadata.USER_IMAGE,customer.profile_pic);
            bundle.putString(ApplicationMetadata.MESSAGE,customer.message);
            bundle.putString(ApplicationMetadata.CUSTOMER_ID,customer.customer_id);
            bundle.putString(ApplicationMetadata.USER_MOBILE,customer.phone_no);
            bundle.putString(ApplicationMetadata.LOCATION,customer.location);
            Fragment fragment = MechOnWayFragment.newInstance(bundle);
            addFragmentToStack(fragment,"mech_on_way");
        }
    }
}

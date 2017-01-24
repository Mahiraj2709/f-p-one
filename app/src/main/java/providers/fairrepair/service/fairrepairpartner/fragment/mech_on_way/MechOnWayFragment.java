package providers.fairrepair.service.fairrepairpartner.fragment.mech_on_way;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.model.OfferAccepted;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.LocationUtils;

/**
 * Created by admin on 12/29/2016.
 */

public class MechOnWayFragment extends Fragment implements OnMapReadyCallback, MechOnWayView {
    private static final String TAG = MechOnWayFragment.class.getSimpleName();
    private MainActivity activity;
    private GoogleMap map;
    private Presenter presenter;
    @BindView(R.id.tv_phoneNo)
    TextView tv_phoneNo;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_requestId)
    TextView tv_requestId;
    @BindView(R.id.image_profile)
    CircleImageView imageProfile;
    @BindView(R.id.btn_arrived) TextView btn_arrived;
    @BindView(R.id.btn_finish) TextView btn_finish;
    @BindView(R.id.rl_informationContainer) RelativeLayout rl_informationContainer;

    public static MechOnWayFragment newInstance(Bundle args) {
        MechOnWayFragment fragment = new MechOnWayFragment();
        Bundle data = new Bundle();
        data.putBundle("args", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_i_have_arrived));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mech_on_way_fragment, container, false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        presenter = new PresenterImp(this, getActivity(), getContext());
        Bundle bundle = getArguments().getBundle("args");
        presenter.setOffer(bundle);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FairRepairApplication.getBus().unregister(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        presenter.onMapReady();
    }

    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void setView(OfferAccepted offer) {
        tv_address.setText(offer.location);
        tv_requestId.setText(getString(R.string.request_id) + "-" + offer.request_id);
        tv_phoneNo.setText(offer.phone_no);

        Glide.with(this)
                .load(ApplicationMetadata.CUSTOMER_IMAGE_BASE_URL + offer.profile_pic)
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageProfile);
    }

    @Override
    public void setMap(LatLng mechLatLng, LatLng customerLatLng) {
        if (map != null) {
            map.clear();

            map.addMarker(new MarkerOptions().position(customerLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
            /*map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    DialogFragment customerDetailFragment = CustomerDetailFragment.newInstance(1);
                    customerDetailFragment.show(getActivity().getSupportFragmentManager(), "customer_detail");
                    return false;
                }
            });*/

            //get current lat lng of the mechanic

            List<LatLng> allLatLng = new ArrayList<>();
            allLatLng.add(customerLatLng);
            allLatLng.add(mechLatLng);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.computeCentroid(allLatLng), ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    @Override
    public void iHaveArrived() {
        rl_informationContainer.animate().translationY(-300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rl_informationContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        btn_arrived.setVisibility(View.GONE);
        btn_finish.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_arrived)
    public void arrivedBtn() {
        //I have arrived
        presenter.iHaveArrived();

    }

    @OnClick(R.id.btn_finish)
    public void finishTask() {
        //finish service
        presenter.finishTask();
    }
    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    private Customer testData() {
        Customer customer = new Customer();
        customer.name = "Mahiraj";
        customer.customer_id = "7";
        customer.profile_pic = "c0c0a7131538a36e53e65e723a73d6d1.png";
        customer.longitude = "28.540957";
        customer.latitude = "77.398695";
        customer.need = "no nedd";
        customer.engine_manufacturer = "sdf";
        return customer;
    }

}

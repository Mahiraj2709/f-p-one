package providers.fairrepair.service.fairrepairpartner.fragment;


import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.BaseActivity;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;

/**
 * Created by admin on 11/29/2016.
 */

public class CustomerDetailFragment extends DialogFragment {
    private static final String TAG = CustomerDetailFragment.class.getSimpleName();
    @BindView(R.id.tv_locationName)
    TextView tv_locationName;
    @BindView(R.id.tv_userName)
    TextView tv_userName;
    @BindView(R.id.tv_truckModel)
    TextView tv_truckModel;
    @BindView(R.id.tv_engineManufacturer)
    TextView tv_engineManufacturer;
    @BindView(R.id.tv_vinNo)
    TextView tv_vinNo;
    @BindView(R.id.tv_serviceDesc)
    TextView tv_serviceDesc;
    @BindView(R.id.tv_withTrailer)
    TextView tv_withTrailer;
    @BindView(R.id.tv_serviceLocation)
    TextView tv_serviceLocation;
    @BindView(R.id.tv_serviceTime) TextView tv_serviceTime;
    @BindView(R.id.tv_totalTime) TextView tv_totalTime;
    @BindView(R.id.et_offerPrice)
    EditText et_offerPrice;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.donut_progress) DonutProgress donut_progress;
    private String address = "";
    private String state = "", city = "", pincode = "";
    private Customer customer;
    private RequestCompletedCallback requestCallback;
    private Timer timer;
    private int time = 60;

    public interface RequestCompletedCallback{
        void reqeustCompleted();
    }
    public static CustomerDetailFragment newInstance(Customer customer) {
        CustomerDetailFragment f = new CustomerDetailFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(ApplicationMetadata.NOTIFICATION_DATA, customer);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customer = (Customer) getArguments().getSerializable(ApplicationMetadata.NOTIFICATION_DATA);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, R.style.DialogStyle);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_detial_fragment, container, false);
        ButterKnife.bind(this, v);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set the time from the notification window
        if (FairRepairApplication.timeToAcceptRequest > 0) {
            time = FairRepairApplication.timeToAcceptRequest;
        }
        donut_progress.setUnfinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        donut_progress.setFinishedStrokeColor(getResources().getColor(R.color.colorLineSeperator));
        donut_progress.setUnfinishedStrokeWidth(10.0f);
        donut_progress.setFinishedStrokeWidth(10.0f);
        donut_progress.setTextColor(getResources().getColor(R.color.lightGrey));
        ObjectAnimator anim = ObjectAnimator.ofInt(donut_progress, "progress", 0, 100);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(time * 1000);
        anim.start();

        tv_totalTime.setText("60");
        tv_locationName.setText(customer.location);
        tv_userName.setText(customer.name);
        tv_engineManufacturer.setText((customer.engine_manufacturer != null) ? customer.engine_manufacturer : "Not available");
        tv_truckModel.setText((customer.model != null) ? customer.model : "Not available");
        tv_vinNo.setText((customer.vin != null) ? customer.vin : "Not available");
        tv_serviceDesc.setText((customer.need != null) ? customer.need : "Not available");
        tv_withTrailer.setText((customer.trailer != null) ? customer.trailer : "Not available");
        tv_serviceLocation.setText((customer.service_provide_name != null) ? customer.service_provide_name : "Not available");
        tv_serviceTime.setText(customer.service_time);
        Glide.with(this)
                .load(ApplicationMetadata.CUSTOMER_IMAGE_BASE_URL + customer.profile_pic)
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image_profile);

        setTimer();
        return v;
    }

    @OnClick(R.id.ll_acceptRequest)
    public void acceptRequest() {
        float offerPrice = 0.0f;
        try {
            offerPrice = Float.parseFloat(et_offerPrice.getText().toString());
        } catch (NumberFormatException ne) {

        }
        if (et_offerPrice.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_invalid_offer_price).show();
            return;
        } else if (offerPrice < 0.01f) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_offer_price_zero).show();
            return;
        }
        PrefsHelper prefsHelper = new PrefsHelper(getContext());
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.REQUEST_ID, customer.request_id);
        requestParams.put(ApplicationMetadata.APP_CUSTOMER_ID, customer.customer_id);
        requestParams.put(ApplicationMetadata.OFFER_PRICE, et_offerPrice.getText().toString());
        requestParams.put(ApplicationMetadata.LANGUAGE, "en");
        requestParams.put(ApplicationMetadata.LATITUDE, (((BaseActivity) getActivity()).currentLocatoin != null) ? ((BaseActivity) getActivity()).currentLocatoin.getLatitude() + "" : "0.0");
        requestParams.put(ApplicationMetadata.LONGITUDE, (((BaseActivity) getActivity()).currentLocatoin != null) ? ((BaseActivity) getActivity()).currentLocatoin.getLongitude() + "" : "0.0");
        DataManager dataManager = new DataManager(getContext());
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                if ((int) data == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    //dismiss d
                    CustomerDetailFragment.this.dismiss();
                    timer.cancel();
                } else if ((int) data == ApplicationMetadata.FAILURE_RESPONSE_STATUS) {
                    CustomerDetailFragment.this.dismiss();
                    timer.cancel();

                }
                if (requestCallback != null) {
                    requestCallback.reqeustCompleted();
                }
            }
        });
        dataManager.acceptRequest(requestParams);
    }

    @OnClick(R.id.ll_cancelRequest)
    public void cancelRequest() {
        this.dismiss();

    }

    @OnClick(R.id.iv_closeDialog)
    public void closeDialog() {
        this.dismiss();
        if (requestCallback != null) {
            requestCallback.reqeustCompleted();
        }
    }

    public void setRequestCompletedCallback(RequestCompletedCallback callback) {
        requestCallback = callback;
    }

    private void setTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time -= 1;
                        tv_totalTime.setText(time + "");

                        if (time == 0) {
                            CustomerDetailFragment.this.dismiss();
                            timer.cancel();
                        }
                    }
                });
            }
        },0,1000);
    }
}

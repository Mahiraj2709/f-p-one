package providers.fairrepair.service.fairrepairpartner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;

/**
 * Created by admin on 12/29/2016.
 */

public class YourBillFragment extends Fragment {
    private MainActivity activity;
    private static final String TAG = YourBillFragment.class.getSimpleName();
    @BindView(R.id.tv_totalPrice) TextView tv_totalPrice;
    @BindView(R.id.tv_requestId) TextView tv_requestId;
    @BindView(R.id.tv_serviceName) TextView tv_serviceName;
    @BindView(R.id.tv_servicePrice) TextView tv_servicePrice;
    @BindView(R.id.tv_serviceCharge) TextView tv_serviceCharge;
    @BindView(R.id.tv_serviceChargePrice) TextView tv_serviceChargePrice;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;

    private Bundle customerDetail = null;
    public static YourBillFragment newInstance(Bundle customerDetail) {
        YourBillFragment fragment = new YourBillFragment();
        Bundle data = new Bundle();
        data.putBundle("args", customerDetail);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        prefsHelper = new PrefsHelper(context);
        dataManager = new DataManager(context);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_service_history_detail));
        if (getArguments().getBundle("args") != null) {
            customerDetail = getArguments().getBundle("args");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_bill_fragment, container, false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);

        tv_totalPrice.setText("$"+customerDetail.getString(ApplicationMetadata.TOTAL_PRICE));
        tv_requestId.setText("Request ID-"+customerDetail.getString(ApplicationMetadata.REQUEST_ID));
        tv_serviceName.setText(customerDetail.getString(ApplicationMetadata.SERVICE_DETAIL));
        tv_servicePrice.setText("$"+customerDetail.getString(ApplicationMetadata.BILLING_PRICE));
        tv_serviceCharge.setText(customerDetail.getString(ApplicationMetadata.SERVICE_CHARGE));
        tv_serviceChargePrice.setText("$"+customerDetail.getString(ApplicationMetadata.SERVICE_CHARGE_PRICE));
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FairRepairApplication.getBus().unregister(this);
    }

    @OnClick(R.id.btn_done)
    public void done() {
        final Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, "en"));
        requestParams.put(ApplicationMetadata.TOTAL_PRICE, customerDetail.getString(ApplicationMetadata.TOTAL_PRICE));
        requestParams.put(ApplicationMetadata.REQUEST_ID, customerDetail.getString(ApplicationMetadata.CUSTOMER_ID));
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                Fragment fragment = HomeFragment.newInstance(1);
                ((MainActivity)activity).addFragmentToStack(fragment,"home_fragment");
            }
        });

        dataManager.completeRequest(requestParams);
    }
}

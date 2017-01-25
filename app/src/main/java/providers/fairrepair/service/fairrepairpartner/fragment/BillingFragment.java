package providers.fairrepair.service.fairrepairpartner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import providers.fairrepair.service.fairrepairpartner.data.model.ResponseData;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.DialogFactory;

/**
 * Created by admin on 12/29/2016.
 */

public class BillingFragment extends Fragment {
    private MainActivity activity;
    private static final String TAG = BillingFragment.class.getSimpleName();
    @BindView(R.id.et_serviceName) EditText et_serviceName;
    @BindView(R.id.et_servicePrice) EditText et_servicePrice;
    @BindView(R.id.tv_serviceCharge) TextView tv_serviceCharge;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    private Bundle customerDetail = null;

    public static BillingFragment newInstance(Bundle customerDetail) {
        BillingFragment fragment = new BillingFragment();
        Bundle data = new Bundle();
        data.putBundle("args", customerDetail);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_service_history_detail));

        if (getArguments().getBundle("args") != null) {
            customerDetail = getArguments().getBundle("args");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.billing_fragment, container, false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());

        //set service charge
        tv_serviceCharge.setText(customerDetail.getString(ApplicationMetadata.SERVICE_CHARGE));
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FairRepairApplication.getBus().unregister(this);
    }

    @OnClick(R.id.btn_Submit)
    public void generateBill() {
        if (!validBill()) {
            return;
        }
        final Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, "en"));
        requestParams.put(ApplicationMetadata.BILLING_PRICE, et_servicePrice.getText().toString());
        requestParams.put(ApplicationMetadata.REQUEST_ID, customerDetail.getString(ApplicationMetadata.CUSTOMER_ID));
        requestParams.put(ApplicationMetadata.SERVICE_DETAIL, et_serviceName.getText().toString());
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                ResponseData responseData = (ResponseData) data;
                customerDetail.putString(ApplicationMetadata.SERVICE_DETAIL,et_serviceName.getText().toString());
                customerDetail.putString(ApplicationMetadata.BILLING_PRICE,responseData.getBilling_price());
                customerDetail.putString(ApplicationMetadata.SERVICE_CHARGE_PRICE,responseData.getService_charge());
                customerDetail.putString(ApplicationMetadata.TOTAL_PRICE,responseData.getTotal_price());
                Fragment fragment = YourBillFragment.newInstance(customerDetail);
                ((MainActivity)activity).addFragmentToStack(fragment,"your_bill");
            }
        });

        dataManager.generateBill(requestParams);

    }

    private boolean validBill() {
        float price = 0.0f;
        try {
            price = Float.parseFloat(et_servicePrice.getText().toString());
        } catch (NumberFormatException ne) {
            Log.i(TAG,ne.toString());
        }
        if (et_serviceName.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),"Please enter service name!").show();
            return false;
        }else if (et_servicePrice.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),"Please enter service price!").show();
            return false;
        }else if (price <= 0.0f) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),"Please enter valid service price!").show();
            return false;
        }
        return true;
    }
}

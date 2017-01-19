package providers.fairrepair.service.fairrepairpartner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

public class BillingFragment extends Fragment {
    private MainActivity activity;
    private static final String TAG = BillingFragment.class.getSimpleName();
    @BindView(R.id.et_serviceName) EditText et_serviceName;
    @BindView(R.id.et_servicePrice) EditText et_servicePrice;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    public static BillingFragment newInstance(String requestId) {
        BillingFragment fragment = new BillingFragment();
        Bundle data = new Bundle();
        data.putString("args", requestId);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_service_history_detail));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.billing_fragment, container, false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FairRepairApplication.getBus().unregister(this);
    }

    @OnClick(R.id.btn_Submit)
    public void generateBill() {

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, "en"));
        requestParams.put(ApplicationMetadata.BILLING_PRICE, et_servicePrice.getText().toString());
        requestParams.put(ApplicationMetadata.REQUEST_ID, getArguments().getString("args"));
        requestParams.put(ApplicationMetadata.SERVICE_DETAIL, et_serviceName.getText().toString());
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                Fragment fragment = YourBillFragment.newInstance(0);
                ((MainActivity)activity).addFragmentToStack(fragment,"your_bill");
            }
        });

        dataManager.generateBill(requestParams);

    }
}

package providers.fairrepair.service.fairrepairpartner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.model.ServiceHisrotyResponse;

/**
 * Created by admin on 11/24/2016.
 */

public class ServiceHistoryDetailFragment extends Fragment {
    private MainActivity activity;
    private static final String TAG = ServiceHistoryDetailFragment.class.getSimpleName();
    @BindView(R.id.tv_serviceDate) TextView tv_serviceDate;
    @BindView(R.id.tv_serviceLocation) TextView tv_serviceLocation;
    @BindView(R.id.tv_servicePrice) TextView tv_servicePrice;
    private ServiceHisrotyResponse.ServiceHistory mServiceHistory;
    public static ServiceHistoryDetailFragment newInstance(int args) {
        ServiceHistoryDetailFragment fragment = new ServiceHistoryDetailFragment();
        Bundle data = new Bundle();
        data.putInt("args", args);
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
        View view = inflater.inflate(R.layout.service_history_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        return view;
    }

    @Subscribe
    public void getSelectedServiceHistory(ServiceHisrotyResponse.ServiceHistory serviceHistory) {
        mServiceHistory = serviceHistory;
        Log.i(TAG,serviceHistory.servicePrice + " name" +serviceHistory.serviceName);
        tv_serviceDate.setText(serviceHistory.serviceDate);
        tv_servicePrice.setText(serviceHistory.servicePrice);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FairRepairApplication.getBus().unregister(this);
    }
}
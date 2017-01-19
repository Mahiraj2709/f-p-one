package providers.fairrepair.service.fairrepairpartner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.model.ServiceHisrotyResponse;

public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.RibotHolder> {
    private List<ServiceHisrotyResponse.ServiceHistory> mServiceList;
    public static MyClickListerer myClickListerer;
    public ServiceHistoryAdapter(List<ServiceHisrotyResponse.ServiceHistory> mServiceList) {
        this.mServiceList = mServiceList;
    }

    @Override
    public RibotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service, parent, false);
        return new RibotHolder(view);
    }

    @Override
    public void onBindViewHolder(final RibotHolder holder, final int position) {
        ServiceHisrotyResponse.ServiceHistory service = mServiceList.get(position);
        holder.tv_requestId.setText(service.serviceId);
        holder.tv_serviceDate.setText(service.serviceDate);
        holder.tv_serviceName.setText(service.serviceName);
        holder.tv_servicePrice.setText(service.servicePrice);

    }
    public interface MyClickListerer {
        void onItemClick(int position, View view);
    }

    public void setItemClickListener(MyClickListerer myClickListerer) {
        this.myClickListerer = myClickListerer;
    }

    @Override
    public int getItemCount() {
        return mServiceList.size();
    }

    public void setTeamMembers(List<ServiceHisrotyResponse.ServiceHistory> list) {
        mServiceList = list;
    }

    class RibotHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_requestId) public TextView tv_requestId;

        @BindView(R.id.tv_serviceDate) public TextView tv_serviceDate;

        @BindView(R.id.tv_serviceName) public TextView tv_serviceName;

        @BindView(R.id.tv_servicePrice) public TextView tv_servicePrice;

        public RibotHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListerer.onItemClick(getAdapterPosition(), v);
        }
    }
}
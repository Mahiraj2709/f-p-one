package providers.fairrepair.service.fairrepairpartner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;

/**
 * Created by admin on 11/22/2016.
 */

public class AboutFragment extends Fragment {
    @BindView(R.id.tv_supportText)
    TextView tv_supportText;
    public static AboutFragment newInstance(String content) {
        AboutFragment fragment = new AboutFragment();
        Bundle data = new Bundle();
        data.putString("content",content);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_about));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment,container,false);
        ButterKnife.bind(this, view);

        String content = getArguments().getString("content");
        tv_supportText.setText(Html.fromHtml(content));
        return view;
    }
}

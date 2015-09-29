package com.sensis.voyager.app.menufragments;


import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.sensis.voyager.app.R;
import com.sensis.voyager.app.ioc.BaseFragment;

public class AboutFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.about_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView textView = (TextView) getActivity().findViewById(R.id.textView);
        textView.setText(Html.fromHtml(getResources().getString(R.string.about_paragraph)));
    }

}

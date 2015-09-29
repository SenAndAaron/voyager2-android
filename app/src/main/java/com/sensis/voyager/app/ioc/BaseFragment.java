package com.sensis.voyager.app.ioc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

/**
 * Provides a base class with inversion of control available for View objects.
 * Reduces boilerplate code such as:
 * <code>
 * TextView blah = (TextView) getActivity().findViewById(R.id.blah)
 * </code>
 */
public abstract class BaseFragment extends Fragment {

    protected abstract int getLayoutId();

    protected void onCreateView(View view) {
        // Default: do nothing
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        onCreateView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

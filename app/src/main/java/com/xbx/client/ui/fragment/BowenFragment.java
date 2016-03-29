package com.xbx.client.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by EricYuan on 2016/3/29.
 */
public class BowenFragment extends Fragment {
    private static BowenFragment fragment = null;

    public BowenFragment() {
    }

    public static BowenFragment newInstance() {
        if (fragment == null) {
            fragment = new BowenFragment();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

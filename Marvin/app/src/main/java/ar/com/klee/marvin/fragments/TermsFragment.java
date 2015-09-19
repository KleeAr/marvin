package ar.com.klee.marvin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.com.klee.marvin.R;


public class TermsFragment extends Fragment {
    private View view;

    public TermsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_terms, container, false);

        return view;
    }

}
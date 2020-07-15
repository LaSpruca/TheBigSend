package me.nathan.smsabuse_sf.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.nathan.smsabuse_sf.R;

/**
 * Fragement that displays if the user goes onto a page that does not exsit, i.e. not messages or numbers
 * As it's name suggests not rally useful,
 * Everything be the defaults
 */
public class Wizdary extends Fragment {
    public Wizdary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wizdary, container, false);
    }
}
package com.android.brogrammers.sportsm8.socialTab;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.brogrammers.sportsm8.R;

/**
 * Helper Class to reuse Social Fragment for Memberselection
 */
public class SelectorContainer extends AppCompatActivity implements FragmentSocial.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_container);
        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new FragmentSocial();
        Bundle bundle = getIntent().getExtras();
        bundle.putBoolean("addToMeetingMode", true);
        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.selector_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

package com.cafeteria.cafeteria_client.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cafeteria.cafeteria_client.R;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class SpecialsFragment  extends Fragment {

    public SpecialsFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.specials_fragment, container, false);
    }
}

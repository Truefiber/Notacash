package com.gkartash.notacash;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by Gennadiy on 22.06.2014.
 */
public class MainActivity extends Activity implements StartFragment.OnFindButtonPressed{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new StartFragment();


            fm.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
        }



    }


    @Override
    public void onPress(Bundle settings) {
        FragmentManager fm = getFragmentManager();
        Fragment mapFragment = new AtmMapFragment();
        mapFragment.setArguments(settings);
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, mapFragment)
                .addToBackStack(null)
                .commit();




    }
}

package com.gkartash.notacash;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Gennadiy on 23.06.2014.
 */
public class StartFragment extends Fragment {

    private static final String TAG = "StartFragment";



    SeekBar proximitySeekBar;
    Spinner categorySpinner;
    Spinner subcategorySpinner;
    TextView proximityTextView;
    TextView bankTextView;
    Button findButton;
    List<String> category;
    Map<Integer, List<String>> subCategory;
    AtmManager mAtmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAtmManager = new AtmManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);
        proximitySeekBar = (SeekBar) view.findViewById(R.id.proximitySeekBar);
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        subcategorySpinner = (Spinner) view.findViewById(R.id.subcategorySpinner);
        proximityTextView = (TextView) view.findViewById(R.id.proximityTextView);
        updateProximityTextView(proximitySeekBar.getProgress());
        findButton = (Button) view.findViewById(R.id.findButton);
        findButton.setOnClickListener(findButtonListener);
        proximitySeekBar.setOnSeekBarChangeListener(proximitySeekBarListener);
        initSpinners();


        return view;
    }

    private SeekBar.OnSeekBarChangeListener proximitySeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            updateProximityTextView(i);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private View.OnClickListener findButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //mAtmManager.getATMList(0, 0, 300);

            Location testLocation = new Location(LocationManager.GPS_PROVIDER);
            testLocation.setLatitude(50.405426);
            testLocation.setLongitude(30.631094);

            FragmentManager fm = getActivity().getFragmentManager();
            Fragment amf = new AtmMapFragment();
            Bundle args = new Bundle();
            int proximity = proximitySeekBar.getProgress() * 100 + 100;
            args.putInt(AtmMapFragment.ARG_PROXIMITY, proximity);
            args.putParcelable(AtmMapFragment.ARG_USED_LOCATION, testLocation);
            amf.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, amf)
                    .addToBackStack("AtmMapFragment")
                    .commit();


        }
    };

    private void updateProximityTextView (int value) {
        value = value * 100 + 100;
        String proximity = String.format(getActivity().getString(R.string.proximityTextView),
                value);
        proximityTextView.setText(proximity);
    }

    private void initSpinners() {
        if (category == null) {


        }
    }




}

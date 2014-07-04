package com.gkartash.notacash;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    public interface OnFindButtonPressed {
        public void onPress (Bundle settings);
    }



    SeekBar proximitySeekBar;
    Spinner categorySpinner;
    Spinner subcategorySpinner;
    TextView proximityTextView;
    TextView bankTextView;
    Button findButton;
    List<String> category;
    Map<Integer, List<String>> subCategory;
    AtmManager mAtmManager;
    ProgressDialog dialog;
    OnFindButtonPressed callback;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAtmManager = new AtmManager(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (OnFindButtonPressed) activity;
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
        new DataUpdater().execute();





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

            int category = mAtmManager.getCategoryId(categorySpinner.getSelectedItemPosition());
            int subCategoty = mAtmManager.getSubcategoryId(subcategorySpinner
                    .getSelectedItemPosition());
            int proximity = proximitySeekBar.getProgress() * 100 + 100;

            String url = mAtmManager.makeDownloadURL(category, subCategoty, proximity);
            Bundle settings = new Bundle();
            settings.putParcelable(AtmMapFragment.ARG_LOCATION, mAtmManager.getCurrentLocation());
            settings.putString(AtmMapFragment.ARG_URL_FOR_DOWNLOADING, url);
            settings.putInt(AtmMapFragment.ARG_PROXIMITY, proximity);

            callback.onPress(settings);




        }
    };

    private void updateProximityTextView (int value) {
        value = value * 100 + 100;
        String proximity = String.format(getActivity().getString(R.string.proximityTextView),
                value);
        proximityTextView.setText(proximity);
    }

    private void initSpinners() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, mAtmManager.getCategoryList());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSubcategorySpinner(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private class DataUpdater extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.progress_message));
            dialog.setIndeterminate(true);
            dialog.setTitle(getString(R.string.progress_title));
            dialog.setCancelable(true);
            dialog.show();



        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAtmManager.updateData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            initSpinners();
        }

    }

    private void updateSubcategorySpinner(int categoryId) {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, mAtmManager.getSubcategoryList(categoryId));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategorySpinner.setAdapter(spinnerAdapter);
    }








}

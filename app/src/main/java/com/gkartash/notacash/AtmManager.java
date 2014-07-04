package com.gkartash.notacash;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gennadiy on 23.06.2014.
 */
public class AtmManager {

    public static final String TAG = "AtmManager";

    private static final String CATEGORIES_REQUEST_URL = "http://notacash.com/api/categories";
    private static final String SUBCATEGORIES_REQUEST_URL = "http://notacash.com/api/category/";

    private static final String CATEGORY_ID = "Id";
    private static final String SUBCATEGORY_ID = "Id";
    private static final String SUBCATEGORY_NAME = "Name";
    private static final String CATEGORY_NAME = "Name";
    private static final String SUBCATEGORIES_ARRAY = "Subcategories";

    private Context mContext;
    private ProgressDialog dialog;

    private Location currentLocation;
    private SparseArray<String> categories;
    private Map<Integer, SparseArray<String>> subCategoriesMap;                //key - category number



    public AtmManager(Context context) {
        mContext = context;
    }

    public List<String> getSubcategories(int category) {
        return null;
    }



    public List<AtmObject> getATMList(int categoty, int subcategory,
                                      int proximity) {

        FindLocation loc = new FindLocation();
        FindLocation.FixedLocation fixedLocation = new FindLocation.FixedLocation() {
            @Override
            public void onFixedLocation(Location location) {
                Log.d(TAG, "Location: " + location);
                currentLocation = location;



            }
        };

        if (!loc.fixLocation(mContext, fixedLocation)) {
            Toast.makeText(mContext, R.string.location_provider_error, Toast.LENGTH_SHORT).show();
            return null;
        }

        return null;
    }

    //check data connection availability
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public Location getCurrentLocation() {
        return  currentLocation;
    }

    private void updateCategories () {

        JSONArray categoryArray = JSONParser.getJSONArrayFromUrl(CATEGORIES_REQUEST_URL);
        categories = new SparseArray<String>(categoryArray.length());
        int categoryId;
        String categoryName;

        for (int i = 0; i < categoryArray.length(); i++) {
            try {
                JSONObject object = categoryArray.getJSONObject(i);
                categoryId = object.getInt(CATEGORY_ID);
                categoryName = object.getString(CATEGORY_NAME);
                categories.append(categoryId, categoryName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




    }

    private void updateSubCategories() {
        subCategoriesMap = new HashMap<Integer, SparseArray<String>>();
        for (int i = 0; i < categories.size(); i++) {
            try {

                int subcategoryId;
                String subcategoryName;
                String request = SUBCATEGORIES_REQUEST_URL + categories.keyAt(0);
                JSONObject subcategoryObject = JSONParser.getJSONObjectFromUrl(request);
                JSONArray subcategoriesArray = subcategoryObject.getJSONArray(SUBCATEGORIES_ARRAY);
                SparseArray<String> subcategories = new SparseArray<String>();
                for (int j = 1; j < subcategoriesArray.length(); j++) {
                    subcategoryObject = subcategoriesArray.getJSONObject(j);
                    subcategoryId = subcategoryObject.getInt(SUBCATEGORY_ID);
                    subcategoryName = subcategoryObject.getString(SUBCATEGORY_NAME);
                    subcategories.append(subcategoryId, subcategoryName);

                }
                subCategoriesMap.put(i, subcategories);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public void updateData() {

        if (!isConnected()) {
            Toast.makeText(mContext, "Check you internet connection", Toast.LENGTH_SHORT).show();
            return;

        }
        updateCategories();
        updateSubCategories();

    }




}

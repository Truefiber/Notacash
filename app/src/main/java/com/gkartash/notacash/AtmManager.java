package com.gkartash.notacash;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private static final String ATM_REQUEST_FORMAT = "http://notacash" +
            ".com/api/search/%d/%d/%f/%f/%d";

    private static final String CATEGORY_ID = "Id";
    private static final String SUBCATEGORY_ID = "Id";
    private static final String SUBCATEGORY_NAME = "Name";
    private static final String CATEGORY_NAME = "Name";
    private static final String SUBCATEGORIES_ARRAY = "Subcategories";
    private static final String ATM_LAT = "lat";
    private static final String ATM_LNG = "lng";
    private static final String ATM_ADDRESS = "address";
    private static final String ATM_CITY = "city";
    private static final String ATM_BANK = "bank";
    private static final String ATM_WORKTIME = "worktime";
    private static final String ATM_LOCATION_TYPE = "locationType";




    private Context mContext;


    private Location currentLocation;
    private Map<Integer, String> categoriesMap;
    private Map<Integer, Map<Integer, String>> subCategoriesMap;

    /*
    There is no guarantee that category or subcategory IDs will be sequential,
    but we need list representation for Spinner Adapters and API requests
     */
    private List<String> categoryNameList;
    private List<Integer> categoryIdList;
    private List<String> subcategoryNameList;
    private List<Integer> subcategoryIdList;



    public AtmManager(Context context) {
        mContext = context;
        getLocation();
    }





    private void getLocation() {

        FindLocation loc = new FindLocation();
        FindLocation.FixedLocation foundLocation = new FindLocation.FixedLocation() {
            @Override
            public void onFixedLocation(Location location) {
                Log.d(TAG, "Location: " + location);
                currentLocation = location;



            }
        };

        if (!loc.fixLocation(mContext, foundLocation)) {
            Toast.makeText(mContext, R.string.location_provider_error, Toast.LENGTH_SHORT).show();

        }


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
        categoriesMap = new HashMap<Integer, String>();
        int categoryId;
        String categoryName;

        for (int i = 0; i < categoryArray.length(); i++) {
            try {
                JSONObject object = categoryArray.getJSONObject(i);
                categoryId = object.getInt(CATEGORY_ID);
                categoryName = object.getString(CATEGORY_NAME);
                categoriesMap.put(categoryId, categoryName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




    }

    private void updateSubCategories() {
        subCategoriesMap = new HashMap<Integer, Map<Integer, String>>();
        for (int categoryID: categoriesMap.keySet()) {
            try {

                int subcategoryId;
                String subcategoryName;
                String request = SUBCATEGORIES_REQUEST_URL + categoryID;
                JSONObject subcategoryObject = JSONParser.getJSONObjectFromUrl(request);
                JSONArray subcategoriesArray = subcategoryObject.getJSONArray(SUBCATEGORIES_ARRAY);
                Map<Integer, String> subcategories = new HashMap<Integer, String>();
                for (int j = 0; j < subcategoriesArray.length(); j++) {
                    subcategoryObject = subcategoriesArray.getJSONObject(j);
                    subcategoryId = subcategoryObject.getInt(SUBCATEGORY_ID);
                    subcategoryName = subcategoryObject.getString(SUBCATEGORY_NAME);
                    subcategories.put(subcategoryId, subcategoryName);

                }
                subCategoriesMap.put(categoryID, subcategories);

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

    public List<String> getCategoryList() {
        categoryIdList = new ArrayList<Integer>(categoriesMap.keySet());
        categoryNameList = new ArrayList<String>(categoryIdList.size());
        for (int categoryId:categoryIdList) {
            categoryNameList.add(categoriesMap.get(categoryId));
        }

        return categoryNameList;

    }

    public List<String> getSubcategoryList (int categoryId) {
        Map<Integer, String> choosenCategoryMap = subCategoriesMap.get(categoryId);
        subcategoryIdList = new ArrayList<Integer>(choosenCategoryMap.keySet());
        subcategoryNameList = new ArrayList<String>(subcategoryIdList.size());
        for (int subcategoryId:subcategoryIdList) {
            subcategoryNameList.add(choosenCategoryMap.get(subcategoryId));
        }

        return subcategoryNameList;
    }

    public int getCategoryId(int spinnerPosition) {

        return categoryIdList.get(spinnerPosition);
    }

    public int getSubcategoryId(int spinnerPosition) {
        return subcategoryIdList.get(spinnerPosition);

    }

    public String makeDownloadURL(int category, int subcategory, int proximity) {
        return String.format(ATM_REQUEST_FORMAT, category, subcategory,
                currentLocation.getLatitude(), currentLocation.getLongitude(), proximity);
    }

    public static List<AtmObject> getAtmListFromURL(String url) {
        JSONArray atmArray = JSONParser.getJSONArrayFromUrl(url);
        List<AtmObject> list = new ArrayList<AtmObject>();
        for (int i = 0; i < atmArray.length(); i++) {

            try {
                AtmObject atm = new AtmObject();
                JSONObject atmObject = atmArray.getJSONObject(i);
                double latitude = atmObject.getDouble(ATM_LAT);
                double longitude = atmObject.getDouble(ATM_LNG);
                atm.setLocation(new LatLng(latitude, longitude));
                atm.setAddress(atmObject.getString(ATM_ADDRESS));
                atm.setCity(atmObject.getString(ATM_CITY));
                atm.setBank(atmObject.getString(ATM_BANK));
                atm.setWorkTime(atmObject.getString(ATM_WORKTIME));
                atm.setLocationType(atmObject.getString(ATM_LOCATION_TYPE));
                list.add(atm);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return list;


    }




}

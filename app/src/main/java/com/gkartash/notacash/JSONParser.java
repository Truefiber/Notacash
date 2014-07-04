package com.gkartash.notacash;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Gennadiy on 22.06.2014.
 */
public class JSONParser {


    public static JSONArray getJSONArrayFromUrl(String url) {

        JSONArray array = null;
        try {


            array = new JSONArray(getContent(url));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;

    }

    public static JSONObject getJSONObjectFromUrl (String url) {

        JSONObject object = null;

        try {
            object =  new JSONObject(getContent(url));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;

    }

    private static String getContent(String url) {
        InputStream in = null;
        try {
            URL notacashRequest = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) notacashRequest.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Scanner(in).useDelimiter("\\A").next();
    }
}

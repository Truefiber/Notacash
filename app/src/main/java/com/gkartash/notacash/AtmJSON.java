package com.gkartash.notacash;


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
public class AtmJSON {


    public JSONObject getJSON(String url) {
        try {
            URL notacashRequest = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) notacashRequest.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            return new JSONObject(getResponse(in));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static String getResponse(InputStream inStream) {

        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}

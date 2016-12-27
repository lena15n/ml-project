package com.lena.mlapplication.net;


import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetTask extends AsyncTask<String, Void, ArrayList<Double>> {
    private MyAsyncResponse delegate = null;

    public GetTask(MyAsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Double> doInBackground(String... params) {
        String urlString = params[0];
        String login = params[1];
        String password = params[2];

        URL url;
        HttpURLConnection httpConnection = null;
        String basicAuthData = login + ":" + password;
        String basicAuth = "Basic " + Base64.encodeToString(basicAuthData.getBytes(), Base64.NO_WRAP);

        try {
            url = new URL(urlString);

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", basicAuth);
            httpConnection.setUseCaches(false);

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                BufferedReader reader = null;
                StringBuilder response = new StringBuilder();

                try {
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return getDataFromJSON(response.toString());
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(ArrayList<Double> data) {
       delegate.processFinish(data);
    }

    public interface MyAsyncResponse {
        void processFinish(ArrayList<Double> data);
    }

    private ArrayList<Double> getDataFromJSON(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<Double>>(){}.getType();

        return gson.fromJson(json, collectionType);
    }
}

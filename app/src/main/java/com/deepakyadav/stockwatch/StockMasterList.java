package com.deepakyadav.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class StockMasterList extends AsyncTask<Void,Void,String> {

    private static final String TAG = "StockMasterList";
    private MainActivity mainActivity;
    private static final String DOWNLOAD_LINK = "https://api.iextrading.com/1.0/ref-data/symbols";

    public StockMasterList(MainActivity mainActivity) {
        this.mainActivity =mainActivity;
    }

    private HashMap<String, String> jsonMapper(String input) {
        HashMap<String,String> stringHashMap = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(input);
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("name");
                stringHashMap.put(symbol,name);
            }
            return stringHashMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Uri uri = Uri.parse(DOWNLOAD_LINK);
        String url_string = uri.toString();
        StringBuilder string_Builder = new StringBuilder();

        try {
            URL url = new URL(url_string);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line=bufferReader.readLine())!=null) {
                string_Builder.append(line).append("\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string_Builder.toString();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        HashMap<String,String> hashMap = jsonMapper(s);
        mainActivity.stockData(hashMap);
    }

}

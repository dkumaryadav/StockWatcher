package com.deepakyadav.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StockFinancialData extends AsyncTask<String,Void,String> {
    private static final String TAG = "StockFinancialData";
    private MainActivity mainActivity;
    private static final String LINK_HALF_1 = "https://cloud.iexapis.com/stable/stock/";
    private static final String LINK_HALF_2 ="/quote?token=pk_0877c31b4cb3475c88fff1649367592a";

    // Constructor
    public StockFinancialData(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stock = strings[0];
        String API_URL = LINK_HALF_1 + stock + LINK_HALF_2;
        Uri uri = Uri.parse(API_URL);
        String url_string = uri.toString();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(url_string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line).append("\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // FUNCTION: jsonToStock returns stock object
    private Stocks jsonToStock(String input) {
        Stocks stock = new Stocks();
        try {
            JSONObject object = new JSONObject(input);
            String symbol = object.getString("symbol");
            String name = object.getString("companyName");
            double price = object.getDouble("latestPrice");
            double priceChange = object.getDouble("change");
            double changePercentage = object.getDouble("changePercent");
            Log.d(TAG, "jsonToStock: changePercentage" + changePercentage);
            stock.setStockName(name);
            stock.setStockSymbol(symbol);
            stock.setStockPrice(price);
            stock.setStockPriceChange(priceChange);
            stock.setStockChangePercentage(changePercentage);
            Log.d(TAG, "jsonToStock: changePercentage from stock" + stock.getStockChangePercentage());

            return stock;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Stocks stock = jsonToStock(s);
        mainActivity.stockFinancial(stock);
    }
}

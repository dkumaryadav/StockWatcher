package com.deepakyadav.stockwatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private  static final String MARKET_WATCH = "http://www.marketwatch.com/investing/stock/";
    List<Stocks> stockInformation = new ArrayList<>();
    HashMap<String, String> hashMap;
    RecyclerView recyclerView;
    StocksAdapter adapter;
    SwipeRefreshLayout srLayout;
    SQLHandler db;

    // Reload swipe refresh layout
    private void reload() {
        Log.d(TAG, "reload: STARTED");
        srLayout.setRefreshing(false);
        ArrayList<Stocks> infoArrayList = db.loadStocksFromDB();
        for (int stock = 0; stock < infoArrayList.size(); stock++) {
            String symbol = infoArrayList.get(stock).getStockSymbol();
            new StockFinancialData(MainActivity.this).execute(symbol);
        }
        Log.d(TAG, "reload: COMPLETED");
    }

    // Check if connectivity is there
    private boolean checkConnectivity()  {
        Log.d(TAG, "checkConnectivity: STARTED");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if( networkInfo != null && networkInfo.isConnected() ){
            Log.d(TAG, "checkConnectivity: COMPLETED");
            return true;
        } else {
            Log.d(TAG, "checkConnectivity: COMPLETED");
            return false;
        }
    }

    // Alert dialog box saying no connectivity
    public void noConnectivity(String stage) {
        Log.d(TAG, "noConnectivity: STARTED");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        if( stage.equals("Add Stock"))
            builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
        else if (stage.equals("Refresh"))
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
        else
            builder.setMessage("Stocks App needs internet to get latest prices \n Loading saved stocks with price as 0$");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d(TAG, "noConnectivity: COMPLETED");
    }

    // onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        srLayout = findViewById(R.id.srLayout);

        adapter = new StocksAdapter(stockInformation, this);
        Log.d(TAG, "onCreate: " + stockInformation);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        srLayout.setProgressViewOffset(true, 0, 200);
        srLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright));

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!checkConnectivity()) {
                    srLayout.setRefreshing(false);
                    noConnectivity("Refresh");
                } else
                    reload();
            }
        });

        db = new SQLHandler(this);
        new StockMasterList(MainActivity.this).execute();
        ArrayList<Stocks> infoArrayList = db.loadStocksFromDB();
        if (!checkConnectivity()) {
            noConnectivity("App Start");
            stockInformation.addAll(infoArrayList);
            Collections.sort(stockInformation, new Comparator<Stocks>() {
                @Override
                public int compare(Stocks o1, Stocks o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < infoArrayList.size(); i++) {
                String symbol = infoArrayList.get(i).getStockSymbol();
                new StockFinancialData(MainActivity.this).execute(symbol);
            }
        }

        Log.d(TAG, "onCreate: end");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: start");
        getMenuInflater().inflate(R.menu.add, menu);
        Log.d(TAG, "onCreateOptionsMenu: end");
        return true;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: STARTED");
        super.onResume();
        Log.d(TAG, "onResume: " + stockInformation.size());
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onResume: COMPLETED");
    }

    // FUNCTION: searchStock returns list of stock matching the user search string
    private ArrayList<String> searchStock(String s) {
        Log.d(TAG, "searchStock: STARTED");
        ArrayList<String> sList = new ArrayList<>();
        if (hashMap != null && !hashMap.isEmpty()){
            for (String symbol : hashMap.keySet()) {
                String name = hashMap.get(symbol);
                if (symbol.toUpperCase().contains(s.toUpperCase()))
                    sList.add(symbol + " - " + name);
                else if (name.toUpperCase().contains(s.toUpperCase()))
                    sList.add(symbol + " - " + name);
            }
        }
        Log.d(TAG, "searchStock: COMPLETED");
        return sList;
    }

    // FUNCTION: checkDuplicate checks for duplicates
    private boolean checkDuplicate(String s) {
        Log.d(TAG, "checkDuplicate: STARTED");
        String sym = s.split("-")[0].trim();
        Stocks stock = new Stocks();
        stock.setStockSymbol(sym);
        Log.d(TAG, "checkDuplicate: COMPLETED");
        if( stockInformation.contains(stock) )
            return true;
        else
            return false;
    }

    // FUNCTION: duplicateStockDialog to notify stock is already present in the stock list
    private void duplicateStockDialog(String s) {
        Log.d(TAG, "duplicateStockDialog: STARTED");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Stock!");
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        builder.setMessage("Stock Symbol "+s+" is already displayed");
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "duplicateStockDialog: STARTED");
    }

    // FUNCTION: addNewStock
    private void addNewStock(String s) {
        Log.d(TAG, "addNewStock: STARTED");
        String symbol= s.split("-")[0].trim();
        new StockFinancialData(MainActivity.this).execute(symbol);
        Stocks stock = new Stocks();
        stock.setStockSymbol(symbol);
        stock.setStockName(hashMap.get(symbol));
        db.addStockToDB(stock);
        Log.d(TAG, "addNewStock: COMPLETED");
    }

    // FUNCTION: multipleStocksFound when more than one stock option had been found
    private void multipleStocksFound(final String s, ArrayList<String> stockOptions, int size) {
        Log.d(TAG, "multipleStocksFound: STARTED");
        final String[] strings = new String[size];
        for (int i = 0; i < strings.length; i++)
            strings[i] = stockOptions.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Selection");
        builder.setIcon(R.drawable.ic_note_black_24dp);
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkDuplicate(strings[which]))
                    duplicateStockDialog(s);
                else
                    addNewStock(strings[which]);
            }
        });

        // Don't do anything if "Never mind" is clicked
        builder.setNegativeButton("Never mind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "multipleStocksFound: COMPLETED");
    }

    // FUNCTION: stockNotFound
    private void stockNotFound(String symbol) {
        Log.d(TAG, "stockNotFound: STARTED");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: "+ symbol);
        builder.setMessage("Data for stock symbol");
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "stockNotFound: COMPLETED");
    }

    // FUNCTION: addNewStockDialog
    private void addNewStockDialog() {
        Log.d(TAG, "addNewStockDialog: STARTED");
        if (hashMap == null) {
            new StockMasterList(MainActivity.this).execute();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a stock symbol(in CAPS)");
        builder.setCancelable(false);

        final EditText stockSymbolET = new EditText(this);
        // Making the edit text only enter CAPs
        stockSymbolET.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        stockSymbolET.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(stockSymbolET);

        // POSITIVE Button : Ok
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Checking for internet connection
                if (!checkConnectivity())
                    noConnectivity("Add Stock");
                else if (stockSymbolET.getText().toString().trim().isEmpty()) // In input field was empty
                    Toast.makeText(MainActivity.this, "Input field was empty.\nTry again enter stock symbol like TSLA..", Toast.LENGTH_LONG).show();
                else {
                    // Search for a possible stock with the given name
                    ArrayList<String> stockResults = searchStock( stockSymbolET.getText().toString().trim() );
                    if (!stockResults.isEmpty()) {
                        ArrayList<String> stockOptions = new ArrayList<>(stockResults);

                        if (stockOptions.size() == 1) { // If only one stock option was found
                            if (checkDuplicate(stockOptions.get(0))) // Check if stock is already part of the list
                                duplicateStockDialog(stockSymbolET.getText().toString());
                            else // New stock found will be added to the stock list
                                addNewStock(stockOptions.get(0));
                        }
                        else // If more than stock was found
                            multipleStocksFound(stockSymbolET.getText().toString(), stockOptions, stockOptions.size());
                    } else // If no stock options were found
                        stockNotFound(stockSymbolET.getText().toString());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // NEGATIVE Button
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Don't do anything
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        Log.d(TAG, "addNewStockDialog: COMPLETED");
    }

    // FUNCTION: onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: STARTED");
            switch (item.getItemId()) {
                case R.id.add_stock:    if (!checkConnectivity())
                                            noConnectivity("Add Stock");
                                        else
                                            addNewStockDialog();
                                        return true;
                default: return super.onOptionsItemSelected(item);
            }
    }

    // FUNCTION: stockData populates hash map with stock data
    public void stockData(HashMap<String, String> hashMap) {
        if (hashMap != null && !hashMap.isEmpty())
            this.hashMap = hashMap;
    }

    // FUNCTION: stockFinancial populates stock's $ data
    public void stockFinancial(Stocks stock) {
        Log.d(TAG, "stockFinancial: STARTED");
        if (stock != null) {
            int index = stockInformation.indexOf(stock);
            if (index > -1)
                stockInformation.remove(index);
            stockInformation.add(stock);
            Collections.sort(stockInformation, new Comparator<Stocks>() {
                @Override
                public int compare(Stocks o1, Stocks o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            adapter.notifyDataSetChanged();
        }
        Log.d(TAG, "stockFinancial: COMPLETED");
    }

    // FUNCTION: onClick
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: STARTED");
        int position = recyclerView.getChildLayoutPosition(v);
        String marketWatchURL = MARKET_WATCH + stockInformation.get(position).getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(marketWatchURL));
        startActivity(intent);
        Log.d(TAG, "onClick: COMPLETED");
    }

    // FUNCTION: onLongClick
    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick: STARTED");
        TextView stockSymbol = v.findViewById(R.id.stockSymbol);
        String stockSymbolText = stockSymbol.getText().toString().trim();
        final int position = recyclerView.getChildLayoutPosition(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setIcon(R.drawable.ic_delete_black_24dp);
        builder.setMessage("Delete Stock Symbol " + stockSymbolText + "?");

        // If stock has to be deleted
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteStockFromDB(stockInformation.get(position).getStockSymbol());
                stockInformation.remove(position);
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Log.d(TAG, "onLongClick: COMPLETED");
        return false;
    }

    // FUNCTION: onDestroy
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: STARTED");
        super.onDestroy();
        db.shutDown();
        Log.d(TAG, "onDestroy: COMPLETED");
    }

}

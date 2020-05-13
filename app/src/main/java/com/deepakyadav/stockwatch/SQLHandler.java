package com.deepakyadav.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class SQLHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String CNAME = "CompanyName";
    private static final String CREATE_TABLE = "CREATE TABLE " +
                                                TABLE_NAME + " (" +
                                                SYMBOL + " TEXT not null unique," +
                                                CNAME + " TEXT not null )";
    private static final String TAG = "SQLHandler";
    private SQLiteDatabase database;

    // Constructor
    public SQLHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "onCreate: Table Created" + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Stocks> loadStocksFromDB() {
        ArrayList<Stocks> stock = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, CNAME},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Stocks stk = new Stocks();
                String stockSymbol = cursor.getString(0);
                String stockName = cursor.getString(1);
                stk.setStockSymbol(stockSymbol);
                stk.setStockName(stockName);
                stk.setStockPrice(0.0);
                stk.setStockPriceChange(0.0);
                stk.setStockChangePercentage(0.0);
                stock.add(stk);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stock;
    }

    public void addStockToDB(Stocks stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getStockSymbol());
        values.put(CNAME, stock.getStockName());
        long key = database.insert(TABLE_NAME, null, values);
    }

    public void deleteStockFromDB(String symbol) {
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{symbol});
    }

    public void shutDown() {
        database.close();
    }
}

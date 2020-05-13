package com.deepakyadav.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class StocksAdapter extends RecyclerView.Adapter<ViewHolder>{

    private List<Stocks> list;
    private MainActivity mainActivity;

    // Constructor
    public StocksAdapter(List<Stocks> stockList, MainActivity ma) {
        this.list = stockList;
        this.mainActivity = ma;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view,viewGroup,false);
        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Stocks stock = list.get(i);
        // Updating stock prices with RED if price change is less than 0
        if (stock.getStockPriceChange() < 0)
            updateStocks(viewHolder, stock, "RED");
        else
            updateStocks(viewHolder, stock, "GREEN");
    }

    // FUNCTION: updateStocks will update the stocks in RED / GREEN Color
    private void updateStocks(ViewHolder holder, Stocks stock, String color) {
        if( color.equals("RED")){
            holder.stockName.setTextColor(Color.RED);
            holder.stockSymbol.setTextColor(Color.RED);
            holder.stockPrice.setTextColor(Color.RED);
            holder.stockChange.setTextColor(Color.RED);
            holder.stockChangePercentage.setTextColor(Color.RED);
            holder.stockArrow.setImageResource(R.drawable.arrow_down); // Using down arrow as stock is < 0
            holder.stockArrow.setColorFilter(Color.RED);
        } else{
            holder.stockName.setTextColor(Color.GREEN);
            holder.stockSymbol.setTextColor(Color.GREEN);
            holder.stockPrice.setTextColor(Color.GREEN);
            holder.stockChange.setTextColor(Color.GREEN);
            holder.stockChangePercentage.setTextColor(Color.GREEN);
            holder.stockArrow.setImageResource(R.drawable.arrow_up); // Using down arrow as stock is > 0
            holder.stockArrow.setColorFilter(Color.GREEN);
        }
        holder.stockName.setText(stock.getStockName());
        holder.stockSymbol.setText(stock.getStockSymbol());
        holder.stockPrice.setText(String.format(Locale.US, "%.2f", stock.getStockPrice()));
        holder.stockChange.setText(String.format(Locale.US, "%.2f", stock.getStockPriceChange()));
        holder.stockChangePercentage.setText(String.format(Locale.US, "(%.2f%%)", stock.getStockChangePercentage()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

package com.deepakyadav.stockwatch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView stockName;
    public TextView stockSymbol;
    public TextView stockPrice;
    public TextView stockChange;
    public TextView stockChangePercentage;
    public ImageView stockArrow;

    public ViewHolder(@NonNull View view) {
        super(view);
        stockName = itemView.findViewById(R.id.stockName);
        stockSymbol = itemView.findViewById(R.id.stockSymbol);
        stockPrice = itemView.findViewById(R.id.stockPrice);
        stockChange = itemView.findViewById(R.id.stockChange);
        stockChangePercentage = itemView.findViewById(R.id.stockChangePercent);
        stockArrow = itemView.findViewById(R.id.stockArrow);
    }
}

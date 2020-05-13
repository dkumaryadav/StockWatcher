package com.deepakyadav.stockwatch;

import java.io.Serializable;

public class Stocks implements Serializable {

    private String stockName;
    private String stockSymbol;
    private double stockPrice;
    private double stockChange;
    private double stockChangePercentage;

    // Getters
    public String getStockName() { return stockName; }

    public String getStockSymbol() {  return stockSymbol; }

    public double getStockPrice()  { return stockPrice; }

    public double getStockPriceChange()
    {
        return stockChange;
    }

    public double getStockChangePercentage()
    {
        return stockChangePercentage;
    }

    // Setters
    public void setStockName(String stockName) { this.stockName = stockName; }

    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public void setStockPrice(double stockPrice)
    {
        this.stockPrice = stockPrice;
    }

    public void setStockPriceChange(double stockChange)
    {
        this.stockChange = stockChange;
    }

    public void setStockChangePercentage(double stockChangePercentage) {
        this.stockChangePercentage = stockChangePercentage;
    }

    @Override
    public boolean equals(Object obj) {
        boolean output = false;
        if (obj == null || obj.getClass() != getClass()) {
            output = false;
        }else {
            Stocks stock = (Stocks) obj;
            if (this.stockSymbol.equals(stock.stockSymbol))
                output = true;
        }
        return output;
    }

}

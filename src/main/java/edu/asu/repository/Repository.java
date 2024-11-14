package edu.asu.repository;


import edu.asu.entity.Stock;
import edu.asu.entity.StockOrder;
import edu.asu.entity.Stocks;
import edu.asu.entity.Trader;

public interface Repository {
    Trader getTraders(String email, String password);

    void postTrader(Trader trader);

    void handleOrder(StockOrder stockOrder);

    Stocks getStocks(String email);

    int getWallet(String email);

    void addWallet(String email, Long add, boolean withdrawl);

    void sellStock(Stock stock, String email);
}

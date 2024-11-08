package edu.asu.repository;


import edu.asu.entity.StockOrder;
import edu.asu.entity.Stocks;
import edu.asu.entity.Trader;

public interface Repository {
    Trader getTraders(String email, String password);

    void postTrader(Trader trader);

    void handleOrder(StockOrder stockOrder);

    Stocks getStocks(String email);

}

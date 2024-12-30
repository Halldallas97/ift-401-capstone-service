package edu.asu.repository;


import edu.asu.entity.*;

public interface Repository {
    Trader getTraders(String email, String password);

    void postTrader(Trader trader);

    void handleOrder(StockOrder stockOrder);

    Stocks getStocks(String email);

    int getWallet(String email);

    void addWallet(String email, Long add, boolean withdrawl);

    void sellStock(Stock stock, String email, double sellPrice);

    Transactions getTraderTransactions(String email);
}

package edu.asu.repository;

import edu.asu.entity.Portfolio;
import edu.asu.entity.Trader;

public interface Repository {
    Trader getTraders(String email, String password);

    void postTrader(Trader trader);

//    Portfolio getPortfolio(String email);
}

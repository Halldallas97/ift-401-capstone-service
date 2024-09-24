package edu.asu.repository;

import edu.asu.entity.Trader;

import java.util.List;

public interface Repository {
    List<Trader> getTraders();

    void postTrader(Trader trader);
}

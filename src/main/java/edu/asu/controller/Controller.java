package edu.asu.controller;

import edu.asu.entity.StockOrder;
import edu.asu.entity.Stocks;
import edu.asu.entity.Trader;
import edu.asu.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@org.springframework.web.bind.annotation.RestController
@Slf4j
@RequestMapping("api/server")
public class Controller {
    private final Repository repository;
    public Controller(Repository repository) {
        this.repository = repository;
    }
    @PostMapping("/login")
    public Trader getTraders(@RequestParam("email") String email, @RequestParam("password") String password) {
        return repository.getTraders(email, password);
    }

    @PostMapping("/buy")
    public void postBuyOrder(@RequestBody StockOrder stockOrder) {
        repository.handleOrder(stockOrder);
    }

    @GetMapping("/stock")
    public Stocks getPortfolio(@RequestParam("email") String email) {
        return repository.getStocks(email);
    }
    @GetMapping("/wallet")
    public int getWallet(@RequestParam("email") String email) {
        return repository.getWallet(email);
    }
    @PutMapping("/wallet")
    public void updateWallet(@RequestParam("email") String email,@RequestParam("add") Long add, @RequestParam("withdrawl") boolean withdrawl){
        repository.addWallet(email, add, withdrawl);
    }

    @PostMapping("/trader")
    public void postTrader(@RequestBody Trader trader) {
        //todo hash password
        log.info("going to create a new trader! {}", trader);
        repository.postTrader(trader);
    }
}

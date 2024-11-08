package edu.asu.controller;

import edu.asu.entity.StockOrder;
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
    //todo implement getting users portfolio data
//    @GetMapping("/portfolio")
//    public Portfolio getPortfolio(@RequestParam("email") String email) {
//        Portfolio portfolio = repository.getPortfolio(email);
//        return portfolio.getTrader().getPortfolio();
//    }

    //todo implement pushing data to traders portfolio

    @PostMapping("/trader")
    public void postTrader(@RequestBody Trader trader){
        //todo hash password
        log.info("going to create a new trader! {}", trader);
        repository.postTrader(trader);
    }
}

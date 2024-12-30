package edu.asu.controller;

import edu.asu.entity.Stock;
import edu.asu.entity.StockOrder;
import edu.asu.entity.Stocks;
import edu.asu.entity.Transactions;
import edu.asu.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@org.springframework.web.bind.annotation.RestController
@Slf4j
@RequestMapping("api/server/stock")
public class StockController {
    private final Repository repository;
    public StockController(Repository repository) {
        this.repository = repository;
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
    public void updateWallet(@RequestParam("email") String email,
                             @RequestParam("add") Long add,
                             @RequestParam("withdrawal") boolean withdrawal){
        repository.addWallet(email, add, withdrawal);
    }
    @PutMapping("/sell")
    public void sellStock(@RequestBody Stock stock,
                          @RequestParam("email") String email,
                          @RequestParam("currentPrice") double sellPrice ){
        repository.sellStock(stock, email, sellPrice);
    }
    @GetMapping("/transactions")
    public Transactions getTransactions(@RequestParam("email") String email){
        return repository.getTraderTransactions(email);
    }

}

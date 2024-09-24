package edu.asu.controller;

import edu.asu.entity.Trader;
import edu.asu.entity.Traders;
import edu.asu.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@Slf4j
@RequestMapping("api/server")
public class Controller {
    private final Repository repository;
    public Controller(Repository repository) {
        this.repository = repository;
    }
    @GetMapping("/traders")
    public Traders getTraders() {
        List<Trader> traderList = repository.getTraders();
        return Traders.builder().traders(traderList).build();
    }
    @PostMapping("/trader")
    public void postTrader(@RequestBody Trader trader){
        log.info("going to create a new trader! {}", trader);
        repository.postTrader(trader);

    }
}

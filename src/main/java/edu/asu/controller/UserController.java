package edu.asu.controller;

import edu.asu.entity.Trader;
import edu.asu.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@org.springframework.web.bind.annotation.RestController
@Slf4j
@RequestMapping("api/server/user")
public class UserController {
    private final Repository repository;
    public UserController(Repository repository) {
        this.repository = repository;
    }
    @PostMapping("/login")
    public Trader getTraders(@RequestParam("email") String email, @RequestParam("password") String password) {
        return repository.getTraders(email, password);
    }

    @PostMapping("/trader")
    public void postTrader(@RequestBody Trader trader) {
        log.info("going to create a new trader! {}", trader);
        repository.postTrader(trader);
    }
}

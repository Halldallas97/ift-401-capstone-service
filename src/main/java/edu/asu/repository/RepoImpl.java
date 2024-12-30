package edu.asu.repository;

import edu.asu.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class RepoImpl implements Repository {

    @Value("${secret.encoder.password}")
    private String secret;
    @Value("${secret.encoder.salt}")
    private int salt;
    @Value("${secret.encoder.iterations}")
    private int iterations;

    private final JdbcTemplate jdbcTemplate;

    public RepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Stocks getStocks(String email) {
        Integer id = getUserId(email);
        if (id == null) {
            System.out.println("User ID not found for email: " + email);
            return null;
        }

        String sql = "SELECT * FROM stock WHERE portfolio_id = ?";
        Stocks stocks = new Stocks();
        List<Stock> stocksList;
        try {
            stocksList = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
                Stock stock = new Stock();
                stock.setUUID(rs.getString("uuid"));
                stock.setCompany(rs.getString("company_name"));
                stock.setCost(rs.getLong("cost"));
                stock.setQuantity(rs.getLong("quantity"));
                stock.setSym(rs.getString("sym"));
                return stock;
            });
            stocks.setStocks(stocksList);
            if (stocksList.isEmpty()) {
                System.out.println("No stocks found for portfolio ID: " + id);
            }
            return stocks;
        } catch (DataAccessException e) {
            log.error("Error retrieving stocks", e);
            return null;
        }
    }

    @Override
    public Transactions getTraderTransactions(String email) {
        Portfolio portfolio = getPortfolioByEmail(email);

        // Handle case where portfolio is not found
        if (portfolio == null) {
            throw new RuntimeException("Portfolio not found for email: " + email);
        }

        String sql = "SELECT * FROM transaction WHERE email = ? AND portfolio_id = ?";
        Transactions transactions = new Transactions();
        List<Transaction> transactionList;

        try {
            transactionList = jdbcTemplate.query(sql, new Object[]{email, portfolio.getId()}, (rs, rowNum) -> {
                Transaction t = new Transaction();
                t.setCompany(rs.getString("company_name"));
                t.setCost(rs.getLong("cost"));
                t.setEmail(rs.getString("email"));
                t.setEvaluation(rs.getDouble("evaluation"));
                t.setQuantity(rs.getLong("quantity"));
                t.setSellPrice(rs.getDouble("sell_price"));
                t.setSym(rs.getString("sym"));
                return t;
            });

            // Add the list of transactions to the Transactions object
            transactions.setTransactionList(transactionList);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching transactions for trader: " + email, e);
        }

        return transactions; // Return the Transactions object
    }


    @Override
    public int getWallet(String email) {
        String sql = "SELECT wallet FROM trader WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No wallet found for email: {}", email);
            return 0; // Default wallet balance
        } catch (DataAccessException e) {
            log.error("Database access error while fetching wallet for email: {}", email, e);
            throw e; // Rethrow if it's a database access issue
        } catch (Exception e) {
            log.error("Unexpected error while fetching wallet for email: {}", email, e);
            throw e; // Rethrow unexpected exceptions
        }
    }


    @Override
    public void addWallet(String email, Long add, boolean withdrawal) {
        int startingBalance = getWallet(email);
        long newBalance = withdrawal ? startingBalance - add : startingBalance + add;
        if (withdrawal && startingBalance < add) {
            log.error("Insufficient funds for withdrawal");
            return;
        }
        String sql = "UPDATE trader " +
                "SET wallet = ? " +
                "WHERE email = ?";
        try {
            jdbcTemplate.update(sql, newBalance, email);
        } catch (DataAccessException e) {
            log.error("Exception occurred while updating wallet: ", e);
        }
        log.info("Successfully found wallet and updated it");
    }

    @Override
    public void sellStock(Stock stock, String email, double sellPrice) {
        double eval = (stock.getQuantity() * sellPrice) - (stock.getQuantity() * stock.getCost()) ;

        Portfolio portfolio = getPortfolioByEmail(email);
        if (portfolio == null) {
            System.out.println("Portfolio ID not found for email: " + email);
            return;
        }
        Transaction t = Transaction.builder().sym(stock.getSym()).cost(stock.getCost()).quantity(stock.getQuantity())
                .company(stock.getCompany()).sellPrice(sellPrice).email(email).portfolio(portfolio).evaluation(eval).build();
        updateTransactions(t);

        long amount = stock.getCost() * stock.getQuantity();
        addWallet(email, amount, false);
        Integer id = getUserId(email);
        String sql = "DELETE FROM stock \n" +
                "WHERE portfolio_id = ? and \n" +
                "company_name = ? and \n" +
                "quantity = ?";
        try {
            jdbcTemplate.update(sql, id, stock.getCompany(), stock.getQuantity());
        } catch (DataAccessException e) {
            log.error("Exception occurred while updating wallet: ", e);
        }
        log.info("Successfully found wallet and updated it");

    }



    private void updateTransactions(Transaction t) {
        String sql = "INSERT INTO transaction (company_name, cost, email, evaluation, quantity, sell_price, sym, portfolio_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            jdbcTemplate.update(sql, t.getCompany(), t.getCost(), t.getEmail(),t.getEvaluation(),t.getQuantity(),t.getSellPrice(),t.getSym(),t.getPortfolio().getId());

        } catch (DataAccessException e) {
            log.error("unable to update transactions table for trader {} ", t.getEmail() );
        }

    }


    private Portfolio getPortfolioByEmail(String email) {
        String sql = "SELECT id, trader_email FROM portfolio WHERE trader_email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) ->
                    Portfolio.builder()
                            .id(rs.getLong("id"))
                            .trader(Trader.builder().email(rs.getString("trader_email")).build())
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Trader getTraders(String email, String rawPassword) {
        String sql = "SELECT * FROM trader WHERE email = ?";
        try {
            Trader trader = jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) -> {
                Trader t = new Trader();
                t.setEmail(rs.getString("email"));
                t.setFirstName(rs.getString("first_name"));
                t.setLastName(rs.getString("last_name"));
                t.setAdmin(rs.getBoolean("admin"));
                t.setUserName(rs.getString("user_name"));
                t.setWallet(rs.getInt("wallet"));
                t.setPassword(rs.getString("password")); // Retrieve the encoded password
                return t;
            });

            Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(secret, salt, iterations, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
            if (trader != null && encoder.matches(rawPassword, trader.getPassword())) {
                return trader;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            log.error("Error retrieving trader", e);
            return null;
        }
    }


    @Override
    public void postTrader(Trader trader) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(secret, salt, iterations, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        String encodedPassword = encoder.encode(trader.getPassword());

        String sql = "insert into trader (password, email, admin, user_name, first_name, last_name, wallet)\n" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, encodedPassword, trader.getEmail(), trader.isAdmin(), trader.getUserName(), trader.getFirstName(), trader.getLastName(), trader.getWallet());
            createPortfolioAccount(trader.getEmail());
        } catch (DataAccessException e) {
            log.error("unable to add trader {} with error:", trader.getFirstName(), e);
        }
    }

    @Override
    public void handleOrder(StockOrder stockOrder) {
        UUID uuid = UUID.randomUUID();
        Integer id = getUserId(stockOrder.getEmail());
        if (id == null) {
            System.out.println("Portfolio ID not found for email: " + stockOrder.getEmail());
            return;
        }
        String sql = "INSERT INTO stock (uuid, company_name, cost, quantity, sym, volume, portfolio_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, uuid.toString(), stockOrder.getName(), stockOrder.getCostPerStock(),
                stockOrder.getQuantity(), stockOrder.getSymbol(), stockOrder.getTotal(), id);

        updateWallet(stockOrder.getEmail(), stockOrder.getNewBalance());
        log.info("success");
    }

    private void updateWallet(String email, int wallet) {
        String sql = "update trader set wallet = ? where email = ? ";
        jdbcTemplate.update(sql, wallet, email);
    }

    private Integer getUserId(String email) {
        String sql = "SELECT id FROM portfolio WHERE trader_email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void createPortfolioAccount(String email) {
        String sql = "insert into portfolio (trader_email) values (?)";
        try {
            jdbcTemplate.update(sql, email);
        } catch (DataAccessException e) {
            log.error("unable to add portfolio with associated id {} with error:", email, e);
        }
    }
}

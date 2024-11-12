package edu.asu.repository;

import edu.asu.entity.Stock;
import edu.asu.entity.StockOrder;
import edu.asu.entity.Stocks;
import edu.asu.entity.Trader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class RepoImpl implements Repository {
    private final JdbcTemplate jdbcTemplate;

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
    public int getWallet(String email) {
        String sql = "SELECT wallet FROM trader WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



    @Override
    public Trader getTraders(String email, String password) {
        String sql = "SELECT * FROM trader WHERE email = ? AND password = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email, password}, (rs, rowNum) -> {
                Trader trader = new Trader();
                trader.setEmail(rs.getString("email"));
                trader.setFirstName(rs.getString("first_name"));
                trader.setLastName(rs.getString("last_name"));
                trader.setAdmin(rs.getBoolean("admin"));
                trader.setUserName(rs.getString("user_name"));
                trader.setWallet(rs.getInt("wallet"));
                return trader;
            });
        } catch (DataAccessException e) {
            log.error("Error retrieving trader", e);
            return null;
        }
    }

    @Override
    public void postTrader(Trader trader) {
        String sql = "insert into trader (password, email, admin, user_name, first_name, last_name, wallet)\n" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, trader.getPassword(), trader.getEmail(), trader.isAdmin(), trader.getUserName(), trader.getFirstName(), trader.getLastName(), 0);
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

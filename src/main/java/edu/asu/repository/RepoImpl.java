package edu.asu.repository;

import edu.asu.entity.StockOrder;
import edu.asu.entity.Trader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class RepoImpl implements Repository {
    private final JdbcTemplate jdbcTemplate;

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
            return null; // Or throw an exception based on your requirements
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

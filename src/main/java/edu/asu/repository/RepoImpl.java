package edu.asu.repository;

import edu.asu.entity.Trader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RepoImpl implements Repository {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Trader> getTraders() {
        List<Trader> traders = new ArrayList<>();
        String sql = "select * from trader";
        try {
            jdbcTemplate.query(sql, (rs, rowNum) -> {
                Trader t = Trader.builder()
                        .password(rs.getString("password"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .admin(rs.getBoolean("admin"))
                        .userName(rs.getString("user_name"))
                        .build();
                traders.add(t);
                return t;
            });
        } catch (DataAccessException e) {
            log.error("Error retrieving employees list", e);
        }
        return traders;
    }

    @Override
    public void postTrader(Trader trader) {
        String sql = "insert into trader (password, email, admin, user_name, first_name, last_name)\n" +
                "values (?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, trader.getPassword(), trader.getEmail(), trader.isAdmin(), trader.getUserName(), trader.getFirstName(), trader.getLastName());
        } catch (DataAccessException e){
            log.error("unable to add trader {} with error:", trader.getFirstName(), e);
        }

    }
}

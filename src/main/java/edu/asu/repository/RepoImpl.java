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
                        .phoneNumber(rs.getString("phone_number"))
                        .fname(rs.getString("first_name"))
                        .lname(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .build();
                traders.add(t);
                return t;
            });
        } catch (DataAccessException e) {
            log.error("Error retrieving employees list", e);
        }
        return traders;
    }
}

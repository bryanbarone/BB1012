package com.bryanbarone.cf.toolrental.repository;

import com.bryanbarone.cf.toolrental.model.RentalCost;
import com.bryanbarone.cf.toolrental.model.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class ToolJDBCRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    // queries would typically be configured or centralized, but placing here for simplicity and visibility
    private final static String fullquery = "SELECT code, brand, type, charge, holiday, weekday, weekend FROM TOOL join RENTAL_COST on type = tooltype_id";
    private final static String fullsort = " ORDER BY type, brand";

    @Transactional(readOnly=true)
    public List<Tool> findAllTools() {
        return jdbcTemplate.query(fullquery + fullsort,
                new ToolRowMapper());
    }

    @Transactional(readOnly=true)
    public Tool findToolByCode(String code) {
        return jdbcTemplate.queryForObject(fullquery + " where code=?",
                new Object[]{code}, new ToolRowMapper());
    }
}

/**
 * RowMapper to populate the Tool object.
 * Populates both tool and cost objects from the resultset.
 */
class ToolRowMapper implements RowMapper<Tool> {
    @Override
    public Tool mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tool tool = new Tool();
        // populate tool data
        tool.setCode(rs.getString("code"));
        tool.setBrand(rs.getString("brand"));
        tool.setType(rs.getString("type"));
        // populate rental costs and set on tool
        RentalCost costs = new RentalCost();
        costs.setCharge(rs.getBigDecimal("charge"));
        costs.setHoliday(rs.getBoolean("holiday"));
        costs.setWeekday(rs.getBoolean("weekday"));
        costs.setWeekend(rs.getBoolean("weekend"));
        tool.setCosts(costs);

        return tool;
    }
}
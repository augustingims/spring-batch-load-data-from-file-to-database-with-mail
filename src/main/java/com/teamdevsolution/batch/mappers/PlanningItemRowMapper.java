package com.teamdevsolution.batch.mappers;

import com.teamdevsolution.batch.domain.PlanningItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlanningItemRowMapper implements RowMapper<PlanningItem> {

    @Override
    public PlanningItem mapRow(ResultSet resultSet, int i) throws SQLException {
        PlanningItem item = new PlanningItem();
        item.setLibelleFormation(resultSet.getString(1));
        item.setDateDebutSeance(resultSet.getDate(2).toLocalDate());
        item.setDateFinSeance(resultSet.getDate(3).toLocalDate());
        return item;
    }
}


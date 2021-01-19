package com.teamdevsolution.batch.processors;

import com.teamdevsolution.batch.domain.Planning;
import com.teamdevsolution.batch.domain.PlanningItem;
import com.teamdevsolution.batch.mappers.PlanningItemRowMapper;
import com.teamdevsolution.batch.utils.QueryUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanningProcessor implements ItemProcessor<Planning, Planning> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlanningProcessor(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Planning process(Planning planning) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("formateur", planning.getFormateur().getId());
        List<PlanningItem> planningItems = jdbcTemplate.query(QueryUtils.SELECT_SEANCE_QUERY, params, new PlanningItemRowMapper());
        planning.setSeances(planningItems);
        return planning;
    }
}

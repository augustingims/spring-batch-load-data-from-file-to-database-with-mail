package com.teamdevsolution.batch.config;

import com.teamdevsolution.batch.domain.Planning;
import com.teamdevsolution.batch.mappers.PlanningRowMapper;
import com.teamdevsolution.batch.processors.PlanningProcessor;
import com.teamdevsolution.batch.services.MailContentGenerator;
import com.teamdevsolution.batch.services.PlanningMailSenderService;
import com.teamdevsolution.batch.services.impl.MailContentGeneratorImpl;
import com.teamdevsolution.batch.services.impl.PlanningMailSenderServiceImpl;
import com.teamdevsolution.batch.utils.QueryUtils;
import com.teamdevsolution.batch.writers.PlanningItemWriter;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateNotFoundException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class LoadPlaningConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    public LoadPlaningConfig(StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean(destroyMethod = "")
    public JdbcCursorItemReader<Planning> loadPlaningReader(){
        return new JdbcCursorItemReaderBuilder<Planning>()
                .name("loadPlaningReader")
                .dataSource(dataSource)
                .sql(QueryUtils.SELECT_FORMATEUR_QUERY)
                .rowMapper(new PlanningRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Planning, Planning> planningProcessor(final NamedParameterJdbcTemplate jdbcTemplate) {
        return new PlanningProcessor(jdbcTemplate);
    }

    @Bean
    public MailContentGenerator mailContentGenerator(final freemarker.template.Configuration conf)
            throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return new MailContentGeneratorImpl(conf);
    }

    @Bean
    public PlanningMailSenderService planningMailSenderService(final JavaMailSender javaMailSender) {
        return new PlanningMailSenderServiceImpl(javaMailSender);
    }

    @Bean
    public PlanningItemWriter planningWriter(final PlanningMailSenderService planningService,
                                             final MailContentGenerator mailContentGenerator) {
        return new PlanningItemWriter(planningService, mailContentGenerator);
    }

    @Bean
    public Step planningStep() {
        return stepBuilderFactory.get("planningStep")
                .<Planning, Planning>chunk(10)
                .reader(loadPlaningReader())
                .processor(planningProcessor(null))
                .writer(planningWriter(null, null))
                .build();
    }
}

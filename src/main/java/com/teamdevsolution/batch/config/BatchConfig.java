package com.teamdevsolution.batch.config;

import com.teamdevsolution.batch.deciders.SeanceDecider;
import com.teamdevsolution.batch.domain.Formateur;
import com.teamdevsolution.batch.validators.MyJobParametersValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    public BatchConfig(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public JobParametersValidator defaultJobParametersValidator(){
        DefaultJobParametersValidator bean = new DefaultJobParametersValidator();
        bean.setRequiredKeys(new String[]{"formateursFile","formationsFile","seancesFile"});
        bean.setOptionalKeys(new String[]{"run.id"});
        return bean;
    }

    @Bean
    public JobParametersValidator myJobParametersValidator(){
        return new MyJobParametersValidator();
    }

    @Bean
    public JobParametersValidator compositeJobParametersValidator(){
        CompositeJobParametersValidator composite = new CompositeJobParametersValidator();
        composite.setValidators(Arrays.asList(defaultJobParametersValidator(), myJobParametersValidator()));
        return composite;
    }

    @Bean
    public Flow loadFormateurFlow(final Step loadFormateurStep){
        return new FlowBuilder<Flow>("loadFormateurFlow")
                .start(loadFormateurStep)
                .end();
    }

    @Bean
    public Flow loadFormationFlow(final Step loadFormationStep){
        return new FlowBuilder<Flow>("loadFormationFlow")
                .start(loadFormationStep)
                .end();
    }

    @Bean
    public Flow parallelFlow(){
        return new FlowBuilder<Flow>("parallelFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(loadFormateurFlow(null), loadFormationFlow(null))
                .end();
    }

    @Bean
    public Job jobFormationBatch(final Step loadSeanceStepWithCsv, final Step loadSeanceStepWithTxt, final Step planningStep){
        return jobBuilderFactory.get("formations-batch")
                .start(parallelFlow())
                .next(seanceDecider()).on("txt").to(loadSeanceStepWithTxt)
                .from(seanceDecider()).on("csv").to(loadSeanceStepWithCsv)
                .from(loadSeanceStepWithTxt).on("*").to(planningStep)
                .from(loadSeanceStepWithCsv).on("*").to(planningStep)
                .end()
                .validator(compositeJobParametersValidator())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public JobExecutionDecider seanceDecider(){
        return new SeanceDecider();
    }
}

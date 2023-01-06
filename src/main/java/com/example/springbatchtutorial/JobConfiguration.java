package com.example.springbatchtutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfiguration {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    Job firstJob() {
        Tasklet tasklet = (contribution, chunkContext) -> {
            System.out.println("Hallo Welt");
            return RepeatStatus.FINISHED;
        };

        TaskletStep step = new StepBuilder("Hello_World_StepBuilder", jobRepository)
                .tasklet(tasklet, transactionManager).build();

        return new JobBuilder("Hello_World_JobBuilder", jobRepository)
                .start(step).build();
    }
}

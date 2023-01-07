package com.example.springbatchtutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
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

    @Autowired
    private SoutBuilder soutBuilder;

    @Bean
    Job firstJob() {
        return soutBuilder
                .setJobName("MeinJobName")
                .setStepName("MeinStepName")
                .setMessage("Hey, du da!")
                .getJob();

        //        Tasklet tasklet = (contribution, chunkContext) -> {
//            System.out.println("Hallo Welt");
//            return RepeatStatus.FINISHED;
//        };
//
//        TaskletStep step = new StepBuilder("Hello_World_StepBuilder", jobRepository)
//                .tasklet(tasklet, transactionManager).build();
//
//        return new JobBuilder("Hello_World_JobBuilder", jobRepository)
//                .start(step).build();
    }
}

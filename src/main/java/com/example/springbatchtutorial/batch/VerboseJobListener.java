package com.example.springbatchtutorial.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class VerboseJobListener {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("LET'S DO IT, "
                + jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.println("YOU'VE DONE IT, "
                + jobExecution.getJobInstance().getJobName());
    }
}

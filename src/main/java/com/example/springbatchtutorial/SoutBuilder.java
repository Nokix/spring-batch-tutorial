package com.example.springbatchtutorial;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@Setter
@Accessors(chain = true)
public class SoutBuilder {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("Hello World")
    private String message;
    @Value("soutStep")
    private String stepName;
    @Value("soutJob")
    private String jobName;
    private Boolean showThread = false;

    public Tasklet getTasklet() {
        String message = this.message;
        return (contribution, chunkContext) -> {
            System.out.println(message +
                    (showThread ? " "+Thread.currentThread().getName() : ""));
            return RepeatStatus.FINISHED;
        };
    }

    public TaskletStep getTaskletStep() {
        String stepName = this.stepName;
        return new StepBuilder(stepName, jobRepository)
                .tasklet(getTasklet(), transactionManager).build();
    }

    public Job getJob() {
        String jobName = this.jobName;
        return new JobBuilder(jobName, jobRepository)
                .start(getTaskletStep()).build();
    }
}

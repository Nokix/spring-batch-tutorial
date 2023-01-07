package com.example.springbatchtutorial;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    @Bean
    Job chainedSteps() {
        TaskletStep step1 = soutBuilder.setMessage("Message 1").setStepName("Step 1").getTaskletStep();
        TaskletStep step2 = soutBuilder.setMessage("Message 2").setStepName("Step 2").getTaskletStep();
        TaskletStep step3 = soutBuilder.setMessage("Message 3").setStepName("Step 3").getTaskletStep();

        return new JobBuilder("chainedJob", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @Bean
//    @Primary
    Job chainedStepsWithStatus() {
        TaskletStep step1 = soutBuilder.setMessage("Message 1").setStepName("Step 1").getTaskletStep();
        TaskletStep step2 = soutBuilder.setMessage("Message 2").setStepName("Step 2").getTaskletStep();
        TaskletStep step3 = soutBuilder.setMessage("Message 3").setStepName("Step 3").getTaskletStep();

        return new JobBuilder("chainedJob", jobRepository)
                .start(step1)
                .on("COMPLETED")
                .to(step2)
                .from(step2)
                .on("COMPLETED")
                .to(step3)
                .end()
                .build();
    }


    Flow createSmallFlow() {
        TaskletStep step1 = soutBuilder.setMessage("Message 1").setStepName("Step 1").getTaskletStep();
        TaskletStep step2 = soutBuilder.setMessage("Message 2").setStepName("Step 2").getTaskletStep();
        TaskletStep step3 = soutBuilder.setMessage("Message 3").setStepName("Step 3").getTaskletStep();
        return new FlowBuilder<Flow>("3 Part Flow").start(step1).next(step2).next(step3).end();
    }

    @Bean
    //@Primary
    Job flowFirstJob() {
        TaskletStep step0 = soutBuilder.setMessage("Message 0").setStepName("Step 0").getTaskletStep();
        Flow flow = createSmallFlow();
        return new JobBuilder("flowFirstJob", jobRepository)
                .start(flow).next(step0)
                .end().build();
    }

    @Bean
    @Primary
    Job flowLastJob() {
        TaskletStep step0 = soutBuilder.setMessage("Message 0").setStepName("Step 0").getTaskletStep();
        Flow flow = createSmallFlow();
        return new JobBuilder("flowFirstJob", jobRepository)
                .start(step0).on("COMPLETED").to(flow)
                .end().build();
    }
}

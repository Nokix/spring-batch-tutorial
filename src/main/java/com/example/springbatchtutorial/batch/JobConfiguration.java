package com.example.springbatchtutorial.batch;

import com.example.springbatchtutorial.entity.Student;
import com.example.springbatchtutorial.repository.StudentRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

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
//    @Primary
    Job flowLastJob() {
        TaskletStep step0 = soutBuilder.setMessage("Message 0").setStepName("Step 0").getTaskletStep();
        Flow flow = createSmallFlow();
        return new JobBuilder("flowFirstJob", jobRepository)
                .start(step0).on("COMPLETED").to(flow)
                .end().build();
    }

    @Bean
//    @Primary
    Job parallelFlows() {
        TaskletStep stepA1 = soutBuilder.setMessage("Message A1").setStepName("Step A1").setShowThread(true).getTaskletStep();
        TaskletStep stepA2 = soutBuilder.setMessage("Message A2").setStepName("Step A2").setShowThread(true).getTaskletStep();
        TaskletStep stepA3 = soutBuilder.setMessage("Message A3").setStepName("Step A3").setShowThread(true).getTaskletStep();
        Flow flowA = new FlowBuilder<Flow>("3 Part A Flow").start(stepA1).next(stepA2).next(stepA3).end();


        TaskletStep stepB1 = soutBuilder.setMessage("Message B1").setStepName("Step B1").setShowThread(true).getTaskletStep();
        TaskletStep stepB2 = soutBuilder.setMessage("Message B2").setStepName("Step B2").setShowThread(true).getTaskletStep();
        TaskletStep stepB3 = soutBuilder.setMessage("Message B3").setStepName("Step B3").setShowThread(true).getTaskletStep();
        Flow flowB = new FlowBuilder<Flow>("3 Part B Flow").start(stepB1).next(stepB2).next(stepB3).end();

        return new JobBuilder("paralellFlows", jobRepository)
                .start(flowA).split(new SimpleAsyncTaskExecutor()).add(flowB).end().build();
    }

    @Bean
//    @Primary
    Job deciderJob() {
        TaskletStep step1 = soutBuilder.setMessage("Initial Step").setStepName("Step I").getTaskletStep();
        TaskletStep step2 = soutBuilder.setMessage("Repeating Step").setStepName("Step R").getTaskletStep();

        RepeatDecider repeatDecider = new RepeatDecider(3);

        return new JobBuilder("deciderJob", jobRepository)
                .start(step1)
                .next(repeatDecider)
                .from(repeatDecider).on(RepeatDecider.REPEAT).to(step2)
                .from(repeatDecider).on(RepeatDecider.STOP).end()
                .from(step2).on("*").to(repeatDecider).end()
                .build();
    }

    @Bean
//    @Primary
    Job nestedJob(JobLauncher jobLauncher) {
        TaskletStep step0 = soutBuilder.setMessage("First Step").setStepName("Step F").getTaskletStep();
        TaskletStep step1 = soutBuilder.setMessage("Nested Step").setStepName("Step N").getTaskletStep();
        TaskletStep step2 = soutBuilder.setMessage("Last Step").setStepName("Step L").getTaskletStep();

        Job nestedJob = new JobBuilder("Nested Job", jobRepository).flow(step1).on("*").fail().end().build();

        Step nestedJobStep = new JobStepBuilder(new StepBuilder("nestedJobStep", jobRepository))
                .job(nestedJob)
                .launcher(jobLauncher)
                .repository(jobRepository)
                .build();

        return new JobBuilder("outer Job", jobRepository)
                .start(step0).next(nestedJobStep).next(step2).build();
    }

    @Bean
//    @Primary
    Job jobWithListener() {

        JobExecutionListener listener = new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("LET'S DO IT, "
                        + jobExecution.getJobInstance().getJobName());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("YOU'VE DONE IT, "
                        + jobExecution.getJobInstance().getJobName());
            }
        };

        Flow flow = createSmallFlow();

        return new JobBuilder("very verbose Job", jobRepository)
                .listener(listener)
                .start(flow)
                .end().build();
    }


    @Bean
//    @Primary
    Job jobWithListener2() {
        return new JobBuilder("very verbose Job", jobRepository)
                .listener(new VerboseJobListener())
                .start(createSmallFlow())
                .end().build();
    }

    @Bean
//    @Primary
    Job iterableReaderJob() {
        ChunkListener chunkListener = new ChunkListener() {
            @Override
            public void beforeChunk(ChunkContext context) {
                System.out.println("Chunk is complete: " + context.isComplete());
            }

            @Override
            public void afterChunk(ChunkContext context) {
                System.out.println("Chunk is complete: " + context.isComplete());
            }
        };

        List<String> list = List.of("a", "b", "c", "d", "e", "f", "g");

        TaskletStep listReadAndSoutStep = new StepBuilder("ListReadAndSoutStep", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .listener(chunkListener)
                .reader(new IterableReader<>(list))
                .processor(String::toUpperCase)
                .writer(chunk -> chunk.forEach(System.out::println))
                .build();

        return new JobBuilder("ListReadAndSoutJob", jobRepository)
                .start(listReadAndSoutStep)
                .build();
    }


    @Bean
//    @Primary
    Job stateFullStepJob() {

        List<String> list = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");

        TaskletStep listReadAndSoutStep = new StepBuilder("ListReadAndSoutStep", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(new StateFullListReader<>(list, 1500))
                .processor(String::toUpperCase)
                .writer(chunk -> chunk.forEach(System.out::println))
                .build();

        return new JobBuilder("StatefullStepJob", jobRepository)
                .start(listReadAndSoutStep)
                .build();
    }

    @Bean
    @Qualifier("messageTaskletStep")
    @StepScope
    @Scope(proxyMode = ScopedProxyMode.DEFAULT)
    TaskletStep getStepWithMessageValue(@Value("${jobParameters['message']:arr}") String message) {
        return soutBuilder.setMessage(message).setStepName("Step 1").getTaskletStep();
    }

    @Bean
//    @Primary
    Job paremterJob(@Qualifier("messageTaskletStep") TaskletStep taskletStep) {

        return new JobBuilder("chainedJob", jobRepository)
                .start(taskletStep)
                .build();
    }

    @Bean
    @Primary
    Job databaseJob(StudentRepository studentRepository) {
        RepositoryItemReader<Student> reader = new RepositoryItemReader<>();
        reader.setRepository(studentRepository);
        reader.setMethodName("findAll");
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        reader.setPageSize(10);

        TaskletStep step = new StepBuilder("printStudentsStep", jobRepository)
                .<Student, Student>chunk(10, transactionManager)
                .reader(reader)
                .writer(chunk -> chunk.forEach(System.out::println)).build();

        return new JobBuilder("printStudentsJob", jobRepository)
                .start(step).build();
    }

}

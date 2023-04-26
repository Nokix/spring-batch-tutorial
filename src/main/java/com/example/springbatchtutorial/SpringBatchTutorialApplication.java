package com.example.springbatchtutorial;

import com.example.springbatchtutorial.entity.Student;
import com.example.springbatchtutorial.faker.FakeMachine;
import com.example.springbatchtutorial.repository.StudentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchTutorialApplication {

	public static void main(String[] args) throws JobInstanceAlreadyCompleteException,
			JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

		ConfigurableApplicationContext context
				= SpringApplication.run(SpringBatchTutorialApplication.class, args);

		FakeMachine fakeMachine = context.getBean(FakeMachine.class);
		StudentRepository studentRepository = context.getBean(StudentRepository.class);
		studentRepository.saveAll(fakeMachine.fakeStudents(100));

		JobParameters jobParameters
				= new JobParametersBuilder()
				.addLong("startAt", 100L)//System.currentTimeMillis())
				.toJobParameters();

		Job job = context.getBean(Job.class);
		JobLauncher jobLauncher = context.getBean(JobLauncher.class);

		jobLauncher.run(job, jobParameters);

		context.close();
	}
}


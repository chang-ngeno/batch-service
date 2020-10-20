package core.batch.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@EnableScheduling
@AllArgsConstructor
public class BatchScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchScheduler.class);

	private Job employeeJob;
	private JobLauncher jobLauncher;


	@Scheduled(cron = "${batch.scheduler.test}")
	public void test() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		LOGGER.info("The test Scheduler has been fired: " + sdf.format(new Date()));
	}

	@Scheduled(cron = "${batch.scheduler.employee}")
	public void schedule() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		jobLauncher.run(employeeJob, new JobParametersBuilder()
				.addDate("date", new Date())
				.toJobParameters());
	}
}

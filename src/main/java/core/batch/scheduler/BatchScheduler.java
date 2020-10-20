package core.batch.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BatchScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchScheduler.class);

	@Scheduled(cron = "${batch.scheduler.test}")
	public void test() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		LOGGER.info("The test Scheduler has been fired: " + sdf.format(new Date()));
	}
}

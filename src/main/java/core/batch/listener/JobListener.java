package core.batch.listener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import core.batch.DTO.EmployeeDTO;

@Configuration
public class JobListener extends JobExecutionListenerSupport {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("In Completion Listener ..");
			List<EmployeeDTO> results = jdbcTemplate.query(
					"SELECT first_name,last_name,company_name,address,city,county,state,zip FROM employee",
					(rs, rowNum) -> {
						return new EmployeeDTO(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
					});
			results.forEach(System.out::println);

		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			System.out.println("Failed to complete batch job, JobId: " + jobExecution.getJobId());
		}
		renameFile();
	}

	public void renameFile() {
		File oldFile = new File("");
		File newFile = new File("employee.csv.bkp");
		try {
			oldFile = new ClassPathResource("employee.csv").getFile();
			newFile = new File(oldFile.getParentFile() + "/employee_" // Category.FORMAT.ordinal(), Category.FORMAT.ordinal(), Locale.UK
					+ new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + ".csv");
			System.out.println("The file path is: " + newFile.getAbsolutePath());
			System.out.println("Renaming processed file: " + oldFile + " to: " + newFile);
			oldFile.renameTo(newFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}

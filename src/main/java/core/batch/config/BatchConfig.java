package core.batch.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import core.batch.DTO.EmployeeDTO;
import core.batch.listener.JobListener;
import core.batch.model.Employee;
import core.batch.processor.EmployeeProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public FlatFileItemReader<Employee> reader() {
		LOGGER.info("Entry :: BatchConfig.class :: reader()");
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
		/*
		 * if (!new ClassPathResource("employee.csv").exists()) { reader = null;
		 * LOGGER.warn("Employee :: reader() file not found"); return reader; }
		 */
		reader.setResource(new ClassPathResource("employee.csv"));
		
		reader.setLineMapper(new DefaultLineMapper<Employee>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "first_name", "last_name", "company_name", "address", "city", "county",
								"state", "zip" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
					{
						setTargetType(Employee.class);
					}
				});
			}
		});

		return reader;
	}

	@Bean
	public EmployeeProcessor processor() {
		return new EmployeeProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<EmployeeDTO> writer() {
		JdbcBatchItemWriter<EmployeeDTO> writer = new JdbcBatchItemWriter<EmployeeDTO>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setSql("INSERT INTO employee (first_name,last_name,company_name,address,city,county,state,zip) "
				+ "VALUES (:firstName, :lastName,:companyName,:address,:city,:county,:state,:zip)");
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public Job importUserJob(JobListener listener) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step1()).end().build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Employee, EmployeeDTO>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

}

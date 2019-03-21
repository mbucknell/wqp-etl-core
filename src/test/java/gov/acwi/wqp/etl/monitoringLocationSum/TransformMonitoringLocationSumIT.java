package gov.acwi.wqp.etl.monitoringLocationSum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.acwi.wqp.etl.BaseFlowIT;
import gov.acwi.wqp.etl.monitoringLocationSum.index.BuildMonitoringLocationSumIndexesFlowIT;
import gov.acwi.wqp.etl.monitoringLocationSum.table.SetupMonitoringLocationSumSwapTableFlowIT;

public class TransformMonitoringLocationSumIT extends BaseFlowIT {

	@Autowired
	@Qualifier("monitoringLocationSumFlow")
	private Flow monitoringLocationSumFlow;

	@PostConstruct
	public void beforeClass() throws ScriptException, SQLException {
		EncodedResource encodedResource = new EncodedResource(resource, Charset.forName("UTF-8"));
		ScriptUtils.executeSqlScript(dataSource.getConnection(), encodedResource);
	}

	@Before
	public void setup() {
		testJob = jobBuilderFactory.get("monitoringLocationSumFlowTest")
				.start(monitoringLocationSumFlow)
				.build()
				.build();
		jobLauncherTestUtils.setJob(testJob);
	}

	@Test
	@DatabaseSetup(value="classpath:/testResult/wqp/monitoringLocationSum/empty.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/station/station.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/activity/activity.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/result/result.xml")
//	@ExpectedDatabase(value="classpath:/testResult/wqp/monitoringLocationSum/monitoringLocationSum.xml", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void transformMonitoringLocationSumStepTest() {
		try {
			JobExecution jobExecution = jobLauncherTestUtils.launchStep("transformMonitoringLocationSumStep", testJobParameters);
			assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DatabaseSetup(value="classpath:/testResult/wqp/monitoringLocationSum/empty.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/station/station.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/activity/activity.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/result/result.xml")
	@ExpectedDatabase(value="classpath:/testResult/wqp/monitoringLocationSum/indexes/all.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=BuildMonitoringLocationSumIndexesFlowIT.EXPECTED_DATABASE_TABLE,
			query=BuildMonitoringLocationSumIndexesFlowIT.EXPECTED_DATABASE_QUERY)
	@ExpectedDatabase(connection="pg", value="classpath:/testResult/wqp/monitoringLocationSum/create.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=SetupMonitoringLocationSumSwapTableFlowIT.EXPECTED_DATABASE_TABLE,
			query=SetupMonitoringLocationSumSwapTableFlowIT.EXPECTED_DATABASE_QUERY)
	@ExpectedDatabase(value="classpath:/testResult/wqp/monitoringLocationSum/monitoringLocationSum.xml", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void monitoringLocationSumFlowTest() {
		Job monitoringLocationSumFlowTest = jobBuilderFactory.get("monitoringLocationSumFlowTest")
					.start(monitoringLocationSumFlow)
					.build()
					.build();
		jobLauncherTestUtils.setJob(monitoringLocationSumFlowTest);
		try {
			JobExecution jobExecution = jobLauncherTestUtils.launchJob(testJobParameters);
			assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}

package gov.acwi.wqp.etl.summaries.activitySum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.acwi.wqp.etl.BaseFlowIT;
import gov.acwi.wqp.etl.summaries.activitySum.index.BuildActivitySumIndexesFlowIT;
import gov.acwi.wqp.etl.summaries.activitySum.table.SetupActivitySumSwapTableFlowIT;

public class TransformActivitySumIT extends BaseFlowIT {

	public static final String EXPECTED_DATABASE_QUERY_ANALYZE = BASE_EXPECTED_DATABASE_QUERY_ANALYZE + "'activity_sum_swap_testsrc'";

	@Autowired
	@Qualifier("activitySumFlow")
	private Flow activitySumFlow;

	@BeforeEach
	public void setUp() {
		testJob = jobBuilderFactory.get("activitySumFlowTest")
				.start(activitySumFlow)
				.build()
				.build();
		jobLauncherTestUtils.setJob(testJob);
	}

	@Test
	@DatabaseSetup(value="classpath:/testResult/wqp/activitySum/empty.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/activity/csv/")
	@DatabaseSetup(value="classpath:/testData/wqp/activityMetric/activityMetric.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/result/csv/")
	@ExpectedDatabase(value="classpath:/testResult/wqp/activitySum/activitySum.xml", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void transformActivitySumStepTest() {
		try {
			JobExecution jobExecution = jobLauncherTestUtils.launchStep("transformActivitySumStep", testJobParameters);
			assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@ExpectedDatabase(
			value="classpath:/testResult/analyze/activitySum.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=EXPECTED_DATABASE_TABLE_CHECK_ANALYZE,
			query=EXPECTED_DATABASE_QUERY_ANALYZE)
	public void analyzeActivitySumStepTest() {
		try {
			JobExecution jobExecution = jobLauncherTestUtils.launchStep("analyzeActivitySumStep", testJobParameters);
			assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DatabaseSetup(value="classpath:/testData/wqp/activity/csv/")
	@DatabaseSetup(value="classpath:/testData/wqp/activityMetric/activityMetric.xml")
	@DatabaseSetup(value="classpath:/testData/wqp/result/csv/")
	@ExpectedDatabase(
			value="classpath:/testResult/wqp/activitySum/indexes/all.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=EXPECTED_DATABASE_TABLE_CHECK_INDEX,
			query=BuildActivitySumIndexesFlowIT.EXPECTED_DATABASE_QUERY)
	@ExpectedDatabase(
			connection=CONNECTION_INFORMATION_SCHEMA,
			value="classpath:/testResult/wqp/activitySum/create.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=EXPECTED_DATABASE_TABLE_CHECK_TABLE,
			query=SetupActivitySumSwapTableFlowIT.EXPECTED_DATABASE_QUERY)
	@ExpectedDatabase(value="classpath:/testResult/wqp/activitySum/activitySum.xml", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/analyze/activitySum.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=EXPECTED_DATABASE_TABLE_CHECK_ANALYZE,
			query=EXPECTED_DATABASE_QUERY_ANALYZE)
	public void activitySumFlowTest() {
		try {
			JobExecution jobExecution = jobLauncherTestUtils.launchJob(testJobParameters);
			assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}

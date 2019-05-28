package gov.acwi.wqp.etl.biologicalHabitatMetric.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

public class BuildBiologicalHabitatMetricHuc4IndexIT extends BaseBuildBiologicalHabitatMetricIndexesIT {

	@Test
	@ExpectedDatabase(value="classpath:/testResult/wqp/biologicalHabitatMetric/indexes/huc4.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=EXPECTED_DATABASE_TABLE_CHECK_INDEX,
			query=EXPECTED_DATABASE_QUERY + " and indexname='bio_hab_metric_swap_testsrc_huc_4'")
	public void buildBiologicalHabitatMetricHuc4IndexStepTest() {
		try {
			JobExecution jobExecution = jobLauncherTestUtils
					.launchStep("buildBiologicalHabitatMetricHuc4IndexStep", testJobParameters);
			assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
}

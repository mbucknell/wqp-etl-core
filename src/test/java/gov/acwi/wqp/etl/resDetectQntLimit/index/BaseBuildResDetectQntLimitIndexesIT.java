package gov.acwi.wqp.etl.resDetectQntLimit.index;

import org.junit.Before;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import gov.acwi.wqp.etl.BaseFlowIT;
import gov.acwi.wqp.etl.EtlConstantUtils;

public abstract class BaseBuildResDetectQntLimitIndexesIT extends BaseFlowIT {

	public static final String EXPECTED_DATABASE_QUERY_ANALYZE = BASE_EXPECTED_DATABASE_QUERY_CHECK_INDEX + "'r_detect_qnt_lmt_swap_testsrc'";

	@Autowired
	@Qualifier(EtlConstantUtils.BUILD_RES_DETECT_QNT_LIMIT_INDEXES_FLOW)
	private Flow buildResDetectQntLimitIndexesFlow;

	@Before
	public void setUp() {
		testJob = jobBuilderFactory.get("BuildResDetectQntLimitIndexesFlowTest")
				.start(buildResDetectQntLimitIndexesFlow)
				.build()
				.build();
		jobLauncherTestUtils.setJob(testJob);
	}
}

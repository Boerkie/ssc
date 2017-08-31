package rewards.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import common.money.MonetaryAmount;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.TestInfrastructureConfig;

public class RewardNetworkTests {

	/**
	 * 1. Create the system test class
	 */
	private RewardNetwork rewardNetwork;

	/**
	 * 2. Implement test setup logic
	 */
	@Before
	public void setUp() throws Exception {

		// Call SpringApplication.run, providing it the TestInfrastructureConfig.class
		// that you want to load
		ApplicationContext context = SpringApplication.run(TestInfrastructureConfig.class);

		// Ask the context to get the rewardNetwork bean
		rewardNetwork = context.getBean(RewardNetwork.class);
	}

	/**
	 * 3. Implement test logic
	 */
	@Test
	public void testRewardForDining() {
		// create a new dining of 100.00 charged to credit card '1234123412341234' by
		// merchant '123457890' as test input
		Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");

		// call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
		RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

		// assert the expected reward confirmation results
		assertNotNull(confirmation);
		assertNotNull(confirmation.getConfirmationNumber());

		// assert an account contribution was made
		AccountContribution contribution = confirmation.getAccountContribution();
		assertNotNull(contribution);

		// the account number should be '123456789'
		assertEquals("123456789", contribution.getAccountNumber());

		// the total contribution amount should be 8.00 (8% of 100.00)
		assertEquals(MonetaryAmount.valueOf("8.00"), contribution.getAmount());

		// the total contribution amount should have been split into 2 distributions
		assertEquals(2, contribution.getDistributions().size());

		// each distribution should be 4.00 (as both have a 50% allocation)
		assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Annabelle").getAmount());
		assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Corgan").getAmount());
	}
}
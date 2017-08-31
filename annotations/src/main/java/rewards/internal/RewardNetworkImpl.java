package rewards.internal;

import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.money.MonetaryAmount;

/**
 * Rewards an Account for Dining at a Restaurant.
 * 
 * The sole Reward Network implementation. This object is an application-layer
 * service responsible for coordinating with the domain-layer to carry out the
 * process of rewarding benefits to accounts for dining.
 * 
 * Said in other words, this class implements the "reward account for dining"
 * use case.
 * 
 * Annotate the class with an appropriate stereotype annotation to cause
 * component-scan to detect and load this bean.
 */

@Service
public class RewardNetworkImpl implements RewardNetwork {

	private AccountRepository accountRepository;

	private RestaurantRepository restaurantRepository;

	private RewardRepository rewardRepository;

	/**
	 * Creates a new reward network.
	 * 
	 * Configure Dependency Injection for all 3 dependencies. Decide if you should
	 * use field level or constructor injection.
	 * 
	 * Can see from method body that it makes use of Constructor-injection.
	 * 
	 * @param accountRepository the repository for loading accounts to reward
	 * @param restaurantRepository the repository for loading restaurants that
	 * determine how much to reward
	 * @param rewardRepository the repository for recording a record of successful
	 * reward transactions
	 */

	@Autowired
	public RewardNetworkImpl(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
			RewardRepository rewardRepository) {
		this.accountRepository = accountRepository;
		this.restaurantRepository = restaurantRepository;
		this.rewardRepository = rewardRepository;
	}

	public RewardConfirmation rewardAccountFor(Dining dining) {
		Account account = accountRepository.findByCreditCard(dining.getCreditCardNumber());
		Restaurant restaurant = restaurantRepository.findByMerchantNumber(dining.getMerchantNumber());
		MonetaryAmount amount = restaurant.calculateBenefitFor(account, dining);
		AccountContribution contribution = account.makeContribution(amount);
		accountRepository.updateBeneficiaries(account);
		return rewardRepository.confirmReward(contribution, dining);
	}
}
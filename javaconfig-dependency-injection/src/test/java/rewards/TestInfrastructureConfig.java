package rewards;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import config.RewardsConfig;

@Configuration
//TestInfrastructureConfig used in tests, and the tests also requires the RewardNetwork
@Import(RewardsConfig.class)
public class TestInfrastructureConfig {

	/**
	 * Creates an in-memory "rewards" database populated 
	 * with test data for fast testing
	 * Infrastructure bean
	 */
	@Bean
	public DataSource dataSource(){
		return
			(new EmbeddedDatabaseBuilder())
			.addScript("classpath:rewards/testdb/schema.sql")
			.addScript("classpath:rewards/testdb/data.sql")
			.build();
	}	
}

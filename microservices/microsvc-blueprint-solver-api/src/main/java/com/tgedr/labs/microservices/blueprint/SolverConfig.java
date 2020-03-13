package com.tgedr.labs.microservices.blueprint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableAutoConfiguration
@Configuration
@EnableSwagger2
public class SolverConfig {

	@Value("${io.blocking.coefficient:0}")
	private double ioBlockingCoefficient;

	/**
	 * here we try to optimize the task executor taking into account
	 * the IO/computing ratio that the tasks will eventually take
	 * @return
	 */
	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {

		if (1 < ioBlockingCoefficient || 0 > ioBlockingCoefficient)
			throw new RuntimeException("blockingCoefficient should be in between [0.0,1.0]");

		// one decimal only
		ioBlockingCoefficient = ((int) (ioBlockingCoefficient / 0.1)) * 0.1;

		int corePoolSize = Runtime.getRuntime().availableProcessors();
		int maxPoolSize = (int) (ioBlockingCoefficient == 1.0 ? corePoolSize * 10
				: corePoolSize / (1 - ioBlockingCoefficient));

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setThreadNamePrefix("knapsack-core-pool-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.initialize();
		executor.setDaemon(true);
		return executor;
	}

}

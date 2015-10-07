package pl.rembol.jme3.world.threads;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class ThreadManager {

	private Map<Executor, ExecutorService> executorMap = new HashMap<>();

	@PostConstruct
	private void setUp() {
		executorMap
				.put(Executor.PARTIAL_PATH, Executors.newFixedThreadPool(20));
		executorMap.put(Executor.FULL_PATH, Executors.newFixedThreadPool(10));
	}

	@PreDestroy
	private void tearDown() {
		for (ExecutorService executor : executorMap.values()) {
			executor.shutdown();
		}
	}

	public <T> Future<T> submit(Executor executor, Callable<T> caller) {
		return executorMap.get(executor).submit(caller);
	}

}

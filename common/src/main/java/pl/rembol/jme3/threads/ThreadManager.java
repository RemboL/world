package pl.rembol.jme3.threads;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager {

    private Map<Executor, ExecutorService> executorMap = new HashMap<>();

    public ThreadManager() {
        executorMap
                .put(Executor.PARTIAL_PATH, Executors.newFixedThreadPool(20));
        executorMap.put(Executor.FULL_PATH, Executors.newFixedThreadPool(10));
    }

    public void tearDown() {
        executorMap.values().forEach(ExecutorService::shutdown);
    }

    public <T> Future<T> submit(Executor executor, Callable<T> caller) {
        return executorMap.get(executor).submit(caller);
    }

}

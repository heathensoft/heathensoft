package io.github.heathensoft.astar;


import io.github.heathensoft.common.Disposable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


public class RequestService implements Disposable {
    
    private static final int MAX_POOL_SIZE = 8;
    private final ExecutorService executor;
    
    public RequestService(int threads) {
        threads = Math.max(1,Math.min(MAX_POOL_SIZE,threads));
        executor = new ThreadPoolExecutor(
                threads,
                MAX_POOL_SIZE,
                3000,
                TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>()
        );
    }
    
    public void handle(PathRequest request) {
        validateRequest(request);
        executor.submit(new Search(request));
    }
    
    public static void handleDirect(PathRequest request) {
        validateRequest(request);
        new Search(request).run();
    }
    
    private static void validateRequest(PathRequest request) {
        if (request == null || request.resolved())
            throw new IllegalStateException("Request == null OR resolved");
    }
    /**
     * Should be within a finally-clause
     */
    @Override
    public void dispose() {
        executor.shutdown();
    }
}

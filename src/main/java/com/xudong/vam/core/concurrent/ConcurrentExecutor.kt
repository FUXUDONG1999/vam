package com.xudong.vam.core.concurrent;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConcurrentExecutor {
    private static final ExecutorService EXECUTOR_SERVICE = new ForkJoinPool(100);

    public <P, R> List<CompletableFuture<R>> executeAll(List<P> parameters, Function<P, R> function) {
        if (parameters == null || function == null) {
            throw new IllegalArgumentException("Parameters and function cannot be null");
        }

        List<CompletableFuture<R>> futures = new ArrayList<>();
        for (P parameter : parameters) {
            CompletableFuture<R> future = CompletableFuture.supplyAsync(
                    () -> function.apply(parameter),
                    EXECUTOR_SERVICE
            );

            futures.add(future);
        }

        return futures;
    }

    public <R> CompletableFuture<R> execute(Callback<R> function) {
        return CompletableFuture.supplyAsync(function::call, EXECUTOR_SERVICE);
    }

    public void run(Runnable runnable) {
        CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE);
    }

    public <R> List<R> wait(List<CompletableFuture<R>> futures) {
        if (futures == null || futures.isEmpty()) {
            return null;
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface Callback<R> {
        R call();
    }
}

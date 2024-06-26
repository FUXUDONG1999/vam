package com.xudong.vam.core.concurrent

import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.stream.Collectors

private val EXECUTOR_SERVICE: ExecutorService = ForkJoinPool(100)

@Component
class ConcurrentExecutor {
    fun <P, R> executeAll(parameters: List<P>, function: (P) -> R): List<CompletableFuture<R>> {
        val futures = ArrayList<CompletableFuture<R>>()
        for (parameter in parameters) {
            val future = CompletableFuture.supplyAsync({
                function(parameter)
            }, EXECUTOR_SERVICE)

            futures.add(future)
        }

        return futures
    }

    fun <R> execute(function: () -> R): CompletableFuture<R> {
        return CompletableFuture.supplyAsync(function, EXECUTOR_SERVICE)
    }

    fun run(runnable: () -> Unit) {
        CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE)
    }

    fun <R> wait(futures: List<CompletableFuture<R>>): List<R> {
        return futures.stream()
            .map { obj ->
                obj.join()
            }
            .collect(Collectors.toList())
    }
}

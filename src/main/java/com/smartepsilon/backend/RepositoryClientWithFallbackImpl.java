package com.smartepsilon.backend;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smartepsilon.backend.exception.ExternalServiceUnhealthy;

public class RepositoryClientWithFallbackImpl implements RepositoryClientWithFallback {

    private final WebTarget webTarget;
    private final WebTarget fallbackTarget;
    private final long timeoutMilis;
    private final int retriesThreshold;

    private interface ResponseSupplier {
        Response supply() throws InterruptedException, ExecutionException, TimeoutException; 
    }
    
    public RepositoryClientWithFallbackImpl(final WebTarget webTarget,
                                            final WebTarget fallbackTarget,
                                            final long timeoutMilis,
                                            final int retriesThreshold) {
        this.webTarget = webTarget;
        this.fallbackTarget = fallbackTarget;
        this.timeoutMilis = timeoutMilis;
        this.retriesThreshold = retriesThreshold;
    }

    @Override
    public Response getRepository(final String owner, final String id) {
        return tryObtainingResponseWithAtMostGivenNumberOfTrials(owner, id, retriesThreshold)
                   .orElseGet(() -> tryObtainingFallbackResponse(owner, id, retriesThreshold)
                                        .orElseThrow(() -> new ExternalServiceUnhealthy(retriesThreshold)));
    }
    
    private Optional<Response> tryObtainingResponseWithAtMostGivenNumberOfTrials(String owner, 
                                                                                 String id, 
                                                                                 int trialsNumber) {
        Callable<Response> pureTask = supplyTaskDefinition(owner, id);
        return callServiceWithAtMostTrialsCountTimes(owner, id, trialsNumber, pureTask);
    }

    private Optional<Response> tryObtainingFallbackResponse(String owner, 
                                                            String id,
                                                            int trialsCount) {
        Callable<Response> fallbackTask = supplyFallbackTask(owner, id);
        return callServiceWithAtMostTrialsCountTimes(owner, id, trialsCount, fallbackTask);
    }

    private Optional<Response> callServiceWithAtMostTrialsCountTimes(String owner, 
                                            String id, 
                                            int trialsCount,
                                            Callable<Response> task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Supplier<Optional<Response>> catchingSupplier = () -> {
            try {
                return Optional.of(getResponseSupplier(executor, task).supply());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // logging of e ommitted for clarity
                return Optional.empty();
            }
        };
        return Stream.iterate(catchingSupplier, i -> i)
                .limit(trialsCount)
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }
    
    private ResponseSupplier getResponseSupplier(ExecutorService executor, Callable<Response> callableTask) throws InterruptedException, 
                                                                                                                   ExecutionException, 
                                                                                                                   TimeoutException {
         return () -> {
             Future<Response> submittedTask = executor.submit(callableTask);
             return submittedTask.get(timeoutMilis, TimeUnit.MILLISECONDS);
         };
    }

    private Callable<Response> supplyFallbackTask(String owner, String id) {
        return supplyInternal(fallbackTarget, owner, id);
    }

    private Callable<Response> supplyTaskDefinition(String owner, String id) {
        return supplyInternal(webTarget, owner, id);
    }

    private Callable<Response> supplyInternal(WebTarget target, String owner, String id) {
        return () -> target.resolveTemplate("owner", owner).resolveTemplate("id", id)
                .request(MediaType.APPLICATION_JSON).get();
    }
}

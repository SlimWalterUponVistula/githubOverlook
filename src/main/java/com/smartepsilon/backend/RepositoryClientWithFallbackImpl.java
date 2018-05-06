package com.smartepsilon.backend;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smartepsilon.backend.exception.ExternalServiceUnhealthy;

public class RepositoryClientWithFallbackImpl implements RepositoryClientWithFallback {

	private final WebTarget webTarget;
	private final WebTarget fallbackTarget;
	private final long timeoutMilis;
	private final int retriesThreshold;

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
		Callable<Response> pureTask = supplyTaskDefinition(owner, id);
		return tryInternallyFirstWithOriginalThenWithFallback(owner, id, pureTask, false);
	}

	private Response tryInternallyFirstWithOriginalThenWithFallback(String owner, String id, Callable<Response> callableTask, boolean fallbackTry) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		int trial = 0;
		while (trial < retriesThreshold) {
			try {
				Future<Response> submittedTask = executor.submit(callableTask);
				return submittedTask.get(timeoutMilis, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				if (++trial == retriesThreshold) {
					if (fallbackTry) {
						throw new ExternalServiceUnhealthy(retriesThreshold, e);
					}
					Callable<Response> fallbackTask = supplyFallbackTask(owner, id);
					return tryInternallyFirstWithOriginalThenWithFallback(owner, id, fallbackTask, true);
				}
			}
		}
		throw new IllegalStateException("Unreachable state!");
	}

	private Callable<Response> supplyFallbackTask(String owner, String id) {
		return supplyInternal(fallbackTarget, owner, id);
	}


	private Callable<Response> supplyTaskDefinition(String owner, String id) {
		return supplyInternal(webTarget, owner, id);
	}

	private Callable<Response> supplyInternal(WebTarget target, String owner, String id) {
		return () -> target
				         .resolveTemplate("owner", owner)
		                 .resolveTemplate("id", id)
		                 .request(MediaType.APPLICATION_JSON)
		                 .get();
	}
}

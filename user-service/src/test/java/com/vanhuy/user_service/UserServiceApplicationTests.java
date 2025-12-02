package com.vanhuy.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

class UserServiceApplicationTests {

	@Test
	void loadTestSimulation() throws Exception {
		// Start a lightweight in-process HTTP server on a random port
		HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
		server.createContext("/test", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				byte[] resp = "ok".getBytes();
				exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
				exchange.sendResponseHeaders(200, resp.length);
				exchange.getResponseBody().write(resp);
				exchange.close();
			}
		});
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		int port = server.getAddress().getPort();

		// Configure load test parameters (kept modest for CI speed)
		final int totalRequests = 200;
		final int concurrency = 40;

		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(2))
				.build();

		ExecutorService executor = Executors.newFixedThreadPool(concurrency);
		AtomicInteger successCount = new AtomicInteger(0);

		List<Callable<Void>> tasks = new ArrayList<>();
		for (int i = 0; i < totalRequests; i++) {
			tasks.add(() -> {
				try {
					HttpRequest req = HttpRequest.newBuilder()
							.uri(URI.create("http://localhost:" + port + "/test"))
							.timeout(Duration.ofSeconds(3))
							.GET()
							.build();

					HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
					if (resp.statusCode() == 200 && "ok".equals(resp.body())) {
						successCount.incrementAndGet();
					}
				} catch (Exception e) {
					// swallow per-request exceptions; we'll assert after
				}
				return null;
			});
		}

		// Run all tasks and wait for completion (bounded wait to avoid CI hangs)
		executor.invokeAll(tasks);
		executor.shutdown();
		executor.awaitTermination(20, TimeUnit.SECONDS);

		// Stop server
		server.stop(0);

		// Assert all requests succeeded
		assertEquals(totalRequests, successCount.get(), "Some requests failed during the simulated load test");
	}

}

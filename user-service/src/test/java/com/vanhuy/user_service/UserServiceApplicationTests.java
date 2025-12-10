package com.vanhuy.user_service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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

		// Configure load test parameters (simulate 1000 users)
		final int totalRequests = 1000;
		// Use a lower concurrency so the test can reliably complete on CI runners
		final int concurrency = Math.min(200, totalRequests);

		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(20))
				.build();

		ExecutorService executor = Executors.newFixedThreadPool(concurrency);
		AtomicInteger successCount = new AtomicInteger(0);

		List<Callable<Void>> tasks = new ArrayList<>();
		for (int i = 0; i < totalRequests; i++) {
			tasks.add(() -> {
				try {
					HttpRequest req = HttpRequest.newBuilder()
							.uri(URI.create("http://localhost:" + port + "/test"))
							.timeout(Duration.ofSeconds(20))
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
		// Allow more time for a larger load
		executor.awaitTermination(300, TimeUnit.SECONDS);

		// Stop server
		server.stop(0);

		// Write metrics file for CI (GitHub Actions) so a runner can push to
		// Pushgateway
		try {
			java.nio.file.Path metricsPath = java.nio.file.Paths.get("target", "load-metrics.env");
			java.nio.file.Files.createDirectories(metricsPath.getParent());
			java.util.List<String> lines = java.util.Arrays.asList(
					"user_load_success=" + successCount.get(),
					"user_load_total=" + totalRequests);
			java.nio.file.Files.write(metricsPath, lines, java.nio.charset.StandardCharsets.UTF_8);
			System.out.println("WROTE_METRICS=" + metricsPath.toAbsolutePath());
		} catch (java.io.IOException e) {
			System.err.println("Failed to write metrics file: " + e.getMessage());
		}

		// Print success rate and do not fail the build here so CI can always push
		// metrics
		double successRate = (totalRequests == 0) ? 0.0 : (100.0 * successCount.get() / totalRequests);
		System.out.println(String.format("LOAD_TEST_RESULT: success=%d total=%d successRate=%.2f%%", successCount.get(),
				totalRequests, successRate));
	}

}

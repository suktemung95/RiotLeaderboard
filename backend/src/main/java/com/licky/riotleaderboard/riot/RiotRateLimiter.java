package com.licky.riotleaderboard.riot;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class RiotRateLimiter{

    private final Deque<Instant> requestTimes = new ArrayDeque<>();
    private final Object lock = new Object();

    public void acquire() {

        while (true) {

            long waitMillis;

            synchronized (lock) {

                Instant now = Instant.now();

                // remove anything longer than 120 seconds ago
                while (!requestTimes.isEmpty()
                        && requestTimes.peekFirst().isBefore(now.minusSeconds(120))) {
                    requestTimes.removeFirst();
                }

                // check last second
                long requestsLastSecond = requestTimes.stream()
                        .filter(t -> t.isAfter(now.minusSeconds(1)))
                        .count();

                if (requestsLastSecond < 20 && requestTimes.size() < 100) {
                    requestTimes.addLast(now);
                    return;
                }

                Instant oldestReqLastSec = requestTimes.stream()
                        .filter(t -> t.isAfter(now.minusSeconds(1)))
                        .findFirst()
                        .orElse(Instant.now());

                Instant allowedAt = null;

                if (requestsLastSecond >= 20) {
                    allowedAt = oldestReqLastSec.plusSeconds(1);
                }

                if (requestTimes.size() >= 100) {
                    Instant longLimitClearsAt =
                            requestTimes.peekFirst().plusSeconds(120);

                    if (allowedAt == null || longLimitClearsAt.isAfter(allowedAt)) {
                        allowedAt = longLimitClearsAt;
                    }
                }

                waitMillis = Math.max(1, Duration.between(Instant.now(), allowedAt).toMillis());
            }
            try {
            Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for Riot Rate Limit", e);
            }
        }
    }
}

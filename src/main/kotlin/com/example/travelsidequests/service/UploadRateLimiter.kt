package com.example.travelsidequests.service

import com.example.travelsidequests.exceptionhandler.TooManyRequestsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * The upload endpoint is open, which makes it a free image host unless something
 * caps it. In-memory and per-instance is the right size for this: it costs nothing
 * and a determined abuser is a problem for a real WAF, not for us.
 */
@Component
class UploadRateLimiter(
    @Value("\${app.uploads.max-per-hour}") private val maxPerHour: Int,
) {
    private val windows = ConcurrentHashMap<String, Window>()

    fun check(client: String) {
        val now = Instant.now()

        val window = windows.compute(client) { _, existing ->
            if (existing == null || Duration.between(existing.startedAt, now) > WINDOW) {
                Window(now, 1)
            } else {
                Window(existing.startedAt, existing.count + 1)
            }
        }!!

        if (window.count > maxPerHour) {
            throw TooManyRequestsException("Too many uploads from this address. Try again later.")
        }

        if (windows.size > MAX_TRACKED) {
            windows.entries.removeIf { Duration.between(it.value.startedAt, now) > WINDOW }
        }
    }

    private class Window(val startedAt: Instant, val count: Int)

    companion object {
        private val WINDOW: Duration = Duration.ofHours(1)
        private const val MAX_TRACKED = 10_000
    }
}

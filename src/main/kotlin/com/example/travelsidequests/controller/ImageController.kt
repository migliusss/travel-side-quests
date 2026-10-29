package com.example.travelsidequests.controller

import com.example.travelsidequests.dto.ImageUploadResponse
import com.example.travelsidequests.exceptionhandler.BadRequestException
import com.example.travelsidequests.exceptionhandler.NotFoundException
import com.example.travelsidequests.service.ImageProcessor
import com.example.travelsidequests.service.ImageStorage
import com.example.travelsidequests.service.UploadRateLimiter
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/images")
class ImageController(
    private val imageStorage: ImageStorage,
    private val imageProcessor: ImageProcessor,
    private val rateLimiter: UploadRateLimiter,
) {

    @Operation(
        summary = "Upload an image for a side quest submission.",
        description = "Returns a storage key to reference from the create-side-quest request. " +
            "Unreferenced uploads are discarded after a day."
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        @RequestParam("file") file: MultipartFile,
        request: HttpServletRequest,
    ): ImageUploadResponse {
        rateLimiter.check(request.remoteAddr ?: "unknown")

        if (file.isEmpty) throw BadRequestException("No file was uploaded.")

        val normalized = imageProcessor.normalize(file.bytes)

        return ImageUploadResponse(
            storageKey = imageStorage.storePending(normalized.bytes, normalized.contentType)
        )
    }

    @Operation(summary = "Serve an uploaded image.")
    @GetMapping("/{key}")
    fun get(@PathVariable key: String): ResponseEntity<Resource> {
        // The key goes straight into an object name, so nothing but a UUID may pass.
        if (!KEY_PATTERN.matches(key)) throw NotFoundException("No image with key $key.")

        val image = imageStorage.load(key) ?: throw NotFoundException("No image with key $key.")

        return ResponseEntity.ok()
            // The key is a UUID, so the bytes behind it never change.
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
            .eTag(key)
            .contentType(MediaType.parseMediaType(image.contentType))
            .contentLength(image.size)
            .body(InputStreamResource(image.stream))
    }

    companion object {
        private val KEY_PATTERN =
            Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    }
}

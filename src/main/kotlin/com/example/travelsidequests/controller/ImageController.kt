package com.example.travelsidequests.controller

import com.example.travelsidequests.dto.ImageUploadResponse
import com.example.travelsidequests.exceptionhandler.BadRequestException
import com.example.travelsidequests.service.ImageService
import com.example.travelsidequests.service.UploadRateLimiter
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
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
    private val imageService: ImageService,
    private val rateLimiter: UploadRateLimiter,
) {

    @Operation(
        summary = "Upload an image for a side quest submission.",
        description = "Returns a key to reference from the create-side-quest request. " +
            "Uploads nothing references are discarded after a day."
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        @RequestParam("file") file: MultipartFile,
        request: HttpServletRequest,
    ): ImageUploadResponse {
        rateLimiter.check(request.remoteAddr ?: "unknown")

        if (file.isEmpty) throw BadRequestException("No file was uploaded.")

        return ImageUploadResponse(storageKey = imageService.upload(file.bytes).toString())
    }

    @Operation(summary = "Serve a side quest image.")
    @GetMapping("/{imageId}")
    fun get(@PathVariable imageId: Long): ResponseEntity<ByteArray> {
        val image = imageService.load(imageId)

        return ResponseEntity.ok()
            // An image row's bytes are never rewritten, so this is safe to keep forever.
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
            .eTag(imageId.toString())
            .contentType(MediaType.parseMediaType(image.contentType))
            .contentLength(image.bytes.size.toLong())
            .body(image.bytes)
    }
}

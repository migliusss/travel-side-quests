package com.example.travelsidequests.service

import com.example.travelsidequests.exceptionhandler.BadRequestException
import org.springframework.stereotype.Component
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Decodes and re-encodes every upload. Two things fall out of that:
 * the format is proven by actually reading the bytes rather than trusting the client's
 * Content-Type header, and the EXIF block is dropped — which matters, because holiday
 * photos routinely carry the GPS coordinates of wherever they were taken.
 */
@Component
class ImageProcessor {

    fun normalize(bytes: ByteArray): NormalizedImage {
        if (bytes.isEmpty()) throw BadRequestException("The uploaded file is empty.")

        val format = detectFormat(bytes)
            ?: throw BadRequestException("Only JPEG and PNG images are accepted.")

        val decoded = ImageIO.read(ByteArrayInputStream(bytes))
            ?: throw BadRequestException("That file could not be read as an image.")

        val resized = downscale(decoded, format)

        val out = ByteArrayOutputStream()
        if (!ImageIO.write(resized, format, out)) {
            throw BadRequestException("That image could not be processed.")
        }

        return NormalizedImage(bytes = out.toByteArray(), contentType = "image/$format")
    }

    private fun detectFormat(bytes: ByteArray): String? {
        ImageIO.createImageInputStream(ByteArrayInputStream(bytes)).use { input ->
            val reader = ImageIO.getImageReaders(input).asSequence().firstOrNull() ?: return null
            val name = reader.formatName.lowercase()

            return if (name in ACCEPTED_FORMATS) name else null
        }
    }

    private fun downscale(source: BufferedImage, format: String): BufferedImage {
        val longestSide = maxOf(source.width, source.height)
        val scale = if (longestSide > MAX_DIMENSION) MAX_DIMENSION.toDouble() / longestSide else 1.0

        val width = (source.width * scale).toInt().coerceAtLeast(1)
        val height = (source.height * scale).toInt().coerceAtLeast(1)

        // JPEG cannot carry an alpha channel, so flatten rather than fail on write.
        val type = if (format == "jpeg") BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
        val target = BufferedImage(width, height, type)

        target.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            drawImage(source, 0, 0, width, height, null)
            dispose()
        }

        return target
    }

    companion object {
        private val ACCEPTED_FORMATS = setOf("jpeg", "png")
        private const val MAX_DIMENSION = 2560
    }
}

class NormalizedImage(
    val bytes: ByteArray,
    val contentType: String,
)

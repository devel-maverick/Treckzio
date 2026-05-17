package com.weathersnap.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

// Compresses captured photos and returns original + compressed sizes
@Singleton
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val io: CoroutineDispatcher
) {

    data class Result(
        val outputFile: File,
        val originalBytes: Long,
        val compressedBytes: Long
    )

    suspend fun compress(
        source: File,
        maxDimension: Int = 1280,
        quality: Int = 70
    ): Result = withContext(io) {
        val originalBytes = source.length()

        // Decode with sample size to save memory
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.absolutePath, boundsOpts)
        val longest = max(boundsOpts.outWidth, boundsOpts.outHeight).coerceAtLeast(1)

        var sampleSize = 1
        while (longest / sampleSize > maxDimension * 2) sampleSize *= 2

        val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        var bitmap = BitmapFactory.decodeFile(source.absolutePath, decodeOpts)
            ?: error("Could not decode image")

        // Scale down if still too large
        val w = bitmap.width
        val h = bitmap.height
        val big = max(w, h)
        if (big > maxDimension) {
            val scale = maxDimension.toFloat() / big
            val newW = (w * scale).toInt().coerceAtLeast(1)
            val newH = (h * scale).toInt().coerceAtLeast(1)
            val scaled = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
            if (scaled !== bitmap) bitmap.recycle()
            bitmap = scaled
        }

        val outDir = File(context.filesDir, "reports").apply { mkdirs() }
        val outFile = File(outDir, "report_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        bitmap.recycle()

        Result(
            outputFile = outFile,
            originalBytes = originalBytes,
            compressedBytes = outFile.length()
        )
    }
}

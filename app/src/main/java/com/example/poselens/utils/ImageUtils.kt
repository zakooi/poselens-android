package com.example.poselens.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * Image utility functions
 */
object ImageUtils {
    
    /**
     * Load bitmap from URI with proper orientation
     */
    suspend fun loadBitmapFromUri(
        context: Context,
        uri: Uri,
        maxSize: Int = Constants.MAX_IMAGE_SIZE
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Decode with inJustDecodeBounds to get dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize)
                options.inJustDecodeBounds = false
                
                // Reopen stream and decode bitmap
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream, null, options)
                    bitmap?.let { rotateBitmapIfNeeded(context, uri, it) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Calculate sample size for bitmap decoding
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Rotate bitmap based on EXIF orientation
     */
    private fun rotateBitmapIfNeeded(
        context: Context,
        uri: Uri,
        bitmap: Bitmap
    ): Bitmap {
        val orientation = getExifOrientation(context, uri)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }
    
    /**
     * Get EXIF orientation from image URI
     */
    private fun getExifOrientation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: IOException) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }
    
    /**
     * Rotate bitmap by degrees
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
    
    /**
     * Compress bitmap to JPEG byte array
     */
    fun compressBitmap(
        bitmap: Bitmap,
        quality: Int = Constants.IMAGE_QUALITY
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
    
    /**
     * Save bitmap to file
     */
    suspend fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
        quality: Int = Constants.IMAGE_QUALITY
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            file.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Create thumbnail from bitmap
     */
    fun createThumbnail(
        bitmap: Bitmap,
        size: Int = Constants.THUMBNAIL_SIZE
    ): Bitmap {
        val ratio = minOf(
            size.toFloat() / bitmap.width,
            size.toFloat() / bitmap.height
        )
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    
    /**
     * Convert bitmap to base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val bytes = compressBitmap(bitmap)
        return android.util.Base64.encodeToString(
            bytes,
            android.util.Base64.NO_WRAP
        )
    }
    
    /**
     * Get image dimensions without loading full bitmap
     */
    fun getImageDimensions(context: Context, uri: Uri): Pair<Int, Int>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                Pair(options.outWidth, options.outHeight)
            }
        } catch (e: IOException) {
            null
        }
    }
}

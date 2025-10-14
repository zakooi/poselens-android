package com.example.poselens.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.poselens.data.ml.ImageAnalyzer
import com.example.poselens.data.remote.ApiService
import com.example.poselens.data.remote.dto.AnalyzeImageRequest
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.model.SceneAnalysisResult
import com.example.poselens.domain.repository.ImageRepository
import com.example.poselens.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageAnalyzer: ImageAnalyzer,
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher
) : ImageRepository {
    
    override suspend fun analyzeImage(imageUri: Uri): Flow<Result<SceneAnalysisResult>> = flow {
        emit(Result.Loading)
        
        try {
            val bitmap = ImageUtils.loadBitmapFromUri(context, imageUri)
                ?: throw Exception("Failed to load image")
            
            val result = imageAnalyzer.analyzeScene(bitmap)
            emit(result)
            
            bitmap.recycle()
        } catch (e: Exception) {
            emit(Result.Error(e, "Failed to analyze image: ${e.message}"))
        }
    }.flowOn(ioDispatcher)
    
    override suspend fun analyzeBitmap(bitmap: Bitmap): Result<SceneAnalysisResult> {
        return withContext(ioDispatcher) {
            imageAnalyzer.analyzeScene(bitmap)
        }
    }
    
    override suspend fun saveImageToGallery(bitmap: Bitmap, filename: String): Result<Uri> {
        return withContext(ioDispatcher) {
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PoseLens")
                }
                
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw Exception("Failed to create MediaStore entry")
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                } ?: throw Exception("Failed to open output stream")
                
                Result.Success(uri)
            } catch (e: Exception) {
                Result.Error(e, "Failed to save image: ${e.message}")
            }
        }
    }
    
    override suspend fun applyImageAdjustments(
        bitmap: Bitmap,
        brightness: Float,
        contrast: Float,
        saturation: Float,
        exposure: Float,
        highlights: Float,
        shadows: Float,
        temperature: Float,
        tint: Float,
        sharpness: Float,
        vignette: Float,
        grain: Float
    ): Result<Bitmap> {
        return withContext(ioDispatcher) {
            try {
                // Simple brightness/contrast adjustment
                // In production, use advanced image processing library
                val adjustedBitmap = bitmap.copy(bitmap.config, true)
                
                // Apply basic adjustments
                val pixels = IntArray(adjustedBitmap.width * adjustedBitmap.height)
                adjustedBitmap.getPixels(pixels, 0, adjustedBitmap.width, 0, 0, adjustedBitmap.width, adjustedBitmap.height)
                
                for (i in pixels.indices) {
                    val pixel = pixels[i]
                    var r = ((pixel shr 16) and 0xff) / 255f
                    var g = ((pixel shr 8) and 0xff) / 255f
                    var b = (pixel and 0xff) / 255f
                    
                    // Apply brightness
                    r = (r + brightness / 100f).coerceIn(0f, 1f)
                    g = (g + brightness / 100f).coerceIn(0f, 1f)
                    b = (b + brightness / 100f).coerceIn(0f, 1f)
                    
                    // Apply contrast
                    r = ((r - 0.5f) * contrast + 0.5f).coerceIn(0f, 1f)
                    g = ((g - 0.5f) * contrast + 0.5f).coerceIn(0f, 1f)
                    b = ((b - 0.5f) * contrast + 0.5f).coerceIn(0f, 1f)
                    
                    pixels[i] = (0xff shl 24) or ((r * 255).toInt() shl 16) or ((g * 255).toInt() shl 8) or (b * 255).toInt()
                }
                
                adjustedBitmap.setPixels(pixels, 0, adjustedBitmap.width, 0, 0, adjustedBitmap.width, adjustedBitmap.height)
                
                Result.Success(adjustedBitmap)
            } catch (e: Exception) {
                Result.Error(e, "Failed to apply adjustments: ${e.message}")
            }
        }
    }
    
    override suspend fun getThumbnail(imageUri: Uri, size: Int): Result<Bitmap> {
        return withContext(ioDispatcher) {
            try {
                val thumbnail = ImageUtils.createThumbnail(context, imageUri, size)
                    ?: throw Exception("Failed to create thumbnail")
                Result.Success(thumbnail)
            } catch (e: Exception) {
                Result.Error(e, "Failed to create thumbnail: ${e.message}")
            }
        }
    }
    
    override suspend fun uploadImageForAnalysis(imageUri: Uri): Result<String> {
        return withContext(ioDispatcher) {
            try {
                val bitmap = ImageUtils.loadBitmapFromUri(context, imageUri)
                    ?: throw Exception("Failed to load image")
                
                val base64 = ImageUtils.bitmapToBase64(bitmap)
                bitmap.recycle()
                
                val request = AnalyzeImageRequest(
                    imageBase64 = base64,
                    includePose = true,
                    includeScene = true
                )
                
                val response = apiService.analyzeImage(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val analysisId = response.body()?.data?.analysisId
                        ?: throw Exception("No analysis ID returned")
                    Result.Success(analysisId)
                } else {
                    Result.Error(
                        Exception("API error: ${response.code()}"),
                        response.body()?.error?.message ?: "Unknown error"
                    )
                }
            } catch (e: Exception) {
                Result.Error(e, "Failed to upload image: ${e.message}")
            }
        }
    }
}

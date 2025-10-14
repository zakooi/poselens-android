package com.example.poselens.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.PoseResult
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.PoseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Use case for detecting pose from an image
 */
class DetectPoseUseCase @Inject constructor(
    private val poseRepository: PoseRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Detect pose from image URI
     * @param imageUri URI of the image
     * @return Flow emitting Result with PoseResult
     */
    suspend operator fun invoke(imageUri: Uri): Flow<Result<PoseResult>> {
        return poseRepository.detectPose(imageUri)
            .flowOn(dispatcher)
    }
    
    /**
     * Detect pose from Bitmap
     * @param bitmap Bitmap to analyze
     * @return Result with PoseResult
     */
    suspend fun detect(bitmap: Bitmap): Result<PoseResult> {
        return poseRepository.detectPoseBitmap(bitmap)
    }
    
    /**
     * Track pose in real-time for camera preview
     * @param bitmap Current camera frame
     * @return Flow emitting pose updates
     */
    fun trackRealtime(bitmap: Bitmap): Flow<Result<PoseResult>> {
        return poseRepository.trackPoseRealtime(bitmap)
            .flowOn(dispatcher)
    }
}

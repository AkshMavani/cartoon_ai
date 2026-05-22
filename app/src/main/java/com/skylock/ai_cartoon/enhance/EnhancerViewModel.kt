package com.skylock.ai_cartoon.enhance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class EnhancerViewModel(private val feature: String) : ViewModel() {

    companion object {
        private const val TAG = "EnhancerViewModel"
    }

    private val apiConfig = ProcessTypeConfig.getConfig(feature)
    private val repository = apiConfig?.let { EnhancerRepository(it.baseUrl) }

    private val _resultUrl = MutableLiveData<String?>()
    val resultUrl: LiveData<String?> = _resultUrl

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _enhancementStage = MutableLiveData(EnhancementStage.IDLE)
    val enhancementStage: LiveData<EnhancementStage> = _enhancementStage

    private var progressJob: Job? = null
    private var isProcessingComplete = false

    init {
        Log.d(TAG, "ViewModel created for feature: $feature")
        if (apiConfig == null) {
            Log.e(TAG, "No API config found for feature: $feature")
        } else {
            Log.d(TAG, "API config - baseUrl: ${apiConfig.baseUrl}, apiType: ${apiConfig.apiType}")
        }
    }

    fun processPhoto(file: File, sign: String = "") {
        Log.d(
            TAG,
            "processPhoto called with file: ${file.absolutePath}, exists: ${file.exists()}, size: ${file.length()}"
        )

        if (repository == null || apiConfig == null) {
            val error = "Unsupported feature: $feature"
            Log.e(TAG, error)
            _errorMessage.value = error
            return
        }

        viewModelScope.launch {
            try {
                updateStage(EnhancementStage.PROCESSING, "Processing ${apiConfig.apiType}...")
                startAdaptiveProgressAnimation(1, 100)

                Log.d(TAG, "Calling repository.processPhoto with type: ${apiConfig.apiType}")
                val response = repository.processPhoto(file, sign, apiConfig.apiType)
                Log.d(
                    TAG,
                    "processPhoto response - code: ${response.code}, msg: ${response.msg}, images: ${response.images?.size}"
                )

                response.images?.firstOrNull()?.let { image ->
                    Log.d(
                        TAG,
                        "First image - id: ${image.id}, name: ${image.name}, size: ${image.size.width}x${image.size.height}"
                    )
                    pollForResult(image)
                } ?: run {
                    Log.e(TAG, "No image in response")
                    onError("No image in response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in processPhoto: ${e.message}", e)
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun pollForResult(image: EnhancerPhotoResponse) {
        Log.d(TAG, "Starting polling for image id: ${image.id}, name: ${image.name}")
        val maxTries = 50
        var attempt = 1

        repeat(maxTries) {
            Log.d(TAG, "Polling attempt $attempt of $maxTries")
            delay(3000)
            try {
                val response = repository!!.getImages(
                    image.id,
                    image.name,
                    image.size.width,
                    image.size.height
                )
                Log.d(
                    TAG,
                    "getImages response - code: ${response.code}, msg: ${response.msg}, images: ${response.images?.size}"
                )

                val resultUrl = response.images?.firstOrNull()?.url
                if (!resultUrl.isNullOrEmpty()) {
                    Log.d(TAG, "Polling succeeded! Result URL: $resultUrl")
                    smoothTransitionToFinal(100)
                    _resultUrl.postValue(resultUrl)
                    updateStage(EnhancementStage.COMPLETED, "Completed!")
                    return
                } else {
                    Log.d(
                        TAG,
                        "Polling attempt $attempt: result URL is null or empty, still processing..."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Polling attempt $attempt failed with exception: ${e.message}", e)
            }
            attempt++
        }

        Log.e(TAG, "Polling timed out after $maxTries attempts")
        onError("Timeout: Processing failed")
    }

    private fun updateStage(stage: EnhancementStage, message: String) {
        Log.d(TAG, "updateStage: $stage, message: $message")
        _enhancementStage.postValue(stage)
    }

    private fun onError(message: String) {
        Log.e(TAG, "onError: $message")
        stopProgressAnimation()
        _errorMessage.postValue(message)
        updateStage(EnhancementStage.ERROR, message)
    }

    private fun startAdaptiveProgressAnimation(startPercent: Int, targetPercent: Int) {
        Log.d(TAG, "startAdaptiveProgressAnimation: $startPercent -> $targetPercent")
        progressJob?.cancel()
        isProcessingComplete = false
        _progress.postValue(startPercent)

        progressJob = viewModelScope.launch {
            var currentProgress = startPercent
            while (currentProgress < targetPercent && !isProcessingComplete) {
                delay(1000)
                val progressRatio =
                    (currentProgress - startPercent).toFloat() / (targetPercent - startPercent)
                val increment = when {
                    progressRatio < 0.3f -> 2
                    progressRatio < 0.8f -> 1
                    else -> if ((currentProgress + 1) % 3 == 0) 1 else 0
                }
                currentProgress = (currentProgress + increment).coerceAtMost(targetPercent - 5)
                _progress.postValue(currentProgress)
                if (currentProgress % 10 == 0) {
                    Log.d(TAG, "Progress: $currentProgress%")
                }
            }
        }
    }

    private suspend fun smoothTransitionToFinal(targetPercent: Int) {
        Log.d(TAG, "smoothTransitionToFinal: target $targetPercent%")
        isProcessingComplete = true
        progressJob?.cancel()
        val currentProgress = _progress.value ?: 0
        if (currentProgress < targetPercent) {
            val remaining = targetPercent - currentProgress
            val steps = remaining.coerceAtMost(3)
            val increment = remaining / steps
            Log.d(
                TAG,
                "Smooth transition: remaining=$remaining, steps=$steps, increment=$increment"
            )
            repeat(steps) {
                delay(500)
                val newProgress = (_progress.value ?: 0) + increment
                _progress.postValue(newProgress)
                Log.d(TAG, "Smooth progress: $newProgress%")
            }
        }
        _progress.postValue(targetPercent)
        Log.d(TAG, "Final progress: $targetPercent%")
    }

    private fun stopProgressAnimation() {
        Log.d(TAG, "stopProgressAnimation called")
        isProcessingComplete = true
        progressJob?.cancel()
    }

    override fun onCleared() {
        Log.d(TAG, "ViewModel cleared")
        super.onCleared()
        stopProgressAnimation()
    }

    enum class EnhancementStage {
        IDLE, PROCESSING, COMPLETED, ERROR
    }
}
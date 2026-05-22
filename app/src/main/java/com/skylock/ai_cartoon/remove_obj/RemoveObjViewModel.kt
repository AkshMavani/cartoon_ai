package com.skylock.ai_cartoon.remove_obj

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skylock.ai_cartoon.base.MyApp
import com.skylock.ai_cartoon.model.AiphotoResponse
import com.skylock.ai_cartoon.model.ImageResponse
import com.skylock.ai_cartoon.util.AiphotoService
import com.skylock.ai_cartoon.util.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URLDecoder

class RemoveObjViewModel : BaseViewModel() {

    // ─── LiveData ────────────────────────────────────────────────────────────

    private val _imageResponse = MutableLiveData<ImageResponse?>()
    val imageResponse: LiveData<ImageResponse?> = _imageResponse

    private val _aiphotoResponse = MutableLiveData<List<AiphotoResponse>?>()
    val aiphotoResponse: MutableLiveData<List<AiphotoResponse>?> = _aiphotoResponse

    private val _maskRemoveResponse = MutableLiveData<MaskRemoveResponse>()
    val maskRemoveResponse: MutableLiveData<MaskRemoveResponse> = _maskRemoveResponse

    // ✅ FIX: Expose errors to the UI so users see a message instead of a silent hang
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ─── Config ───────────────────────────────────────────────────────────────

    private var isAuto: Boolean = false

    // ✅ FIX: Max retry cap — was commented out, causing infinite recursion on failure
    private val MAX_RETRIES = 3

    // ✅ SECURITY NOTE: Move this token to BuildConfig, encrypted SharedPreferences,
    //    or fetch it from a secure backend. Never hardcode auth tokens in source.
    private val API_TOKEN = "NU4IYAS4D0F8CVBSI26R5NU21E0HW737GPJ07WAM"

    companion object {
        private const val TAG = "RemoveObjViewModel"
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    fun setAuto(auto: Boolean) {
        this.isAuto = auto
    }

    fun onRemoveObj(uri: String, urlImage: String?, maskUri: String?, masks: String) {
        Log.d(TAG, "onRemoveObj uri=$uri urlImage=$urlImage maskUri=$maskUri masks=$masks")

        if (maskUri == null) {
            Log.e(TAG, "maskUri is null — aborting")
            return
        }

        _aiphotoResponse.value = null
        _imageResponse.value = null
        _errorMessage.value = null

        val maskFile = if (!isAuto) getFileFromUri(maskUri) else null

        val maskPart = maskFile?.let {
            MultipartBody.Part.createFormData(
                "mask", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        val urlRequestBody = urlImage?.toRequestBody("text/plain".toMediaTypeOrNull())

        var filePart: MultipartBody.Part? = null
        if (urlImage == null) {
            val fileFromUri = getFileFromUri(uri)
            if (fileFromUri == null) {
                Log.e(TAG, "File not found for uri: $uri")
                _errorMessage.postValue("Could not read image file. Please try again.")
                return
            }
            filePart = MultipartBody.Part.createFormData(
                "file", fileFromUri.name, fileFromUri.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        val masksRequestBody = masks.toRequestBody("text/plain".toMediaTypeOrNull())
        val typeRequestBody = "removeobj".toRequestBody("text/plain".toMediaTypeOrNull())

        onProcess(
            filePart,
            urlRequestBody,
            maskPart,
            masksRequestBody,
            typeRequestBody,
            maskFile,
            0
        )
    }

    fun getMaskRemove(uri: String) {
        if (_maskRemoveResponse.value != null) return
        Log.d(TAG, "getMaskRemove: $uri")

        val maskFile = getFileFromUri(uri) ?: run {
            Log.e(TAG, "File not found for mask URI: $uri")
            _maskRemoveResponse.postValue(MaskRemoveResponse(emptyList(), 99))
            return
        }

        val filePart = MultipartBody.Part.createFormData(
            "file", maskFile.name, maskFile.asRequestBody("image/*".toMediaTypeOrNull())
        )

        if (API_TOKEN.isEmpty()) {
            _maskRemoveResponse.postValue(MaskRemoveResponse(emptyList(), 99))
            return
        }

        AiphotoService.getService("removeobj")
            .getMaskRemove(filePart, null, "Bearer $API_TOKEN")
            .enqueue(object : Callback<MaskRemoveResponse> {
                override fun onResponse(
                    call: Call<MaskRemoveResponse>,
                    response: Response<MaskRemoveResponse>
                ) {
                    Log.d(TAG, "getMaskRemove HTTP ${response.code()}")

                    // ✅ FIX: Log the error body when the call "succeeds" at HTTP level
                    //    but returns a non-2xx code — this is a common silent failure case
                    if (!response.isSuccessful) {
                        val errBody = response.errorBody()?.string()
                        Log.e(TAG, "getMaskRemove error body: $errBody")
                        _maskRemoveResponse.postValue(
                            MaskRemoveResponse(
                                emptyList(),
                                response.code()
                            )
                        )
                        return
                    }

                    response.body()?.let { body ->
                        Log.d(TAG, "getMaskRemove masks: ${body.masks}")
                        body.masks?.forEach { mask -> mask.id = View.generateViewId() }
                        _maskRemoveResponse.postValue(body)
                    } ?: run {
                        Log.e(TAG, "getMaskRemove: null body with code ${response.code()}")
                        _maskRemoveResponse.postValue(MaskRemoveResponse(emptyList(), 99))
                    }
                }

                override fun onFailure(call: Call<MaskRemoveResponse>, t: Throwable) {
                    Log.e(TAG, "getMaskRemove failed: ${t.message}", t)
                    _maskRemoveResponse.postValue(MaskRemoveResponse(emptyList(), 99))
                    _errorMessage.postValue("Network error: ${t.message}")
                }
            })
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /**
     * ✅ FIX: Retry is now capped at MAX_RETRIES (3).
     *    Previously MAX_RETRIES check was commented out → infinite recursion
     *    on any persistent network error, causing ANR / OOM crash.
     *
     * ✅ FIX: Each retry creates a fresh API call via processPhoto().
     *    Retrofit Call objects are single-use; retrying the same Call always fails.
     */
    private fun onProcess(
        filePart: MultipartBody.Part?,
        url: RequestBody?,
        maskPart: MultipartBody.Part?,
        listMask: RequestBody,
        type: RequestBody,
        maskFile: File?,
        retries: Int
    ) {
        if (retries >= MAX_RETRIES) {
            Log.e(TAG, "Max retries ($MAX_RETRIES) reached — giving up")
            maskFile?.delete()
            _imageResponse.postValue(null)
            _errorMessage.postValue("Request failed after $MAX_RETRIES attempts. Please check your connection.")
            return
        }

        if (retries > 0) {
            Log.w(TAG, "Retry attempt $retries of $MAX_RETRIES")
        }

        // ✅ FIX: processPhoto() builds a NEW Call each time — required for retries
        processPhoto(
            filePart, url, maskPart, listMask, type,
            object : Callback<AiphotoResponse> {
                override fun onResponse(
                    call: Call<AiphotoResponse>,
                    response: Response<AiphotoResponse>
                ) {
                    Log.d(TAG, "processPhoto HTTP ${response.code()}")

                    // ✅ FIX: Check HTTP success status — Retrofit treats 4xx/5xx as
                    //    "successful" responses (onResponse is called), but body() is null.
                    //    This was a hidden failure: response arrived but was silently dropped.
                    if (!response.isSuccessful) {
                        val errBody = response.errorBody()?.string()
                        Log.e(TAG, "processPhoto HTTP error ${response.code()}: $errBody")
                        maskFile?.delete()
                        _errorMessage.postValue("Server error (${response.code()}). Please try again.")
                        return
                    }

                    maskFile?.delete()
                    val body = response.body()

                    if (body == null) {
                        Log.e(TAG, "processPhoto: response body is null")
                        _errorMessage.postValue("Empty response from server.")
                        return
                    }

                    Log.d(TAG, "processPhoto images: ${body.images}")
                    val images = body.images

                    if (!images.isNullOrEmpty() && images[0].url != null) {
                        _aiphotoResponse.postValue(listOf(body))
                        _imageResponse.postValue(images[0])
                    } else {
                        Log.e(TAG, "processPhoto: images list empty or url null")
                        _errorMessage.postValue("No result image returned. Please try again.")
                    }
                }

                override fun onFailure(call: Call<AiphotoResponse>, t: Throwable) {
                    Log.e(TAG, "processPhoto attempt ${retries + 1} failed: ${t.message}", t)
                    // ✅ FIX: Recurse with incremented retry count (capped above)
                    onProcess(filePart, url, maskPart, listMask, type, maskFile, retries + 1)
                }
            }
        )
    }

    private fun processPhoto(
        filePart: MultipartBody.Part?,
        url: RequestBody?,
        maskPart: MultipartBody.Part?,
        masks: RequestBody,
        type: RequestBody,
        callback: Callback<AiphotoResponse>
    ) {
        Log.d(TAG, "processPhoto filePart=$filePart url=$url maskPart=$maskPart")

        if (API_TOKEN.isEmpty()) {
            _imageResponse.postValue(null)
            _errorMessage.postValue("Missing API token.")
            return
        }

        val platform = "android".toRequestBody("text/plain".toMediaTypeOrNull())

        // ✅ Each call to processPhoto() creates a fresh Retrofit Call — correct for retries
        AiphotoService.getService("removeobj").processPhoto(
            "Bearer $API_TOKEN",
            filePart, url, maskPart, masks,
            null, type, null, 0, 1, platform
        ).enqueue(callback)
    }

    private fun getFileFromUri(uri: String): File? {
        Log.d(TAG, "getFileFromUri: $uri")
        val context = MyApp.getInstance() ?: return null

        return when {
            uri.startsWith("file://") -> {
                val realPath = URLDecoder.decode(uri.replace("file://", ""), "UTF-8")
                File(realPath).takeIf { it.exists() }
            }

            uri.startsWith("content://") -> {
                Constants.convertContentToFile(context, uri)
            }

            else -> {
                File(uri).takeIf { it.exists() }
            }
        }
    }
}
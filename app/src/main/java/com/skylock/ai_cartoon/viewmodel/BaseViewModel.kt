package com.skylock.ai_cartoon.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skylock.ai_cartoon.base.BaseActivity
import com.skylock.ai_cartoon.util.ActivityManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel(), DefaultLifecycleObserver, CoroutineScope {

    private var onError: ((String?) -> Unit)? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    private val viewModelJob = SupervisorJob()

    private val handler = CoroutineExceptionHandler { _, exception ->
        handleError(exception, exception.message)
    }

    override val coroutineContext = viewModelJob + kotlinx.coroutines.Dispatchers.Main


    protected fun launchHandler(
        onError: ((String?) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        this.onError = onError
        viewModelScope.launch(handler) { block() }
    }

    fun <T> subscribe(
        flow: Flow<T>,
        showLoading: Boolean,
        keepLoading: Boolean,
        onNext: (T) -> Unit
    ) {
        flow
            .onStart { if (showLoading) showLoading() }
            .onEach { value ->
                if (!keepLoading) hideLoading()
                onNext(value)
            }
            .catch { throwable ->
                hideLoading()
                handleError(throwable, throwable.message)
            }
            .launchIn(viewModelScope)
    }

    private fun handleError(throwable: Throwable?, error: String?) {
        throwable?.printStackTrace()
        onError?.invoke(throwable?.message ?: error)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel(CancellationException())
    }

    protected fun showLoading() = isLoading.postValue(true)
    protected fun hideLoading() = isLoading.postValue(false)
}
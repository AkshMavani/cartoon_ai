package com.skylock.ai_cartoon.remove_obj

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), DefaultLifecycleObserver, CoroutineScope {

    private var onError: ((String?) -> Unit)? = null

    // Correctly lazy-initialized isLoading LiveData
    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private val viewModelJob = SupervisorJob()


    // Coroutine Scope Implementation
    override val coroutineContext: CoroutineContext
        get() = viewModelJob + Dispatchers.Main

    // Exception Handler to catch coroutine failures
    private val handler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable, null)
    }


    /**
     * Launch a coroutine with a custom error handler
     */
    protected fun launchHandler(
        onError: ((String?) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        this.onError = onError
        viewModelScope.launch(handler) {
            block()
        }
    }

    /**
     * Wraps a value in a flow and shifts execution to the IO dispatcher
     */


    /**
     * Subscribes to a flow with built-in loading and error handling
     */
    fun <T> subscribe(
        flow: Flow<T>,
        onLoading: Boolean = true,
        keepLoading: Boolean = false,
        onNext: (T) -> Unit
    ) {
        flow.onStart {
            if (onLoading) showLoading()
        }.onEach { value ->
            if (!keepLoading) hideLoading()
            onNext(value)
        }.catch { cause ->
            hideLoading()
            handleError(cause, null)
        }.launchIn(viewModelScope)
    }

    private fun handleError(throwable: Throwable?, error: String?) {
        throwable?.printStackTrace()
        onError?.invoke(throwable?.message ?: error)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        viewModelScope.cancel()
    }

    // Solved synthetic classes BaseViewModel$showLoading$1 and BaseViewModel$hideLoading$1
    protected fun showLoading() {
        launch(Dispatchers.Main) {
            isLoading.value = true
        }
    }

    protected fun hideLoading() {
        launch(Dispatchers.Main) {
            isLoading.value = false
        }
    }
}
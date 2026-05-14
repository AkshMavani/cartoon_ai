package com.skylock.ai_cartoon.base

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.util.SharePrefUtils
import com.skylock.ai_cartoon.network.NetworkChangeReceiver
import com.skylock.ai_cartoon.util.ActivityManager
import java.util.Locale

// Generic T extends ViewBinding to allow child activities to use getBinding()
open abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private var _binding: T? = null
    val binding: T get() = _binding!!

    protected open fun isFullScreen(): Boolean = false

    // Abstract methods to be implemented by children
    abstract fun inflateBinding(inflater: LayoutInflater): T
    open fun initView(savedInstanceState: Bundle?) {}
    open fun initListener() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding(layoutInflater)
        setContentView(binding.root)

        ActivityManager.addActivity(this)
        initScreen()

        if (isFullScreen()) {
            window.setFlags(512, 512)
            if (Build.VERSION.SDK_INT <= 29) {
                window.statusBarColor = 0
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = 1280
            } else {
                window.statusBarColor = 0
            }
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 8704
            enableImmersiveMode(window)
        }

        networkChangeReceiver = NetworkChangeReceiver(this)
        window.navigationBarColor = Color.parseColor("#111111")
        @Suppress("DEPRECATION")
        registerReceiver(networkChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

        initView(savedInstanceState)
        initListener()
    }

    // Accessor for children to match your requested style

    private fun initScreen() {
        if (isFullScreen()) window.setFlags(512, 512)
        fullScreen()
        window.decorView.setOnSystemUiVisibilityChangeListener { fullScreen() }
    }

    private fun fullScreen() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = if (isFullScreen()) 5894 else 5380
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_right)
    }

    override fun onDestroy() {
        ActivityManager.removeActivity(this)
        super.onDestroy()
        networkChangeReceiver?.let { unregisterReceiver(it) }
        _binding = null
    }

    // Implements localization logic
    override fun attachBaseContext(context: Context) {
        val language = SharePrefUtils.getString("Language").let {
            if (it == "sys") Locale.getDefault().language else it
        }
        if (language.isNullOrEmpty()) {
            super.attachBaseContext(context)
            return
        }
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(context.createConfigurationContext(config))
    }

    fun enableImmersiveMode(window: Window) {
        if (Build.VERSION.SDK_INT >= 30) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.getDecorView().setSystemUiVisibility(4358)
        }
    }
}
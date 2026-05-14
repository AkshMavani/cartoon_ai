package com.skylock.ai_cartoon.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.base.BaseActivity
import com.skylock.ai_cartoon.databinding.ActivityCartoonIntroBinding

class CartoonIntroActivity : BaseActivity<ActivityCartoonIntroBinding>() {

    private var selectedStyle: String? = null
    private var featureName: String? = null
    override fun inflateBinding(inflater: LayoutInflater): ActivityCartoonIntroBinding {
        return ActivityCartoonIntroBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        // Receive data from the Intent
        selectedStyle = intent.getStringExtra("style")
        featureName = intent.getStringExtra("feature")

        // Setup Video (Removed Ads/Premium logic)
        binding.video.setRawData(R.raw.video_intro_cartoon)
        binding.video.setLooping(true)
        binding.video.play()
    }

    override fun initListener() {
        super.initListener()

        // Button to open the Gallery/Library
        binding.btnSelectPhoto.setOnClickListener {
            val intent = Intent(this, LibraryVer2Activity::class.java).apply {
                putExtra("feature", featureName)
                putExtra("style", selectedStyle)
            }
            startActivity(intent)
           // ActivityExtKt.applyTransition(this, R.anim.slide_in_right, R.anim.slide_nothing)
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
    }
}
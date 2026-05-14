package com.skylock.ai_cartoon.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skylock.ai_cartoon.R

class AIAvatarResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ai_avatar_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*  val intent = intent
       val imageBefore = intent.getStringExtra("image_before") ?: ""
        imageAfter = intent.getStringExtra("image_after") ?: ""
        aiFaceUri = intent.getStringExtra("mask")
        feature = intent.getStringExtra(HomeActivity.FEATURE) ?: "aiavatarresult"
        imageWidth = intent.getIntExtra("image_width", 0)
        imageHeight = intent.getIntExtra("image_height", 0)
        style = intent.getStringExtra("style") ?: ""
    }*/
    }
}
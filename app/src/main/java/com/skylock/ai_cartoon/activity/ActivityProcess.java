package com.skylock.ai_cartoon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.skylock.ai_cartoon.R;

import java.io.File;

public class ActivityProcess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_process);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();

        String imagePath = intent.getStringExtra("image_before");
        String carttonurl = intent.getStringExtra("cartoonUrl");
        int width = intent.getIntExtra("image_width", 0);
        int height = intent.getIntExtra("image_height", 0);
        String style = intent.getStringExtra("style");
        String gender = intent.getStringExtra("gender");
        String feature = intent.getStringExtra("feature");
        Boolean iscartton = intent.getBooleanExtra("isfromCartton",false);

        // Debug log
        Log.d("PROCESS_DATA", "Path: " + imagePath);
        Log.d("PROCESS_DATA", "Size: " + width + "x" + height);
        Log.d("PROCESS_DATA", "Style: " + style);
        Log.d("PROCESS_DATA", "Gender: " + gender);
        Log.d("PROCESS_DATA", "Feature: " + feature);

        // Use data
        if (!iscartton) {
            setupUI(imagePath, style, feature);
        }else{
            ImageView imageView = findViewById(R.id.imgAfter);
            Glide.with(this)
                    .load(carttonurl)
                    .into(imageView);
        }
    }
    private void setupUI(String imagePath, String style, String feature) {
        ImageView imageView = findViewById(R.id.imgAfter);
//        TextView tvStyle = findViewById(R.id.tvStyle);
//        TextView tvFeature = findViewById(R.id.tvFeature);

        Glide.with(this)
                .load(new File(imagePath))
                .into(imageView);

//        tvStyle.setText(style);
//        tvFeature.setText(feature);
    }
}
package com.talhaatif.tickojet

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the TextView
        val helloText = findViewById<TextView>(R.id.tv_username)

        // Set initial transparency and position
        helloText.alpha = 0f
        helloText.translationX = -100f // Start off-screen to the left

        // Animate the TextView
        helloText.animate()
            .alpha(1f) // Fade in
            .translationX(0f) // Slide to the original position
            .setDuration(1000) // 1 second
            .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator()) // Smooth easing
            .start()

        // Animate Profile Image
        val profileImage = findViewById<CircleImageView>(R.id.profile_image)
        profileImage.scaleX = 0f
        profileImage.scaleY = 0f
        profileImage.rotation = 0f
        profileImage.animate()
            .scaleX(1f)
            .scaleY(1f)
            .rotation(360f)
            .setDuration(1000)
            .setInterpolator(OvershootInterpolator())
            .start()

        // Customize Profile Image Border
        profileImage.borderColor = ContextCompat.getColor(this, R.color.theme_primary)
        profileImage.borderWidth = 4


    }
}
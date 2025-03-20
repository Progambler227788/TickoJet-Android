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


        // Animate Profile Image
        val profileImage = findViewById<CircleImageView>(R.id.profile_image)
        // Set initial scale
        profileImage.scaleX = 1f
        profileImage.scaleY = 1f

        // Create the pop-out animation
        profileImage.animate()
            .scaleX(1.5f) // Expand to 120% size
            .scaleY(1.5f)
            .setDuration(600) // 600ms for expansion
            .withEndAction {
                // Shrink back to original size
                profileImage.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600) // 600ms for shrinking
                    .start()
            }
            .start()

        // Customize Profile Image Border
        profileImage.borderColor = ContextCompat.getColor(this, R.color.white)
        profileImage.borderWidth = 4


    }
}
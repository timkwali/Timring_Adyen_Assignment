package com.adyen.android.assignment.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.adyen.android.assignment.R

class SplashScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        /** SET FULLSCREEN */
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        /** SET UP ANIMATIONS */
        val adyen = findViewById<ImageView>(R.id.splash_adyen_iv)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        adyen.startAnimation(slideUp)

        /** GO TO MAIN ACTIVITY */
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}


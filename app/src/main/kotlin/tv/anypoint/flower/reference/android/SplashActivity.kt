package tv.anypoint.flower.reference.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val splashImage = findViewById<ImageView>(R.id.splashImage)
        Glide.with(this)
            .load(IMAGES[(Math.random() * IMAGES.size).toInt()])
            .into(splashImage)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 3000L)
    }

    companion object {
        private val IMAGES = arrayOf(
            "https://image.rocketpunch.com/company/31647/anypointmedia_logo_1498623304.png"
        )
    }
}

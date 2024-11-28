package tv.anypoint.flower.sdk.reference.android.vod

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import tv.anypoint.flower.android.sdk.api.FlowerSdk

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO GUIDE: Initialize SDK
        // env must be one of local, dev, prod
        FlowerSdk.setEnv("local")
        FlowerSdk.init(this)
        // Log level must be one of Verbose, Debug, Info, Warn, Error, Off
        FlowerSdk.setLogLevel("Debug")

        val rootLayout = findViewById<LinearLayout>(R.id.main)

        (videoList + null).forEach { video ->
            val button = Button(this).apply {
                text = video?.title ?: "Custom Channel"
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    val intent = Intent(this@MainActivity, PlaybackActivity::class.java)
                    video?.let { intent.putExtra(VIDEO, video) }
                    startActivity(intent)
                }
            }

            rootLayout.addView(button)
        }
    }

    companion object {
        const val VIDEO = "Video"
    }
}

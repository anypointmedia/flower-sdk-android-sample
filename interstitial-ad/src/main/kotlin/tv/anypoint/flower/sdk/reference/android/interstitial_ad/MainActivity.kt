package tv.anypoint.flower.sdk.reference.android.interstitial_ad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val intent = Intent(this, InterstitialAdActivity::class.java)
            startActivity(intent)
        }
    }
}

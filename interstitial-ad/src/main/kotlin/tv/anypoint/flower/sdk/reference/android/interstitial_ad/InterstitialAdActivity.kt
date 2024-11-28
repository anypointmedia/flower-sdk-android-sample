package tv.anypoint.flower.sdk.reference.android.interstitial_ad

import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import tv.anypoint.flower.android.sdk.api.FlowerAdView
import tv.anypoint.flower.sdk.core.api.FlowerAdsManagerListener
import tv.anypoint.flower.sdk.core.api.FlowerError

class InterstitialAdActivity: Activity() {
    private lateinit var flowerAdView: FlowerAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial_ad)

        // TODO GUIDE: Create FlowerAdView instance
        flowerAdView = findViewById(R.id.flowerAdView)

        requestAd()
    }

    private fun requestAd() {
        flowerAdView.adsManager.addListener(flowerAdsManagerListener)

        // TODO GUIDE: Request interstitial ad
        // arg0: adTagUrl, url from flower system
        //       You must file a request to Anypoint Media to receive a adTagUrl.
        // arg1: extraParams, values you can provide for targeting
        // arg2: adTagHeaders, (Optional) values included in headers for ad request
        flowerAdView.adsManager.requestAd(
            "https://ad_request",
            mapOf(),
            mapOf(),
        );
    }

    fun playAd() {
        flowerAdView.adsManager.play()
    }

    fun stopAd() {
        flowerAdView.adsManager.stop()
        flowerAdView.adsManager.removeListener(flowerAdsManagerListener)
    }

    // TODO GUIDE: Implement FlowerAdsManagerListener
    private val flowerAdsManagerListener = object : FlowerAdsManagerListener {
        override fun onPrepare(adDurationMs: Int) {
            CoroutineScope(Main).launch {
                // TODO GUIDE: Play interstitial ad
                playAd()
            }
        }

        override fun onPlay() {
            CoroutineScope(Main).launch {
                // OPTIONAL GUIDE: Need nothing to do for interstitial ad
            }
        }

        override fun onCompleted() {
            CoroutineScope(Main).launch {
                // TODO GUIDE: Stop FlowerAdsManager after the interstitial ad ends
                stopAd()
            }
        }

        override fun onError(error: FlowerError?) {
            CoroutineScope(Main).launch {
                // TODO GUIDE: Stop FlowerAdsManager on error
                stopAd()
            }
        }

        override fun onAdSkipped(reason: Int) {
            CoroutineScope(Main).launch {
                // OPTIONAL GUIDE: Need nothing to do for interstitial ad
                Log.i("FlowerSDK Example", "Ad skipped - reason: $reason")
            }
        }
    }
}

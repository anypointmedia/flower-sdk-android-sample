package tv.anypoint.flower.sdk.reference.android.interstitial_ad

import android.app.Activity
import android.os.Bundle
import org.lighthousegames.logging.logging
import tv.anypoint.flower.android.sdk.api.FlowerAdView
import tv.anypoint.flower.sdk.core.api.FlowerAdsManagerListener
import tv.anypoint.flower.sdk.core.api.FlowerError

class InterstitialAdActivity: Activity(), FlowerAdsManagerListener {
    private lateinit var flowerAdView: FlowerAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial_ad)

        // TODO GUIDE: create FlowerAdView instance
        flowerAdView = findViewById(R.id.flowerAdView)

        requestAd()
    }

    private fun requestAd() {
        flowerAdView.adsManager.addListener(this)

        // TODO GUIDE: request ad
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
        flowerAdView.adsManager.removeListener(this)
    }

    override fun onPrepare(adDurationMs: Int) {
        // TODO GUIDE: play ad
        playAd()
    }

    override fun onPlay() {
        // TODO GUIDE: need nothing for interstitial ad
    }

    override fun onCompleted() {
        // TODO GUIDE: stop FlowerAdsManager
        stopAd()
    }

    override fun onError(error: FlowerError?) {
        // TODO GUIDE: stop FlowerAdsManager
        stopAd()
    }

    override fun onAdSkipped(reason: Int) {
        logger.info { "onAdSkipped: $reason" }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        val logger = logging()
    }
}

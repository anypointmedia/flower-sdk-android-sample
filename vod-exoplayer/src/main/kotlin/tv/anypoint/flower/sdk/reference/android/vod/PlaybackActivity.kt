package tv.anypoint.flower.sdk.reference.android.vod

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.exoplayer2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import tv.anypoint.flower.android.sdk.api.FlowerAdView
import tv.anypoint.flower.sdk.core.api.*

class PlaybackActivity : Activity(), Player.Listener, FlowerAdsManagerListener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: SurfaceView

    private var video: Video? = null
    private lateinit var nextVideo: Video

    private lateinit var flowerAdView: FlowerAdView
    private lateinit var adInformation: TextView

    private var isContentEnd = false;

    private lateinit var urlInputField: EditText
    private lateinit var durationInputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        video = intent?.getSerializableExtra(MainActivity.VIDEO) as Video?
        nextVideo = videoList.filter { it != video }.first()

        logger.info { "received video: $video" }

        if (video == null) {
            findViewById<LinearLayout>(R.id.customChannelForm).visibility = View.VISIBLE

            urlInputField = findViewById<EditText>(R.id.urlInputField).apply {
                setText("https://xxx")
            }

            durationInputField = findViewById<EditText>(R.id.durationInputField).apply {
                setText("0")
            }

            findViewById<Button>(R.id.playButton).apply {
                setOnClickListener {
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(this.getWindowToken(), 0);

                    releasePlayer()
                    playVod()
                }
            }
        }

        // TODO GUIDE: create FlowerAdView instance
        flowerAdView = findViewById(R.id.flowerAdView)
        adInformation = findViewById(R.id.adInformation)
        playerView = findViewById(R.id.playerView)

        findViewById<Button>(R.id.switchButton).apply {
            text = "Switch to ${nextVideo.title}"
            setOnClickListener {
                releasePlayer()
                val intent = Intent(this@PlaybackActivity, Playback2Activity::class.java)
                intent.putExtra(MainActivity.VIDEO, nextVideo)
                startActivity(intent)
                finish()
            }
        }

        if (video != null) {
            playVod()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        releasePlayer()
    }

    private fun playVod() {
        val url = video?.url ?: urlInputField.text.toString()
        val durationMs = video?.durationMs ?: durationInputField.text.toString().toLong()

        flowerAdView.adsManager.addListener(this)

        player = ExoPlayer.Builder(this)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .build()
            )
            .build()

        player.addListener(this)

        // TODO GUIDE: implement MediaPlayerHook. if you use ExoPlayer, please use this method.
        val mediaPlayerHook = object : MediaPlayerHook {
            override fun getPlayer(): Any? {
                return player
            }
        }

        // TODO GUIDE: implement MediaPlayerHook. if you use own MediaPlayer, please use this method.
        val ownMediaPlayerHook = object : MediaPlayerHook {
            override fun getPlayer(): Any? {
                return object : MediaPlayerAdapter {

                    override fun getCurrentPosition(): Int {
                        TODO("Not yet implemented")
                    }

                    override fun getCurrentMediaChunk(): MediaChunk {
                        TODO("Not yet implemented")
                    }

                    override fun getVolume(): Float {
                        TODO("Not yet implemented")
                    }

                    override fun isPlaying(): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun getHeight(): Int {
                        TODO("Not yet implemented")
                    }

                }
            }
        }

        // TODO GUIDE: request vod ad
        // arg0: adTagUrl, url from flower system.
        //       You must file a request to Anypoint Media to receive a adTagUrl.
        // arg1: contentId, unique content id in your service
        // arg2: durationMs, duration of vod content in milliseconds
        // arg3: extraParams, values you can provide for targeting
        // arg4: mediaPlayerHook, interface that provides currently playing segment information for ad tracking
        // arg5: adTagHeaders, (Optional) values included in headers for ad request
        flowerAdView.adsManager.requestVodAd(
            "https://ad_request",
            "-255",
            durationMs,
            mapOf(),
            mediaPlayerHook,
            mapOf(),
        )

        player.setMediaItem(MediaItem.fromUri(url))

        player.setVideoSurfaceView(playerView)
    }

    private fun releasePlayer() {
        // TODO GUIDE: remove FlowerAdsManagerListener
        flowerAdView.adsManager.removeListener(this)
        // TODO GUIDE: should stop FlowerAdsManager
        flowerAdView.adsManager.stop()
        try {
            player.stop()
            player.release()
        } catch (e: Throwable) {
        }
    }

    override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
        logger.debug { "onSkipSilenceEnabledChanged - skipSilenceEnabled: $skipSilenceEnabled" }
    }

    // TODO GUIDE: should be implemented for vod ad
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        logger.debug { "onPlayerStateChanged - playWhenReady: $playWhenReady, playbackState: $playbackState" }
        when (playbackState) {
            Player.STATE_ENDED -> {
                isContentEnd = true
                // TODO GUIDE: notify end of vod content
                flowerAdView.adsManager.notifyContentEnded()
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        logger.error(error) { "onPlayerError" }
    }

    override fun onPlayerErrorChanged(error: PlaybackException?) {
        logger.error(error) { "onPlayerErrorChanged" }
    }

    override fun onResume() {
        super.onResume()

        flowerAdView.adsManager.resume()
    }

    override fun onPause() {
        super.onPause()

        flowerAdView.adsManager.pause()
    }

    override fun onPrepare(adDurationMs: Int) {
        runOnUiThread {
            if (player.isPlaying) {
                CoroutineScope(Main).launch {
                    // OPTIONAL GUIDE: additional actions before ad playback
                    adInformation.text = "Ads will start in a moment."
                    adInformation.visibility = View.VISIBLE
                    adInformation.refreshDrawableState()
                    delay(5_000)
                    adInformation.visibility = View.GONE
                    adInformation.refreshDrawableState()
                }

                // TODO GUIDE: play midroll ad
                flowerAdView.adsManager.play()
            } else {
                // TODO GUIDE: play preroll ad
                flowerAdView.adsManager.play()
            }
        }
    }

    override fun onPlay() {
        runOnUiThread {
            // TODO GUIDE: pause VOD content
            player.playWhenReady = false
        }
    }

    override fun onCompleted() {
        runOnUiThread {
            // TODO GUIDE: resume VOD content after ad complete
            logger.info { "ad onCompleted - channel player loading: ${player.isLoading}" }
            if (isContentEnd) {
                logger.info { "finish PlaybackActivity" }
                finish()
                return@runOnUiThread
            }
            if (!player.isLoading) {
                player.prepare()
            }
            player.playWhenReady = true
        }
    }

    override fun onError(error: FlowerError?) {
        runOnUiThread {
            // TODO GUIDE: resume VOD content on ad error
            if (isContentEnd) {
                logger.info { "finish PlaybackActivity" }
                finish()
                return@runOnUiThread
            }
            if (!player.isLoading) {
                player.prepare()
            }
            player.playWhenReady = true
        }
    }

    override fun onAdSkipped(reason: Int) {
        runOnUiThread {
            logger.info { "onAdSkipped: $reason" }
        }
    }

    companion object {
        val logger = logging()
    }
}

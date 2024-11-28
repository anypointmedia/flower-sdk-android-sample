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
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.MediaLoadData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ref.ott.org.lighthousegames.logging.logging
import tv.anypoint.flower.android.sdk.api.FlowerAdView
import tv.anypoint.flower.sdk.core.api.*

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : Activity(), Player.Listener, FlowerAdsManagerListener, AnalyticsListener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: SurfaceView

    private var video: Video? = null
    private lateinit var nextVideo: Video

    private lateinit var flowerAdView: FlowerAdView
    private lateinit var adInformation: TextView

    private var isContentEnd = false;
    private var isActivityPaused = false
    private var isPlayerError = false

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
                    createPlayer()
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
                val intent = Intent(this@PlaybackActivity, PlaybackActivity::class.java)
                intent.putExtra(MainActivity.VIDEO, nextVideo)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
        }

        if (video != null) {
            createPlayer()
            playVod()
        }
    }

    private fun playVod() {
        val url = video?.url ?: urlInputField.text.toString()
        val durationMs = video?.durationMs ?: durationInputField.text.toString().toLong()

        flowerAdView.adsManager.addListener(this)

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

    private fun createPlayer() {
        player = ExoPlayer.Builder(this)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .build()
            )
            .build()

        player.addListener(this)
        player.playWhenReady = true
        isPlayerError = false
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

    // OPTIONAL GUIDE: change extraParams during stream playback
    override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {
        flowerAdView.adsManager.changeChannelExtraParams(mapOf("myTargetingKey" to eventTime.realtimeMs.toString()))
    }

    override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime, oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
        logger.debug { "onPositionDiscontinuity - eventTime: $eventTime, oldPosition: $oldPosition, newPosition: $newPosition, reason: $reason" }
    }

    override fun onPlayerError(error: PlaybackException) {
        logger.error(error) { "onPlayerError" }
    }

    override fun onPlayerErrorChanged(error: PlaybackException?) {
        logger.error(error) { "onPlayerErrorChanged" }
    }

    override fun onUpstreamDiscarded(eventTime: AnalyticsListener.EventTime, mediaLoadData: MediaLoadData) {
        logger.info { "onUpstreamDiscarded - eventTime: $eventTime, mediaLoadData: $mediaLoadData" }
    }

    override fun onAudioSinkError(eventTime: AnalyticsListener.EventTime, audioSinkError: java.lang.Exception) {
        try {
            logger.error(audioSinkError) {
                "onAudioSinkError - ${eventTime.currentMediaPeriodId}, ${eventTime.mediaPeriodId}, " +
                        "${eventTime.currentPlaybackPositionMs}, ${eventTime.eventPlaybackPositionMs}"
            }
        } catch (e: Throwable) {
            logger.error(audioSinkError) { "onAudioSinkError - $eventTime" }
        }
    }

    override fun onResume() {
        super.onResume()

        flowerAdView.adsManager.resume()

        isActivityPaused = false
    }

    override fun onPause() {
        super.onPause()

        flowerAdView.adsManager.pause()

        isActivityPaused = true
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

    override fun onDestroy() {
        super.onDestroy()
        logger.debug { "onDestroy" }

        releasePlayer()
    }

    override fun onPrepare(adDurationMs: Int) {
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

    override fun onPlay() {
        // TODO GUIDE: pause VOD content
        player.playWhenReady = false
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

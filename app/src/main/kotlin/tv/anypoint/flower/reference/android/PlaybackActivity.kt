package tv.anypoint.flower.reference.android

import android.app.Activity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import tv.anypoint.flower.android.sdk.api.FlowerAdView
import tv.anypoint.flower.sdk.core.api.FlowerAdsManagerListener
import tv.anypoint.flower.sdk.core.api.FlowerError
import tv.anypoint.flower.sdk.core.api.MediaPlayerHook
import java.io.IOException

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : Activity(), Player.Listener, FlowerAdsManagerListener, AnalyticsListener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: SurfaceView

    private lateinit var video: Video

    private lateinit var flowerAdView: FlowerAdView
    private lateinit var adInformation: TextView

    private var isContentEnd = false;
    private var isActivityPaused = false
    private var isPlayerError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        video = intent?.getSerializableExtra(DetailsActivity.VIDEO) as Video

        logger.info { "received video: $video" }

        setContentView(R.layout.activity_playback)

        // TODO GUIDE: create FlowerAdView instance
        flowerAdView = findViewById(R.id.flowerAdView)
        adInformation = findViewById(R.id.adInformation)
        playerView = findViewById(R.id.playerView)

        createPlayer()
        if (video.vod) {
            playVod()
        } else {
            playLinearTv()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    private fun playVod() {
        flowerAdView.adsManager.addListener(this)

        // TODO GUIDE: implement MediaPlayerHook
        val mediaPlayerHook = object : MediaPlayerHook {
            override fun getPlayer(): Any? {
                return player
            }
        }

        // TODO GUIDE: request vod ad
        // arg0: adTagUrl, url from flower system.
        //       You must file a request to Anypoint Media to receive a adTagUrl.
        // arg1: contentId, unique content id in your service
        // arg2: durationMs, duration of vod content in milliseconds
        // arg3: extraParams, values you can provide for targeting
        // arg4: mediaPlayerHook, interface that provides currently playing segment information for ad tracking
        flowerAdView.adsManager.requestVodAd(
            "https://ad_request",
            "100",
            video.duration,
            mapOf(),
            mediaPlayerHook
        )

        player.setMediaItem(MediaItem.fromUri(video.videoUrl))

        player.setVideoSurfaceView(playerView)
    }

    private fun playLinearTv() {
        flowerAdView.adsManager.addListener(this)

        // TODO GUIDE: implement MediaPlayerHook
        val mediaPlayerHook = object : MediaPlayerHook {
            override fun getPlayer(): Any? {
                return player
            }
        }

        // TODO GUIDE: change original LinearTV stream url by adView.adsManager.changeChannelUrl
        // arg0: videoUrl, original LinearTV stream url
        // arg1: adTagUrl, url from flower system
        //       You must file a request to Anypoint Media to receive a adTagUrl.
        // arg2: channelId, unique channel id in your service
        // arg3: extraParams, values you can provide for targeting
        // arg4: mediaPlayerHook, interface that provides currently playing segment information for ad tracking
        val changedChannelUrl = flowerAdView.adsManager.changeChannelUrl(
            video.videoUrl,
            "https://ad_request",
            "1",
            mapOf(),
            mediaPlayerHook
        )

        logger.info { "from: ${video.videoUrl}, to: $changedChannelUrl" }

        player.setMediaItem(MediaItem.fromUri(changedChannelUrl))

        player.setVideoSurfaceView(playerView)

        player.addAnalyticsListener(this)

        player.prepare()
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

    override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime, oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
        logger.debug { "onPositionDiscontinuity - eventTime: $eventTime, oldPosition: $oldPosition, newPosition: $newPosition, reason: $reason" }
    }

    override fun onPlayerError(error: PlaybackException) {
        logger.error(error) { "onPlayerError" }
        replayLinearTv()
    }

    private fun replayLinearTv() {
        if (!video.vod) {
            logger.info { "replayLinearTv" }
            releasePlayer()
            createPlayer()
            playLinearTv()
        }
    }

    override fun onPlayerErrorChanged(error: PlaybackException?) {
        logger.error(error) { "onPlayerErrorChanged" }
    }

    override fun onLoadError(eventTime: AnalyticsListener.EventTime, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData, error: IOException, wasCanceled: Boolean) {
        logger.error(error) { "onLoadError - eventTime: $eventTime, loadEventInfo.uri: ${loadEventInfo.uri}, mediaLoadData: $mediaLoadData, wasCanceled: $wasCanceled" }

        replayLinearTv()
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
        } catch (e: Exception) {
            logger.error(audioSinkError) { "onAudioSinkError - $eventTime" }
        }
    }

    override fun onResume() {
        super.onResume()

        if (video.vod) {
            flowerAdView.adsManager.resume()
        } else {
            if (isPlayerError) {
                replayLinearTv()
            } else {
                player.playWhenReady = true
            }
        }

        isActivityPaused = false
    }

    override fun onPause() {
        super.onPause()

        if (video.vod) {
            flowerAdView.adsManager.pause()
        } else {
            flowerAdView.adsManager.pause()
            player.pause()
        }

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
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug { "onDestroy" }

        releasePlayer()
    }

    override fun onPrepare(adDurationMs: Int) {
        if (video.vod) {
            if (player.isPlaying) {
                CoroutineScope(Main).launch {
                    // OPTIONAL GUIDE: additional actions before ad playback
                    adInformation.text = "Ads will start in a moment."
                    adInformation.visibility = View.VISIBLE
                    adInformation.refreshDrawableState()
                    delay(5_000)
                    adInformation.visibility = View.GONE
                    adInformation.refreshDrawableState()

                    // TODO GUIDE: play midroll ad
                    flowerAdView.adsManager.play()
                }
            } else {
                // TODO GUIDE: play preroll ad
                flowerAdView.adsManager.play()
            }
        } else {
            // TODO GUIDE: need nothing for linear tv
        }
    }

    override fun onPlay() {
        if (video.vod) {
            // TODO GUIDE: pause VOD content
            player.playWhenReady = false
        } else {
            // OPTIONAL GUIDE: enable additional actions for ad playback
        }
    }

    override fun onCompleted() {
        runOnUiThread {
            if (video.vod) {
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
            } else {
                // OPTIONAL GUIDE: disable additional actions after ad complete
            }
        }
    }

    override fun onError(error: FlowerError?) {
        runOnUiThread {
            if (video.vod) {
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
            } else {
                // TODO GUIDE: restart to play Linear TV on ad error
                if (isActivityPaused) {
                    releasePlayer()
                    isPlayerError = true
                    return@runOnUiThread
                } else {
                    replayLinearTv()
                }
            }
        }
    }

    companion object {
        val logger = logging()
    }
}

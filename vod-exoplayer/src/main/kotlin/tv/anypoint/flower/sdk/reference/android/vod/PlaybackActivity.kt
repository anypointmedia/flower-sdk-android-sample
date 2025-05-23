package tv.anypoint.flower.sdk.reference.android.vod

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.exoplayer2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import tv.anypoint.flower.android.sdk.api.FlowerAdView
import tv.anypoint.flower.sdk.core.api.*

class PlaybackActivity : Activity(), Player.Listener {

    internal lateinit var player: ExoPlayer
    private lateinit var playerView: SurfaceView

    private var video: Video? = null
    private lateinit var nextVideo: Video

    internal lateinit var flowerAdView: FlowerAdView
    private val flowerAdsManagerListener = FlowerAdsManagerListenerImpl(this)

    internal var isContentEnd = false;

    private lateinit var urlInputField: EditText
    private lateinit var durationInputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        video = intent?.getSerializableExtra(MainActivity.VIDEO) as Video?
        nextVideo = videoList.filter { it != video }.first()

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

                    playVod()
                }
            }
        }

        // TODO GUIDE: Create FlowerAdView instance
        flowerAdView = findViewById(R.id.flowerAdView)
        playerView = findViewById(R.id.playerView)

        findViewById<Button>(R.id.switchButton).apply {
            text = "Switch to ${nextVideo.title}"
            setOnClickListener {
                // TODO GUIDE: Stop Flower SDK and release player resources on view destroy
                flowerAdView.adsManager.removeListener(flowerAdsManagerListener)
                flowerAdView.adsManager.stop()
                player.stop()
                player.release()

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

        // TODO GUIDE: Stop Flower SDK and release player resources on view destroy
        flowerAdView.adsManager.removeListener(flowerAdsManagerListener)
        flowerAdView.adsManager.stop()
        player.stop()
        player.release()
    }

    private fun playVod() {
        val url = video?.url ?: urlInputField.text.toString()
        val durationMs = video?.durationMs ?: durationInputField.text.toString().toLong()

        flowerAdView.adsManager.addListener(flowerAdsManagerListener)

        player = ExoPlayer.Builder(this)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .build()
            )
            .build()

        player.addListener(this)

        // TODO GUIDE: Implement MediaPlayerHook to return the player instance
        val mediaPlayerHook = object : MediaPlayerHook {
            override fun getPlayer(): Any? {
                return player
            }
        }

        // TODO GUIDE: Request VOD ad
        // arg0: adTagUrl, url from flower system.
        //       You must file a request to Anypoint Media to receive a adTagUrl.
        // arg1: contentId, unique content id in your service
        // arg2: durationMs, duration of VOD content in milliseconds
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

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_ENDED -> {
                isContentEnd = true
                // TODO GUIDE: Notify the end of VOD content
                flowerAdView.adsManager.notifyContentEnded()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        flowerAdView.adsManager.resume()
    }

    override fun onPause() {
        super.onPause()

        flowerAdView.adsManager.pause()
    }
}

// TODO GUIDE: Implement FlowerAdsManagerListener
private class FlowerAdsManagerListenerImpl(private val playbackActivity: PlaybackActivity) : FlowerAdsManagerListener {
    override fun onPrepare(adDurationMs: Int) {
        CoroutineScope(Main).launch {
            if (playbackActivity.player.isPlaying) {
                // OPTIONAL GUIDE: Implement custom actions for when the ad playback is ready

                // TODO GUIDE: Play mid-roll ad
                playbackActivity.flowerAdView.adsManager.play()
            } else {
                // TODO GUIDE: Play pre-roll ad
                playbackActivity.flowerAdView.adsManager.play()
            }
        }
    }

    override fun onPlay() {
        CoroutineScope(Main).launch {
            // TODO GUIDE: Pause VOD content when the ad playback starts
            playbackActivity.player.playWhenReady = false
        }
    }

    override fun onCompleted() {
        CoroutineScope(Main).launch {
            // TODO GUIDE: Resume VOD content when the ad playback ends
            if (playbackActivity.isContentEnd) {
                playbackActivity.finish()
                return@launch
            }
            if (!playbackActivity.player.isLoading) {
                playbackActivity.player.prepare()
            }
            playbackActivity.player.playWhenReady = true
        }
    }

    override fun onError(error: FlowerError?) {
        CoroutineScope(Main).launch {
            // TODO GUIDE: Resume VOD content on ad error
            if (playbackActivity.isContentEnd) {
                playbackActivity.finish()
                return@launch
            }
            if (!playbackActivity.player.isLoading) {
                playbackActivity.player.prepare()
            }
            playbackActivity.player.playWhenReady = true
        }
    }

    override fun onAdSkipped(reason: Int) {
        CoroutineScope(Main).launch {
            // OPTIONAL GUIDE: Need nothing to do for VOD
            Log.i("FlowerSDK Example", "Ad skipped - reason: $reason")
        }
    }
}

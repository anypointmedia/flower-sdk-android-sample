package tv.anypoint.flower.sdk.reference.android.linear_tv

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

    private var isActivityPaused = false
    private var isPlayerError = false

    private lateinit var urlInputField: EditText

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

            findViewById<Button>(R.id.playButton).apply {
                setOnClickListener {
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(this.getWindowToken(), 0);

                    releasePlayer()
                    playLinearTv()
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
            playLinearTv()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        releasePlayer()
    }

    private fun playLinearTv() {
        val url = video?.url ?: urlInputField.text.toString()

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

        // TODO GUIDE: change original LinearTV stream url by adView.adsManager.changeChannelUrl
        // arg0: videoUrl, original LinearTV stream url
        // arg1: adTagUrl, url from flower system
        //       You must file a request to Anypoint Media to receive a adTagUrl.
        // arg2: channelId, unique channel id in your service
        // arg3: extraParams, values you can provide for targeting
        // arg4: mediaPlayerHook, interface that provides currently playing segment information for ad tracking
        // arg5: adTagHeaders, (Optional) values included in headers for ad request
        // arg6: channelStreamHeaders, (Optional) values included in headers for channel stream request
        val changedChannelUrl = flowerAdView.adsManager.changeChannelUrl(
            url,
            "https://ad_request",
            "1",
            mapOf(),
            mediaPlayerHook,
            mapOf(),
            mapOf(),
        )

        player.setMediaItem(MediaItem.fromUri(changedChannelUrl))

        player.setVideoSurfaceView(playerView)

        player.playWhenReady = true

        player.prepare()
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

    private fun replayLinearTv() {
        logger.info { "replayLinearTv" }
        releasePlayer()
        playLinearTv()
    }

    override fun onPause() {
        super.onPause()

        flowerAdView.adsManager.pause()
        player.pause()

        isActivityPaused = true
    }

    override fun onResume() {
        super.onResume()

        if (isPlayerError) {
            replayLinearTv()
        } else {
            player.playWhenReady = true
        }

        isActivityPaused = false
    }

    override fun onPlayerError(error: PlaybackException) {
        logger.error(error) { "onPlayerError" }
        replayLinearTv()
    }

    override fun onPrepare(adDurationMs: Int) {
        // TODO GUIDE: need nothing for linear tv
    }

    override fun onPlay() {
        runOnUiThread {
            // OPTIONAL GUIDE: enable additional actions for ad playback
        }
    }

    override fun onCompleted() {
        runOnUiThread {
            // OPTIONAL GUIDE: disable additional actions after ad complete
        }
    }

    override fun onError(error: FlowerError?) {
        runOnUiThread {
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

    override fun onAdSkipped(reason: Int) {
        logger.info { "onAdSkipped: $reason" }
    }

    companion object {
        val logger = logging()
    }
}

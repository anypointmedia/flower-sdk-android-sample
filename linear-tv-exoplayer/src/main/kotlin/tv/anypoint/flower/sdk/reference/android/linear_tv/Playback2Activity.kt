package tv.anypoint.flower.sdk.reference.android.linear_tv

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

class Playback2Activity : Activity(), Player.Listener {

    internal lateinit var player: ExoPlayer
    private lateinit var playerView: SurfaceView

    private var video: Video? = null
    private lateinit var nextVideo: Video

    internal lateinit var flowerAdView: FlowerAdView
    private val flowerAdsManagerListener = FlowerAdsManagerListenerImpl2(this)

    private lateinit var urlInputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback2)

        video = intent?.getSerializableExtra(MainActivity.VIDEO) as Video?
        nextVideo = videoList.filter { it != video }.first()

        if (video == null) {
            findViewById<LinearLayout>(R.id.customChannelForm2).visibility = View.VISIBLE

            urlInputField = findViewById<EditText>(R.id.urlInputField2).apply {
                setText("https://xxx")
            }

            findViewById<Button>(R.id.playButton2).apply {
                setOnClickListener {
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(this.getWindowToken(), 0);

                    playLinearTv()
                }
            }
        }

        // TODO GUIDE: Create FlowerAdView instance
        flowerAdView = findViewById(R.id.flowerAdView2)
        playerView = findViewById(R.id.playerView2)

        findViewById<Button>(R.id.switchButton2).apply {
            text = "Switch to ${nextVideo.title}"
            setOnClickListener {
                // TODO GUIDE: Stop Flower SDK and release player resources on view destroy
                flowerAdView.adsManager.removeListener(flowerAdsManagerListener)
                flowerAdView.adsManager.stop()
                player.stop()
                player.release()

                val intent = Intent(this@Playback2Activity, PlaybackActivity::class.java)
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

        // TODO GUIDE: Stop Flower SDK and release player resources on view destroy
        flowerAdView.adsManager.removeListener(flowerAdsManagerListener)
        flowerAdView.adsManager.stop()
        player.stop()
        player.release()
    }

    internal fun playLinearTv() {
        val url = video?.url ?: urlInputField.text.toString()

        flowerAdView.adsManager.addListener(flowerAdsManagerListener)

        player = ExoPlayer.Builder(this)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .build()
            )
            .build()

        player.addListener(this)

        // TODO GUIDE: Implement MediaPlayerHook to return the player instance if the player is supported by Flower SDK
        val mediaPlayerHook = object : MediaPlayerHook {
            override fun getPlayer(): Any? {
                return player
            }
        }

        // TODO GUIDE: Implement MediaPlayerHook to return a MediaPlayerAdapter instance if the player is not supported by Flower SDK
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

                    override fun pause() {
                        TODO("Not yet implemented")
                    }

                    override fun resume() {
                        TODO("Not yet implemented")
                    }
                }
            }
        }

        // TODO GUIDE: Change original linear TV stream url
        // arg0: videoUrl, original linear TV stream url
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

    override fun onPause() {
        super.onPause()

        player.pause()
    }

    override fun onResume() {
        super.onResume()

        player.playWhenReady = true
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("FlowerSDK Example", "onPlayerError", error)

        // TODO GUIDE: Stop Flower SDK and release linear TV player resources on player error
        flowerAdView.adsManager.removeListener(flowerAdsManagerListener)
        flowerAdView.adsManager.stop()
        player.stop()
        player.release()

        // TODO GUIDE: Restart linear TV playback on player error
        playLinearTv()
    }
}

// TODO GUIDE: Implement FlowerAdsManagerListener
private class FlowerAdsManagerListenerImpl2(private val playback2Activity: Playback2Activity) : FlowerAdsManagerListener {
    override fun onPrepare(adDurationMs: Int) {
        CoroutineScope(Main).launch {
            // OPTIONAL GUIDE: Need nothing to do for linear TV
        }
    }

    override fun onPlay() {
        CoroutineScope(Main).launch {
            // OPTIONAL GUIDE: Implement custom actions for when the ad playback starts
        }
    }

    override fun onCompleted() {
        CoroutineScope(Main).launch {
            // OPTIONAL GUIDE: Implement custom actions for when the ad playback ends
        }
    }

    override fun onError(error: FlowerError?) {
        CoroutineScope(Main).launch {
            // TODO GUIDE: Stop Flower SDK and release linear TV player resources on ad error
            playback2Activity.flowerAdView.adsManager.removeListener(this@FlowerAdsManagerListenerImpl2)
            playback2Activity.flowerAdView.adsManager.stop()
            playback2Activity.player.stop()
            playback2Activity.player.release()

            // TODO GUIDE: Restart linear TV playback on ad error
            playback2Activity.playLinearTv()
        }
    }

    override fun onAdSkipped(reason: Int) {
        CoroutineScope(Main).launch {
            // OPTIONAL GUIDE: Need nothing to do for linear TV
            Log.i("FlowerSDK Example", "Ad skipped - reason: $reason")
        }
    }
}

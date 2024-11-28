package tv.anypoint.flower.sdk.reference.android.vod

import java.io.Serializable

data class Video(
    val title: String,
    val url: String,
    val durationMs: Long,
) : Serializable

val videoList = listOf(
    Video(
        title = "Your VOD 1",
        url = "https://xxx",
        durationMs = 0
    ),
    Video(
        title = "Your VOD 2",
        url = "https://xxx",
        durationMs = 0
    )
)

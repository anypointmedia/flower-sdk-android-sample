package tv.anypoint.flower.sdk.reference.android.linear_tv

import java.io.Serializable

data class Video(
    val title: String,
    val url: String,
) : Serializable

val videoList = listOf(
    Video(
        title = "Your Linear TV Channel 1",
        url = "https://xxx",
    ),
    Video(
        title = "Your Linear TV Channel 2",
        url = "https://xxx",
    )
)

package tv.anypoint.flower.reference.android

object VideoList {
    val categoryNames = arrayOf(
        "Linear TV",
        "VOD"
    )
    val linearTvList = listOf(
        Video(
            title = "Your Linear TV Stream",
            cardImageUrl = "https://apm-assets.s3.ap-northeast-2.amazonaws.com/demo/anypoint_logo.png",
            cardDetailIconUrl = "https://apm-assets.s3.ap-northeast-2.amazonaws.com/demo/anypoint_logo.png",
            // videoUrl should be a working m3u8 link
            videoUrl = "https://xxx"
        )
    )
    val vodList = listOf<Video>(
        Video(
            title = "Your VOD Stream",
            description = "",
            cardImageUrl = "https://apm-assets.s3.ap-northeast-2.amazonaws.com/demo/anypoint_logo.png",
            cardDetailIconUrl = "https://apm-assets.s3.ap-northeast-2.amazonaws.com/demo/anypoint_logo.png",
            // videoUrl should be a working m3u8 link
            videoUrl = "https://XXX",
            vod = true,
            // duration of video in milliseconds
            duration = 0
        )
    )
    val categoryItems = listOf(
        linearTvList,
        vodList
    )

}

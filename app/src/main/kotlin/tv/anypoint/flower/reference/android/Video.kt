package tv.anypoint.flower.reference.android

import java.io.Serializable

/**
 * Video class represents video entity with title, description, image thumbs and video url.
 */
data class Video(
    var id: Long = 0,
    var title: String?,
    var description: String = "",
    var backgroundImageUrl: String? = null,
    var cardImageUrl: String? = null,
    var cardDetailIconUrl: String? = null,
    var videoUrl: String,
    var studio: String? = null,
    val vod: Boolean = false,
    val duration: Long = 0L,
) : Serializable {
    override fun toString(): String {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}'
    }
}

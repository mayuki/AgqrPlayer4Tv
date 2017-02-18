package org.misuzilla.agqrplayer4tv.model.preference

enum class StreamingType(val value: Int) {
    RTMP(0),
    HLS(1);

    companion object {
        private val map = values().associateBy(StreamingType::value)
        fun fromInt(type: Int) = map[type]
    }
}
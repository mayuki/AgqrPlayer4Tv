package org.misuzilla.agqrplayer4tv.model.preference

enum class PlayerType(val value: Int) {
    EXO_PLAYER(0),
    ANDROID_DEFAULT(1),
    WEB_VIEW(2);

    companion object {
        private val map = PlayerType.values().associateBy(PlayerType::value)
        fun fromInt(type: Int) = map[type]
    }
}
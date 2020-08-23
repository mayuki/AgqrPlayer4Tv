package org.misuzilla.agqrplayer4tv.model.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ApplicationPreference {
    private val playerType = MutableLiveData(PlayerType.EXO_PLAYER)
    private val streamingType = MutableLiveData(StreamingType.HLS)
    private val isLastShutdownCorrectly = MutableLiveData(true)
    private val recommendationCurrentProgram = MutableLiveData("")

    private lateinit var context: Context
    private lateinit var sharedPreference: SharedPreferences

    fun initialize(context: Context) {
        this.context = context
        this.sharedPreference = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        playerType.value = PlayerType.fromInt(sharedPreference.getInt("PlayerType", PlayerType.EXO_PLAYER.value))
        streamingType.value = StreamingType.fromInt(sharedPreference.getInt("StreamingType", StreamingType.HLS.value))
        isLastShutdownCorrectly.value = sharedPreference.getBoolean("IsLastShutdownCorrectly", true)
        recommendationCurrentProgram.value = sharedPreference.getString("RecommendationCurrentProgram", "")
    }

    fun setPlayerType(value: PlayerType) {
        playerType.value = value
        sharedPreference.edit().putInt("PlayerType", value.value).commit()
    }
    fun getPlayerType(): LiveData<PlayerType> { return playerType }

    fun setStreamingType(value: StreamingType) {
        streamingType.value = value
        sharedPreference.edit().putInt("StreamingType", value.value).commit()
    }
    fun getStreamingType(): LiveData<StreamingType> { return streamingType }

    fun setIsLastShutdownCorrectly(value: Boolean) {
        isLastShutdownCorrectly.value = value
        sharedPreference.edit().putBoolean("IsLastShutdownCorrectly", value).commit()
    }
    fun getIsLastShutdownCorrectly(): LiveData<Boolean> { return isLastShutdownCorrectly }

    fun setRecommendationCurrentProgram(value: String) {
        recommendationCurrentProgram.value = value
        sharedPreference.edit().putString("RecommendationCurrentProgram", value).commit()
    }
    fun getRecommendationCurrentProgram(): LiveData<String> { return recommendationCurrentProgram }
}

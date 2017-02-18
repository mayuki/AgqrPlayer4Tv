package org.misuzilla.agqrplayer4tv.model.preference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.misuzilla.agqrplayer4tv.infrastracture.extension.*
import jp.keita.kagurazaka.rxproperty.RxProperty
import rx.Subscription
import rx.subscriptions.CompositeSubscription

object ApplicationPreference {
    val playerType = RxProperty(PlayerType.EXO_PLAYER)
    val streamingType = RxProperty(StreamingType.HLS)
    val isLastShutdownCorrectly = RxProperty(true)
    val recommendationCurrentProgram = RxProperty("")

    fun initialize(context: Context): Subscription {
        val subscriptions = CompositeSubscription()
        val sharedPreference = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        playerType.set(PlayerType.fromInt(sharedPreference.getInt("PlayerType", PlayerType.EXO_PLAYER.value)))
        streamingType.set(StreamingType.fromInt(sharedPreference.getInt("StreamingType", StreamingType.HLS.value)))
        isLastShutdownCorrectly.set(sharedPreference.getBoolean("IsLastShutdownCorrectly", true))
        recommendationCurrentProgram.set(sharedPreference.getString("RecommendationCurrentProgram", ""))

        playerType
                .subscribe { sharedPreference.edit().putInt("PlayerType", it.value).commit() }
                .addTo(subscriptions)
        streamingType
                .subscribe { sharedPreference.edit().putInt("StreamingType", it.value).commit() }
                .addTo(subscriptions)
        isLastShutdownCorrectly
                .subscribe { sharedPreference.edit().putBoolean("IsLastShutdownCorrectly", it).commit() }
                .addTo(subscriptions)
        recommendationCurrentProgram
                .subscribe { sharedPreference.edit().putString("RecommendationCurrentProgram", it).commit() }
                .addTo(subscriptions)

        return subscriptions
    }
}

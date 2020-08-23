package org.misuzilla.agqrplayer4tv.component.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.misuzilla.agqrplayer4tv.model.Timetable
import org.misuzilla.agqrplayer4tv.model.UpdateRecommendation

/**
 * Leanback Launcher (いわゆるホーム)のおすすめ一覧に表示するためのサービスです。
 */
class UpdateRecommendationService :
    IntentService("UpdateRecommendationService"),
    CoroutineScope by CoroutineScope(Dispatchers.Main)
{
    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            launch {
                updateNotificationsAsync()
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString(), ex)
        }
    }

    suspend fun updateNotificationsAsync() {
        Log.d(TAG, "updateNotificationAsync")
        val timetable = Timetable(applicationContext)
        val recommendation = UpdateRecommendation(applicationContext)

        // タイムテーブルの更新 & マッピングの取得、その後画像も取得
        // 画像とかなんやかんやを待つ
        val programAndIcons = recommendation.getCurrentAndNextProgramAndIconAsync(timetable)
        for (index in (0 until MAX_RECOMMENDATION_COUNT)) {
            if (programAndIcons.size - 1 < index) {
                // もし数が合わないときはそれはなくなった扱い
                notificationManager.cancel(index)
            } else {
                val value = programAndIcons[index]
                Log.d(TAG, "Recommendation: ${value.program}")
                notificationManager.notify(index, value.createNotification(applicationContext))
            }
        }
    }

    companion object {
        const val TAG = "UpdateRecommendation"
        const val MAX_RECOMMENDATION_COUNT = 2
    }
}
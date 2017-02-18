package org.misuzilla.agqrplayer4tv.component.broadcastreceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.misuzilla.agqrplayer4tv.component.service.UpdateRecommendationService

class BootupBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            scheduleRecommendationUpdate(context)
        }
    }

    companion object {
        const val TAG = "BootupBroadcastReceiver"
        val INITIAL_DELAY = 5000L

        fun scheduleRecommendationUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val recommendationIntent = Intent(context, UpdateRecommendationService::class.java)
            val alarmIntent = PendingIntent.getService(context, 0, recommendationIntent, 0)

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, INITIAL_DELAY, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent)
        }
    }
}
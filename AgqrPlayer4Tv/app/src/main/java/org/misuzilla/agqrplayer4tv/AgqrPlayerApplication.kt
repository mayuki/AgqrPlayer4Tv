package org.misuzilla.agqrplayer4tv

import android.app.Application
import android.content.Intent
import android.util.Log
import org.misuzilla.agqrplayer4tv.infrastracture.extension.subscribe
import org.misuzilla.agqrplayer4tv.model.NowPlaying
import org.misuzilla.agqrplayer4tv.model.Timetable
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import com.jakewharton.threetenabp.AndroidThreeTen
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import org.misuzilla.agqrplayer4tv.component.service.UpdateRecommendationService
import org.misuzilla.agqrplayer4tv.model.Reservation

class AgqrPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCenter.start(this, "ffc14c3d-6ca4-4a90-b3e3-ee8e8416183f", Analytics::class.java, Crashes::class.java)

        ApplicationPreference.initialize(this)
        AndroidThreeTen.init(this)

        Reservation.initialize(this)
        Reservation.instance.onChangeAsObservable.subscribe {
            startService(Intent(this, UpdateRecommendationService::class.java))
        }

        NowPlaying.initialize(Timetable(this))
        NowPlaying.now!!.program.subscribe {
            Log.d(TAG, "Current: ${it.title} (${it.mailAddress}) ${it.start}-${it.end}" )
            Reservation.instance.removeAllExpired()
        }
    }

    companion object {
        const val TAG = "AgqrPlayerApplication"
    }
}
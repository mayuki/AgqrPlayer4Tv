package org.misuzilla.agqrplayer4tv.component.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.fragment.PlaybackExoPlayerFragment
import org.misuzilla.agqrplayer4tv.component.fragment.PlaybackPlayerFragmentBase
import org.misuzilla.agqrplayer4tv.component.fragment.PlaybackWebViewFragment
import org.misuzilla.agqrplayer4tv.component.broadcastreceiver.BootupBroadcastReceiver
import org.misuzilla.agqrplayer4tv.component.service.UpdateRecommendationService
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.PlayerType
import android.content.Intent
import androidx.lifecycle.Observer
import org.misuzilla.agqrplayer4tv.AgqrPlayerApplication
import org.misuzilla.agqrplayer4tv.model.NowPlaying
import org.misuzilla.agqrplayer4tv.model.Reservation

class MainActivity : FragmentActivity() {
    private var currentFragment: PlaybackPlayerFragmentBase? = null
    private var lastPlayerType: PlayerType = PlayerType.EXO_PLAYER

    override fun onAttachedToWindow() {
        Log.d("MainActivity", "onAttachedToWindow")
        super.onAttachedToWindow()

        updateWindowFlags()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // スケジュールして、同時にサービスを起動する
        ApplicationPreference.setRecommendationCurrentProgram("")
        BootupBroadcastReceiver.scheduleRecommendationUpdate(applicationContext)
        startService(Intent(this, UpdateRecommendationService::class.java))

        NowPlaying.now.getProgram().observe(this, Observer {
            Log.d(AgqrPlayerApplication.TAG, "Current: ${it.title} (${it.mailAddress}) ${it.start}-${it.end}" )
            Reservation.instance.removeAllExpired()
        })
        Reservation.instance.onChangeAsObservable.subscribe {
            startService(Intent(this, UpdateRecommendationService::class.java))
        }

        updateWindowFlags()

        setupPlayerFlagment()
    }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        super.onResume()

        updateWindowFlags()

        if (lastPlayerType != ApplicationPreference.getPlayerType().value) {
            setupPlayerFlagment()
        }
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        super.onPause()

        clearWindowFlags()

        // 正常シャットダウンフラグを立てる
        ApplicationPreference.setIsLastShutdownCorrectly(true)
    }

    private fun updateWindowFlags() {
        // 起動したらスクリーンをオンにするように
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun clearWindowFlags() {
        // 起動したらスクリーンをオンにするようにしたやつを落とす
        // そうじゃないとActivityが生きている間、無限につきっぱなしになる
        window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // DayDream(スクリーンセーバー)を抑制する
    }

    override fun onDestroy() {
        Log.d("MainActivity", "onDestroy")

        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                currentFragment?.let {
                    if (it.viewModel.isPlaying().value!!) {
                        it.stop()
                    } else {
                        it.play()
                    }
                }
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    fun setupPlayerFlagment() {

        // RTMPは突然の死を迎えることがある…
//        if (!ApplicationPreference.isLastShutdownCorrectly.get() && ApplicationPreference.streamingType.get() == StreamingType.RTMP) {
//            ApplicationPreference.streamingType.set(StreamingType.HLS)
//        }

        val fragment = when (ApplicationPreference.getPlayerType().value) {
            PlayerType.EXO_PLAYER -> PlaybackExoPlayerFragment()
            PlayerType.ANDROID_DEFAULT -> PlaybackExoPlayerFragment()
            PlayerType.WEB_VIEW -> PlaybackWebViewFragment()
            else -> throw NotImplementedError()
        }

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit()

        // 正常シャットダウンフラグを折っておく
        ApplicationPreference.setIsLastShutdownCorrectly(false)

        lastPlayerType = ApplicationPreference.getPlayerType().value!!
    }
}

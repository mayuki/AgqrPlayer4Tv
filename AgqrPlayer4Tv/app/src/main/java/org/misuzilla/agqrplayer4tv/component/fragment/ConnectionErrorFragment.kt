package org.misuzilla.agqrplayer4tv.component.fragment

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.ErrorSupportFragment
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.SettingsActivity

class ConnectionErrorFragment : ErrorSupportFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = context!!.getString(R.string.app_name)
        message = context!!.getString(R.string.error_connection_failed_repeatedly)
        buttonText = context!!.getString(R.string.error_open_setting)
        imageDrawable = context!!.getDrawable(R.drawable.lb_ic_sad_cloud)
        setDefaultBackground(true)

        setButtonClickListener {
            activity!!.finish()
            startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
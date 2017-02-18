package org.misuzilla.agqrplayer4tv.component.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.fragment.ConnectionErrorFragment

class ErrorActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = ConnectionErrorFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.main_frame, fragment)
            transaction.commit()
        }
    }
}

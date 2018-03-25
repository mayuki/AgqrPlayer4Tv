package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.content.Context
import android.os.Bundle
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addAction
import java.util.*

/**
 * Created by Tomoyo on 12/3/2016.
 */

class SettingsGuidedStepFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(context!!.getString(R.string.guidedstep_settings_title), context!!.getString(R.string.guidedstep_settings_description), context!!.getString(R.string.app_name), null)
    }


    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        with (actions) {
            addAction(context!!, 0, context!!.getString(R.string.guidedstep_streaming_title), context!!.getString(R.string.guidedstep_streaming_description))
            addAction(context!!, 1, context!!.getString(R.string.guidedstep_player_title), context!!.getString(R.string.guidedstep_player_description))
            addAction(context!!, 2, context!!.getString(R.string.guidedstep_about_app_title), context!!.getString(R.string.guidedstep_about_app_description))
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        add(fragmentManager, when (action.id) {
            0L -> StreamingSettingGuidedStepFragment()
            1L -> PlayerSettingGuidedStepFragment()
            2L -> AboutSettingGuidedStepFragment()
            else -> throw IllegalArgumentException("Unknown action Id")
        })
    }
}

package org.misuzilla.agqrplayer4tv.component.activity

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.fragment.app.FragmentActivity
import org.misuzilla.agqrplayer4tv.component.fragment.guidedstep.AboutSettingGuidedStepFragment
import org.misuzilla.agqrplayer4tv.component.fragment.guidedstep.CancelAllSchedulesConfirmationGuidedStepFragment
import org.misuzilla.agqrplayer4tv.component.fragment.guidedstep.SettingsGuidedStepFragment

class SettingsActivity : FragmentActivity() {
    private val openFragmentType: SettingsFragmentType by lazy { SettingsFragmentType.valueOf(intent.getStringExtra(ARG_OPEN_FRAGMENT_TYPE)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, when (openFragmentType) {
                SettingsFragmentType.ABOUT -> AboutSettingGuidedStepFragment()
                SettingsFragmentType.CANCEL_ALL_SCHEDULES -> CancelAllSchedulesConfirmationGuidedStepFragment()
                else -> SettingsGuidedStepFragment()
            }, R.id.content)
        }
    }

    companion object {
        const val ARG_OPEN_FRAGMENT_TYPE = "OpenFragmentType"

        fun createIntent(context: Context, fragmentType: SettingsFragmentType): Intent {
            return Intent(context, SettingsActivity::class.java)
                    .putExtra(ARG_OPEN_FRAGMENT_TYPE, fragmentType.name)
        }
    }

    enum class SettingsFragmentType {
        DEFAULT,
        ABOUT,
        CANCEL_ALL_SCHEDULES
    }
}

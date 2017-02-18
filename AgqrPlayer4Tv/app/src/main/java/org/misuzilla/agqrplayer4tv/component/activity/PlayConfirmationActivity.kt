package org.misuzilla.agqrplayer4tv.component.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v4.app.FragmentActivity
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.fragment.guidedstep.PlayConfirmationGuidedStepFragment
import org.misuzilla.agqrplayer4tv.model.TimetableProgram

/**
 * Recommendationから呼び出される、再生確認画面のActivityです。
 */
class PlayConfirmationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val program = intent.getSerializableExtra(ARG_PROGRAM) as TimetableProgram
        val fragment = PlayConfirmationGuidedStepFragment.createInstance(program, intent.getBooleanExtra(ARG_CAN_START_ACTIVITY, true))

        GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
    }

    companion object {
        const val ARG_PROGRAM = "Program"
        const val ARG_CAN_START_ACTIVITY = "CanStartActivity" // 直接起動できる選択肢を出すかどうか

        fun createIntent(context: Context, program: TimetableProgram, canStartActivity: Boolean): Intent {
            return Intent(context, PlayConfirmationActivity::class.java)
                    .putExtra(ARG_PROGRAM, program)
                    .putExtra(ARG_CAN_START_ACTIVITY, canStartActivity)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.MainActivity
import org.misuzilla.agqrplayer4tv.model.*

/**
 * 再生か予約の確認画面のFragment
 */
class PlayConfirmationGuidedStepFragment : GuidedStepSupportFragment() {
    private val program: TimetableProgram by lazy { arguments!![ARG_PROGRAM] as TimetableProgram }
    private val canStartActivity: Boolean by lazy { arguments!![ARG_CAN_START_ACTIVITY] as Boolean }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        val isScheduled = Reservation.instance.isScheduled(program)

        return GuidanceStylist.Guidance(program.title,
                context!!.getString(R.string.play_confirmation_description)
                        .format(program.personality, program.start.toShortString(), if (isScheduled) context!!.getString(R.string.play_confirmation_description_scheduled) else ""),
                context!!.getString(R.string.app_name),
                null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val isScheduled = Reservation.instance.isScheduled(program)

        if (canStartActivity) {
            actions.add(GuidedAction.Builder(context)
                    .id(ACTION_OPEN_IMMEDIATELY.toLong())
                    .title(context!!.getString(R.string.play_confirmation_open_player))
                    .description(context!!.getString(R.string.play_confirmation_open_player_description))
                    .build())
        }

        if (isScheduled) {
            actions.add(GuidedAction.Builder(context)
                    .id(ACTION_CANCEL_TIMER.toLong())
                    .title(context!!.getString(R.string.play_confirmation_cancel_timer))
                    .description(context!!.getString(R.string.play_confirmation_cancel_timer_description))
                    .build())
        } else {
            actions.add(GuidedAction.Builder(context)
                    .id(ACTION_REGISTER_TIMER.toLong())
                    .title(context!!.getString(R.string.play_confirmation_set_timer))
                    .description(context!!.getString(R.string.play_confirmation_set_timer_description).format(program.start.toShortString()))
                    .build())
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val intent = Intent(context, MainActivity::class.java)

        when (action.id.toInt()) {
            ACTION_OPEN_IMMEDIATELY -> {
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finishGuidedStepSupportFragments()
            }
            ACTION_REGISTER_TIMER -> {
                Reservation.instance.schedule(program)
                finishGuidedStepSupportFragments()
            }
            ACTION_CANCEL_TIMER -> {
                Reservation.instance.cancel(program)
                finishGuidedStepSupportFragments()
            }
        }
    }

    companion object {
        const val ACTION_OPEN_IMMEDIATELY = 0
        const val ACTION_REGISTER_TIMER = 1
        const val ACTION_CANCEL_TIMER = 2

        const val ARG_PROGRAM = "Program"
        const val ARG_CAN_START_ACTIVITY = "CanStartActivity"

        fun createInstance(program: TimetableProgram, canStartActivity: Boolean): PlayConfirmationGuidedStepFragment {
            val fragment = PlayConfirmationGuidedStepFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(ARG_PROGRAM, program)
                putBoolean(ARG_CAN_START_ACTIVITY, canStartActivity)
            }
            return fragment
        }
    }
}
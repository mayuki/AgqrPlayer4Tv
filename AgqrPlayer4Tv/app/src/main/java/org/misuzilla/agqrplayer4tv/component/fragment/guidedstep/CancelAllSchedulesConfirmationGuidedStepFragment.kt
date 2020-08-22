package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.model.Reservation

/**
 * Created by Tomoyo on 1/16/2017.
 */
class CancelAllSchedulesConfirmationGuidedStepFragment  : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(context!!.getString(R.string.cancel_all_schedules_confirmation),
                context!!.getString(R.string.cancel_all_schedules_confirmation_description),
                context!!.getString(R.string.app_name),
                null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(GuidedAction.Builder(context)
                .id(ACTION_CANCEL.toLong())
                .title(context!!.getString(R.string.action_cancel))
                .build())
        actions.add(GuidedAction.Builder(context)
                .id(ACTION_OK.toLong())
                .title(context!!.getString(R.string.action_ok))
                .build())
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_CANCEL -> {
                finishGuidedStepSupportFragments()
            }
            ACTION_OK -> {
                Reservation.instance.cancelAll()
                finishGuidedStepSupportFragments()
            }
        }
    }

    companion object {
        const val ACTION_CANCEL = 0L
        const val ACTION_OK = 1L

        fun createInstance(): CancelAllSchedulesConfirmationGuidedStepFragment {
            return CancelAllSchedulesConfirmationGuidedStepFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.os.Bundle
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addCheckAction
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.StreamingType

/**
 * 設定: 配信形式設定のGuidedStepクラスです。
 */
class StreamingSettingGuidedStepFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(context.getString(R.string.guidedstep_streaming_title), context.getString(R.string.guidedstep_streaming_description_long), context.getString(R.string.guidedstep_settings_title), null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        with (actions) {
            addCheckAction(context, StreamingType.RTMP.value,
                    context.getString(R.string.guidedstep_streaming_type_rtmp),
                    context.getString(R.string.guidedstep_streaming_type_rtmp_description),
                    ApplicationPreference.streamingType.get() == StreamingType.RTMP)
            addCheckAction(context, StreamingType.HLS.value,
                    context.getString(R.string.guidedstep_streaming_type_hls),
                    context.getString(R.string.guidedstep_streaming_type_hls_description),
                    ApplicationPreference.streamingType.get() == StreamingType.HLS)
        }
    }


    override fun onGuidedActionClicked(action: GuidedAction) {
        actions.forEach { it.isChecked = it == action }
        ApplicationPreference.streamingType.set(StreamingType.fromInt(action.id.toInt()))

        fragmentManager.popBackStack()
    }
}
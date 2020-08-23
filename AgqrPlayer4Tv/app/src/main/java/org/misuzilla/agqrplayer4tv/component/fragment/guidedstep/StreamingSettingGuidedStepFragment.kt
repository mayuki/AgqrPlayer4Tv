package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addCheckAction
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.StreamingType

/**
 * 設定: 配信形式設定のGuidedStepクラスです。
 */
class StreamingSettingGuidedStepFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(requireContext().getString(R.string.guidedstep_streaming_title), requireContext().getString(R.string.guidedstep_streaming_description_long), requireContext().getString(R.string.guidedstep_settings_title), null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        with (actions) {
            addCheckAction(requireContext(), StreamingType.RTMP.value,
                requireContext().getString(R.string.guidedstep_streaming_type_rtmp),
                requireContext().getString(R.string.guidedstep_streaming_type_rtmp_description),
                ApplicationPreference.getStreamingType().value == StreamingType.RTMP)
            addCheckAction(requireContext(), StreamingType.HLS.value,
                requireContext().getString(R.string.guidedstep_streaming_type_hls),
                requireContext().getString(R.string.guidedstep_streaming_type_hls_description),
                ApplicationPreference.getStreamingType().value == StreamingType.HLS)
        }
    }


    override fun onGuidedActionClicked(action: GuidedAction) {
        actions.forEach { it.isChecked = it == action }
        ApplicationPreference.setStreamingType(StreamingType.fromInt(action.id.toInt())!!)

        parentFragmentManager.popBackStack()
    }
}
package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addCheckAction
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.PlayerType

/**
 * 設定: プレイヤー設定のGuidedStepクラスです。
 */
class PlayerSettingGuidedStepFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(requireContext().getString(R.string.guidedstep_player_title), requireContext().getString(R.string.guidedstep_player_description), requireContext().getString(R.string.guidedstep_settings_title), null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val playerType = ApplicationPreference.getPlayerType().value
        with (actions) {
            addCheckAction(requireContext(), PlayerType.EXO_PLAYER.value,
                requireContext().getString(R.string.guidedstep_player_type_exoplayer),
                requireContext().getString(R.string.guidedstep_player_type_exoplayer_description),
                playerType == PlayerType.EXO_PLAYER || playerType == PlayerType.ANDROID_DEFAULT)
            addCheckAction(requireContext(), PlayerType.WEB_VIEW.value,
                requireContext().getString(R.string.guidedstep_player_type_webview),
                requireContext().getString(R.string.guidedstep_player_type_webview_description),
                playerType == PlayerType.WEB_VIEW)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        actions.forEach { it.isChecked = it == action }
        ApplicationPreference.setPlayerType(PlayerType.fromInt(action.id.toInt())!!)

        parentFragmentManager.popBackStack()
    }
}
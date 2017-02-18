package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.os.Bundle
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addCheckAction
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.PlayerType

/**
 * 設定: プレイヤー設定のGuidedStepクラスです。
 */
class PlayerSettingGuidedStepFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(context.getString(R.string.guidedstep_player_title), context.getString(R.string.guidedstep_player_description), context.getString(R.string.guidedstep_settings_title), null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        with (actions) {
            addCheckAction(context, PlayerType.EXO_PLAYER.value,
                    context.getString(R.string.guidedstep_player_type_exoplayer),
                    context.getString(R.string.guidedstep_player_type_exoplayer_description),
                    ApplicationPreference.playerType.get() == PlayerType.EXO_PLAYER)
            addCheckAction(context, PlayerType.ANDROID_DEFAULT.value,
                    context.getString(R.string.guidedstep_player_type_default),
                    context.getString(R.string.guidedstep_player_type_default_description),
                    ApplicationPreference.playerType.get() == PlayerType.ANDROID_DEFAULT)
            addCheckAction(context, PlayerType.WEB_VIEW.value,
                    context.getString(R.string.guidedstep_player_type_webview),
                    context.getString(R.string.guidedstep_player_type_webview_description),
                    ApplicationPreference.playerType.get() == PlayerType.WEB_VIEW)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        actions.forEach { it.isChecked = it == action }
        ApplicationPreference.playerType.set(PlayerType.fromInt(action.id.toInt()))

        fragmentManager.popBackStack()
    }
}
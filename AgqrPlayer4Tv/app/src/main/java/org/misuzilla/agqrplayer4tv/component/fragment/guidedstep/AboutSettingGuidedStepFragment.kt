package org.misuzilla.agqrplayer4tv.component.fragment.guidedstep

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import org.misuzilla.agqrplayer4tv.BuildConfig
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addInfo
import java.text.SimpleDateFormat
import java.util.*

/**
 * 設定: このアプリケーションについて
 */
class AboutSettingGuidedStepFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(context!!.getString(R.string.guidedstep_about_app_title), context!!.getString(R.string.guidedstep_about_app_description), context!!.getString(R.string.guidedstep_settings_title), null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val packageInfo = context!!.packageManager.getPackageInfo(context!!.packageName, PackageManager.GET_META_DATA);
        with (actions) {
            var index = 0
            addInfo(context!!, ++index, context!!.getString(R.string.guidedstep_about_app_version),
                if (BuildConfig.DEBUG) {
                    "${packageInfo.versionName} (VersionCode ${packageInfo.versionCode}), Debug"
                } else {
                    "${packageInfo.versionName} (VersionCode ${packageInfo.versionCode})"
                }
            )
            addInfo(context!!, ++index, context!!.getString(R.string.guidedstep_about_app_build), "${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date(packageInfo.lastUpdateTime))}")
            addInfo(context!!, ++index, context!!.getString(R.string.guidedstep_about_app_device), "${Build.MANUFACTURER} ${Build.MODEL}")
            addInfo(context!!, ++index, context!!.getString(R.string.guidedstep_about_app_android), "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        }
    }
}
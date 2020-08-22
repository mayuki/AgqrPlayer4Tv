package org.misuzilla.agqrplayer4tv.component.widget

import android.graphics.drawable.Drawable
import androidx.leanback.widget.Action

/**
 * Created by Tomoyo on 1/16/2017.
 */
class SettingsCommandScheduleAction(id: Long, label: String, label2Format: String? = null, icon: Drawable? = null, execute: (() -> Unit)) : CommandAction(id, label, label2Format, icon, execute) {

}
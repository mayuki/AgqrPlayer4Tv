package org.misuzilla.agqrplayer4tv.component.widget

import android.graphics.drawable.Drawable
import androidx.leanback.widget.Action

/**
 * 実行するコマンドを保持するアクションを表すクラスです。
 */
open class CommandAction(id: Long, label: String, label2: String? = null, icon: Drawable? = null, val execute: (() -> Unit)) : Action(id, label, label2, icon) {
}
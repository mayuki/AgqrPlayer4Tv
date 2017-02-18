package org.misuzilla.agqrplayer4tv.infrastracture.extension

import android.content.Context
import android.support.v17.leanback.widget.GuidedAction
import java.util.*

/**
 * Created by Tomoyo on 12/3/2016.
 */


fun MutableList<GuidedAction>.addInfo(context: Context, id: Int, title: String, description: String) {
    val builder = GuidedAction.Builder(context)
    val action = builder
            .id(id.toLong())
            .infoOnly(true)
            .title(title)
            .description(description)
            .multilineDescription(true)
            .build()
    this.add(action)
}
fun MutableList<GuidedAction>.addAction(context: Context, id: Int, title: String, description: String) {
    val builder = GuidedAction.Builder(context)
    val action = builder
            .id(id.toLong())
            .title(title)
            .description(description)
            .build()
    this.add(action)
}
fun MutableList<GuidedAction>.addCheckAction(context: Context, id: Int, title: String, description: String, isChecked: Boolean, checkSetId: Int = 1) {
    val builder = GuidedAction.Builder(context)
    val action = builder
            .id(id.toLong())
            .title(title)
            .description(description)
            .checkSetId(checkSetId)
            .checked(isChecked)
            .build()
    this.add(action)
}
fun MutableList<GuidedAction>.addActionWithSubActions(context: Context, id: Int, title: String, description: String, subActionBuilder: (MutableList<GuidedAction>) -> Unit) {
    val builder = GuidedAction.Builder(context)
    builder.id(id.toLong())
            .title(title)
            .description(description)

    val subactions = ArrayList<GuidedAction>()
    subActionBuilder(subactions)

    builder.subActions(subactions)

    this.add(builder.build())
}
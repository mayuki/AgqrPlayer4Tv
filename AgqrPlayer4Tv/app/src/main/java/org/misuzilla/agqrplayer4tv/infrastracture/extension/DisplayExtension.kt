package org.misuzilla.agqrplayer4tv.infrastracture.extension

import android.util.DisplayMetrics

fun DisplayMetrics.toDevicePixel(value: Float): Int {
    return Math.round(value * this.density)
}
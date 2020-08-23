package org.misuzilla.agqrplayer4tv.infrastracture.extension

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

suspend fun RequestCreator.await(): Bitmap {
    return this.toDeferred().await()
}

fun RequestCreator.toDeferred(): Deferred<Bitmap> {
    val completableDeferred = CompletableDeferred<Bitmap>()

    this.into(object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
            completableDeferred.complete(bitmap)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            completableDeferred.completeExceptionally(Exception("Fetch failed."))
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }
    })

    return completableDeferred
}
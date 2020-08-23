package org.misuzilla.agqrplayer4tv.infrastracture.extension

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.*
import java.io.IOException
import java.net.URL


fun OkHttpClient.get(url: String): Call {
    return this.newCall(okhttp3.Request.Builder()
            .get()
            .url(url)
            .build())
}
fun OkHttpClient.get(url: URL): Call {
    return this.newCall(okhttp3.Request.Builder()
            .get()
            .url(url)
            .build())
}
fun OkHttpClient.get(url: HttpUrl): Call {
    return this.newCall(okhttp3.Request.Builder()
            .get()
            .url(url)
            .build())
}

suspend fun Call.await(): Response {
    val completableDeferred = CompletableDeferred<Response>()

    this.enqueue(object : Callback {
        override fun onFailure(call: Call?, e: IOException?) {
            completableDeferred.completeExceptionally(e!!)
        }

        override fun onResponse(call: Call?, response: Response?) {
            completableDeferred.complete(response!!)
        }
    })

    return completableDeferred.await()
}
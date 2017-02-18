package org.misuzilla.agqrplayer4tv.infrastracture.extension

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
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
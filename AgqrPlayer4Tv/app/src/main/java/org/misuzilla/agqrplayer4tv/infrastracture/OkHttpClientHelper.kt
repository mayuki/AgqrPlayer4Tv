package org.misuzilla.agqrplayer4tv.infrastracture

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.misuzilla.agqrplayer4tv.R
import java.io.File

class OkHttpClientHelper {
    companion object {
        const val CACHE_DIR = "http"
        const val CACHE_SIZE = 1024 * 1024 * 10L

        /**
         * 汎用的なリクエストに利用するためのOkHttpClientを生成します。
         */
        fun create(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                    .cache(Cache(File(context.cacheDir, CACHE_DIR), CACHE_SIZE))
                    .addInterceptor {
                        val request = it.request().newBuilder()
                                .addHeader("User-Agent", context.resources.getString(R.string.user_agent))
                                .build()

                        it.proceed(request)
                    }
                    .build()
        }
    }
}
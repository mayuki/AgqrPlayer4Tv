package org.misuzilla.agqrplayer4tv.infrastracture.exoplayer

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import net.butterflytv.rtmp_client.RtmpClient

/**
 * Created by Tomoyo on 12/11/2016.
 */
class RtmpDataSource : DataSource {
    private val rtmpClient = RtmpClient()
    private var uri: Uri? = null

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        rtmpClient.open(uri?.toString(), false)

        return C.LENGTH_UNSET.toLong()
    }

    override fun getUri(): Uri {
        return uri!!
    }

    override fun close() {
        rtmpClient.close()
    }

    override fun addTransferListener(transferListener: TransferListener?) {
    }

    override fun read(buffer: ByteArray?, offset: Int, readLength: Int): Int {
        if (!rtmpClient.isConnected) return 0
        return rtmpClient.read(buffer, offset, readLength)
    }

}
package org.misuzilla.agqrplayer4tv.component.fragment.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import androidx.lifecycle.LifecycleOwner
import org.misuzilla.agqrplayer4tv.component.fragment.PlaybackControlsRowViewModel

class AgqrDetailsDescriptionPresenter(private val lifecycleOwner: LifecycleOwner) : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, item: Any?) {
        val nowPlaying = (item as PlaybackControlsRowViewModel).nowPlaying

        viewHolder.apply {
            title.text = ""
            subtitle.text = ""
            body.text = ""
        }

        nowPlaying.apply {
            getTitle().observe(lifecycleOwner, { viewHolder.title.text = it })
            getSubTitle().observe(lifecycleOwner, { viewHolder.subtitle.text = it })
            getBody().observe(lifecycleOwner, { viewHolder.body.text = it })
        }
    }
}
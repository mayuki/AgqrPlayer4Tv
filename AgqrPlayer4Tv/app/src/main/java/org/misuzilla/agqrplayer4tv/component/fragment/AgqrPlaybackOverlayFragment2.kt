package org.misuzilla.agqrplayer4tv.component.fragment

import android.R
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.PlaybackSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.misuzilla.agqrplayer4tv.component.activity.SettingsActivity
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.ProgramCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.TypedArrayObjectAdapter
import org.misuzilla.agqrplayer4tv.model.*


class AgqrPlaybackOverlayFragment2 : PlaybackSupportFragment(), CoroutineScope by MainScope() {
    private lateinit var transpotControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var wrappedPlayer: PlayerWrapper
    private lateinit var programCardsAdapter: TypedArrayObjectAdapter<ProgramCardPresenter.Item>
    private val playbackPlayer: PlaybackExoPlayerFragment
        get () = this.parentFragment as PlaybackExoPlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wrappedPlayer = PlayerWrapper(playbackPlayer.player)
        val glueHost = PlaybackSupportFragmentGlueHost(this)
        val playerAdapter = LeanbackPlayerAdapter(requireContext(), wrappedPlayer, 250)

        transpotControlGlue = CustomPlaybackTransportControlGlue(requireContext(), playerAdapter)
        transpotControlGlue.host = glueHost
        transpotControlGlue.title = "-"
        transpotControlGlue.subtitle = "-"

        adapter = initializeRows()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NowPlaying.now.getTitle().observe(viewLifecycleOwner, { transpotControlGlue.title = it })
        NowPlaying.now.getSubTitle().observe(
            viewLifecycleOwner,
            { transpotControlGlue.subtitle = it })
        NowPlaying.now.getProgram().observe(
            viewLifecycleOwner,
            { wrappedPlayer.currentProgram = it })

        NowPlaying.now.getProgram()
            .observe(viewLifecycleOwner, {
                launch {
                    val mapping = UpdateRecommendation(requireContext()).getProgramImageMappingAsync()
                    val dataset = Timetable(requireContext()).getDatasetAsync()
                    programCardsAdapter.clear()
                    val now = LogicalDateTime.now
                    programCardsAdapter.add(0, dataset.data[now.dayOfWeek]!!.filter { it.start > now.time }.map { ProgramCardPresenter.Item(it, mapping) })
                    //rowsAdapter.notifyArrayItemRangeChanged(1, programCards.size())
                }

                //rowsAdapter.notifyArrayItemRangeChanged(0, 1)
            })
    }

    private fun initializeRows(): ArrayObjectAdapter {
        val presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(
            transpotControlGlue.getControlsRow().javaClass, transpotControlGlue.getPlaybackRowPresenter()
        )
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        val rowsAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(transpotControlGlue.getControlsRow())

        programCardsAdapter = TypedArrayObjectAdapter(ProgramCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem(requireContext().getString(org.misuzilla.agqrplayer4tv.R.string.playback_rows_upcoming_header)), programCardsAdapter))

        return rowsAdapter
    }
}

class PlayerWrapper(private val underlyingPlayer: Player) : Player by underlyingPlayer {
    var currentProgram: TimetableProgram = TimetableProgram.DEFAULT

    override fun getDuration(): Long {
        return 1000 * (currentProgram.end.totalSeconds - currentProgram.start.totalSeconds).toLong()
    }

    override fun getCurrentPosition(): Long {
        return 1000 * (LogicalDateTime.now.time.totalSeconds - currentProgram.start.totalSeconds).toLong()
    }

    override fun getBufferedPercentage(): Int {
        return 0
    }
}

class CustomPlaybackTransportControlGlue(context: Context, adapter: LeanbackPlayerAdapter) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
    context,
    adapter
) {
    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        secondaryActionsAdapter.add(PlaybackControlsRow.MoreActions(context))
    }

    override fun onActionClicked(action: Action?) {
        if (action is PlaybackControlsRow.MoreActions) {
            context.startActivity(
                SettingsActivity.createIntent(
                    context,
                    SettingsActivity.SettingsFragmentType.DEFAULT
                )
            )
            return
        }
        super.onActionClicked(action)
    }
}
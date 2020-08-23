package org.misuzilla.agqrplayer4tv.component.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.PlayConfirmationActivity
import org.misuzilla.agqrplayer4tv.component.activity.SettingsActivity
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.AgqrDetailsDescriptionPresenter
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.AgqrPlaybackControlsRowPresenter
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.ProgramCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.SettingsCommandScheduleActionCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.CommandAction
import org.misuzilla.agqrplayer4tv.component.widget.CommandActionCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.SettingsCommandScheduleAction
import org.misuzilla.agqrplayer4tv.component.widget.TypedArrayObjectAdapter
import org.misuzilla.agqrplayer4tv.model.*

class AgqrPlaybackOverlayFragment : PlaybackSupportFragment(), CoroutineScope by MainScope() {
    private val playbackPlayer: PlaybackPlayerFragmentBase
        get () = this.parentFragment as PlaybackPlayerFragmentBase

    private val primaryActionsDefinition by lazy {
        mapOf<Action, (Action) -> Unit>(PlaybackControlsRow.PlayPauseAction(this.activity) to { x ->
            if (playbackPlayer.viewModel.isPlaying().value ?: false) {
                playbackPlayer.stop()
            } else {
                playbackPlayer.play()
            }
        })
    }

    private val secondaryActionsDefinition by lazy {
        mapOf<Action, (Action) -> Unit>(Action(1, requireContext().getString(R.string.guidedstep_settings_title)) to { x ->
            playbackPlayer.stop()
            startActivity(SettingsActivity.createIntent(requireContext(), SettingsActivity.SettingsFragmentType.DEFAULT))
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildDetails()

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            if (item is CommandAction) {
                item.execute()
            } else if (item is ProgramCardPresenter.Item) {
                startActivity(PlayConfirmationActivity.createIntent(requireContext(), item.program, false))
            }
        }
    }

    private fun buildDetails() {
        val nowPlaying = NowPlaying.now as NowPlaying
        val playbackPlayer = parentFragment as PlaybackPlayerFragmentBase

        val playbackControlsRow = PlaybackControlsRow(PlaybackControlsRowViewModel(playbackPlayer, nowPlaying)).apply {
            primaryActionsAdapter = ArrayObjectAdapter(ControlButtonPresenterSelector()).apply {
                addAll(0, primaryActionsDefinition.keys)
            }
            secondaryActionsAdapter = ArrayObjectAdapter(ControlButtonPresenterSelector()).apply {
                addAll(0, secondaryActionsDefinition.keys)
            }
        }

        val programCards = TypedArrayObjectAdapter(ProgramCardPresenter())

        // Rowに対するPresenterの登録と実際に表示したいRowを登録する
        val presenterSelector = ClassPresenterSelector().apply {
            // RowのPresenterたちを登録
            addClassPresenter(PlaybackControlsRow::class.java, AgqrPlaybackControlsRowPresenter(AgqrDetailsDescriptionPresenter(viewLifecycleOwner)).apply {
                setSecondaryActionsHidden(true)
                backgroundColor = requireContext().getColor(R.color.accent_dark)
                progressColor = requireContext().getColor(R.color.accent)
                setOnActionClickedListener {
                    val action = if (primaryActionsDefinition.containsKey(it)) primaryActionsDefinition[it] else secondaryActionsDefinition[it]
                    action?.let { x -> x(it) }
                }
            })
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
        val rowsAdapter = TypedArrayObjectAdapter<Row>(presenterSelector).apply {
            // 各Rowを追加する
            // 1. プレイバックコントロール
            add(playbackControlsRow)
            // 2. これからのプログラム
            add(ListRow(HeaderItem(0, requireContext().getString(R.string.playback_rows_upcoming_header)), programCards))
            // 3. 設定
            add(ListRow(HeaderItem(1, requireContext().getString(R.string.playback_rows_settings_header)), TypedArrayObjectAdapter<Action>(ClassPresenterSelector().apply {
                addClassPresenter(CommandAction::class.java, CommandActionCardPresenter())
                addClassPresenter(SettingsCommandScheduleAction::class.java, SettingsCommandScheduleActionCardPresenter())
            }).apply {
                add(SettingsCommandScheduleAction(0, requireContext().getString(R.string.playback_rows_settings_schedules_clear), requireContext().getString(R.string.playback_rows_settings_schedules_clear_description), requireContext().getDrawable(R.drawable.ic_timer_off_white), {
                    startActivity(SettingsActivity.createIntent(requireContext(), SettingsActivity.SettingsFragmentType.CANCEL_ALL_SCHEDULES))
                }))
                add(CommandAction(1, requireContext().getString(R.string.playback_rows_settings_settings), "", requireContext().getDrawable(R.drawable.ic_settings_applications_white), {
                    startActivity(SettingsActivity.createIntent(requireContext(), SettingsActivity.SettingsFragmentType.DEFAULT))
                }))
                add(CommandAction(2, requireContext().getString(R.string.playback_rows_settings_about), "", requireContext().getDrawable(R.drawable.ic_info_outline_white), {
                    startActivity(SettingsActivity.createIntent(requireContext(), SettingsActivity.SettingsFragmentType.ABOUT))
                }))
            }))
        }

        playbackPlayer.viewModel.isPlaying()
            .observe(viewLifecycleOwner, Observer {
                val action = primaryActionsDefinition.keys.first { it is PlaybackControlsRow.PlayPauseAction } as PlaybackControlsRow.PlayPauseAction
                action.index = when (it) {
                    true -> PlaybackControlsRow.PlayPauseAction.PAUSE
                    false -> PlaybackControlsRow.PlayPauseAction.PLAY
                }
                rowsAdapter.notifyArrayItemRangeChanged(0, 1)
            })

        playbackPlayer.viewModel.getElapsedSeconds()
            .observe(viewLifecycleOwner, Observer {
                playbackControlsRow.currentTime = it * 1000
            })

        nowPlaying.getProgram()
            .observe(viewLifecycleOwner, Observer {
                playbackControlsRow.totalTime = (it.end.totalSeconds - it.start.totalSeconds) * 1000

                launch {
                    val mapping = UpdateRecommendation(requireContext()).getProgramImageMappingAsync()
                    val dataset = Timetable(requireContext()).getDatasetAsync()
                    programCards.clear()
                    val now = LogicalDateTime.now
                    programCards.add(0, dataset.data[now.dayOfWeek]!!.filter { it.start > now.time }.map { ProgramCardPresenter.Item(it, mapping) })
                    rowsAdapter.notifyArrayItemRangeChanged(1, programCards.size())
                }

                rowsAdapter.notifyArrayItemRangeChanged(0, 1)
            })

        adapter = rowsAdapter
    }
}
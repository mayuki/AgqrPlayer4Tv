package org.misuzilla.agqrplayer4tv.component.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.app.PlaybackSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.ErrorActivity
import org.misuzilla.agqrplayer4tv.component.activity.PlayConfirmationActivity
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addTo
import org.misuzilla.agqrplayer4tv.infrastracture.extension.observeOnUIThread
import org.misuzilla.agqrplayer4tv.component.activity.SettingsActivity
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.AgqrDetailsDescriptionPresenter
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.AgqrPlaybackControlsRowPresenter
import org.misuzilla.agqrplayer4tv.component.fragment.PlaybackControlsRowViewModel
import org.misuzilla.agqrplayer4tv.component.fragment.guidedstep.AboutSettingGuidedStepFragment
import org.misuzilla.agqrplayer4tv.component.fragment.presenter.ProgramCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.SettingsCommandScheduleActionCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.CommandAction
import org.misuzilla.agqrplayer4tv.component.widget.CommandActionCardPresenter
import org.misuzilla.agqrplayer4tv.component.widget.SettingsCommandScheduleAction
import org.misuzilla.agqrplayer4tv.component.widget.TypedArrayObjectAdapter
import org.misuzilla.agqrplayer4tv.model.*
import org.threeten.bp.LocalDateTime
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class AgqrPlaybackOverlayFragment : PlaybackSupportFragment() {
    private val subscriptions = CompositeSubscription()

    private val playbackPlayer: PlaybackPlayerFragmentBase
        get () = this.parentFragment as PlaybackPlayerFragmentBase

    private val primaryActionsDefinition by lazy {
        mapOf<Action, (Action) -> Unit>(PlaybackControlsRow.PlayPauseAction(this.activity) to { x ->
            if (playbackPlayer.isPlaying.get()) {
                playbackPlayer.stop()
            } else {
                playbackPlayer.play()
            }
        })
    }

    private val secondaryActionsDefinition by lazy {
        mapOf<Action, (Action) -> Unit>(Action(1, context!!.getString(R.string.guidedstep_settings_title)) to { x ->
            playbackPlayer.stop()
            startActivity(SettingsActivity.createIntent(context!!, SettingsActivity.SettingsFragmentType.DEFAULT))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildDetails()

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            if (item is CommandAction) {
                item.execute()
            } else if (item is ProgramCardPresenter.Item) {
                startActivity(PlayConfirmationActivity.createIntent(context!!, item.program, false))
            }
        }
    }

    override fun onDetach() {
        subscriptions.unsubscribe()
        super.onDetach()
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
            addClassPresenter(PlaybackControlsRow::class.java, AgqrPlaybackControlsRowPresenter(AgqrDetailsDescriptionPresenter()).apply {
                setSecondaryActionsHidden(true)
                backgroundColor = ContextCompat.getColor(context!!, R.color.accent_dark)
                progressColor = ContextCompat.getColor(context!!, R.color.accent)
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
            add(ListRow(HeaderItem(0, context!!.getString(R.string.playback_rows_upcoming_header)), programCards))
            // 3. 設定
            add(ListRow(HeaderItem(1, context!!.getString(R.string.playback_rows_settings_header)), TypedArrayObjectAdapter<Action>(ClassPresenterSelector().apply {
                addClassPresenter(CommandAction::class.java, CommandActionCardPresenter())
                addClassPresenter(SettingsCommandScheduleAction::class.java, SettingsCommandScheduleActionCardPresenter())
            }).apply {
                add(SettingsCommandScheduleAction(0, context!!.getString(R.string.playback_rows_settings_schedules_clear), context!!.getString(R.string.playback_rows_settings_schedules_clear_description), context!!.getDrawable(R.drawable.ic_timer_off_white), {
                    startActivity(SettingsActivity.createIntent(context!!, SettingsActivity.SettingsFragmentType.CANCEL_ALL_SCHEDULES))
                }))
                add(CommandAction(1, context!!.getString(R.string.playback_rows_settings_settings), "", context!!.getDrawable(R.drawable.ic_settings_applications_white), {
                    startActivity(SettingsActivity.createIntent(context!!, SettingsActivity.SettingsFragmentType.DEFAULT))
                }))
                add(CommandAction(2, context!!.getString(R.string.playback_rows_settings_about), "", context!!.getDrawable(R.drawable.ic_info_outline_white), {
                    startActivity(SettingsActivity.createIntent(context!!, SettingsActivity.SettingsFragmentType.ABOUT))
                }))
            }))
        }

        playbackPlayer.isPlaying
                .observeOnUIThread()
                .filter { it != null }
                .subscribe {
                    val action = primaryActionsDefinition.keys.first { it is PlaybackControlsRow.PlayPauseAction } as PlaybackControlsRow.PlayPauseAction
                    action.index = when (it) {
                        true -> PlaybackControlsRow.PlayPauseAction.PAUSE
                        false -> PlaybackControlsRow.PlayPauseAction.PLAY
                    }
                    rowsAdapter.notifyArrayItemRangeChanged(0, 1)
                }
                .addTo(subscriptions)

        playbackPlayer.elapsedSeconds
                .observeOnUIThread()
                .filter { it != null }
                .subscribe { playbackControlsRow.currentTime = it * 1000 }
                .addTo(subscriptions)

        nowPlaying.program
                .observeOnUIThread()
                .filter { it != null }
                .subscribe {
                    playbackControlsRow.totalTime = (it.end.totalSeconds - it.start.totalSeconds) * 1000

                    val mappingTask = UpdateRecommendation(context!!).getProgramImageMappingAsync()

                    Timetable(context!!)
                        .getDatasetAsync()
                        .subscribe {
                            programCards.clear()
                            val now = LogicalDateTime.now
                            programCards.add(0, it.data[now.dayOfWeek]!!.filter { it.start > now.time }.map { ProgramCardPresenter.Item(it, mappingTask) })
                            rowsAdapter.notifyArrayItemRangeChanged(1, programCards.size())
                        }

                    rowsAdapter.notifyArrayItemRangeChanged(0, 1)
                }
                .addTo(subscriptions)

        adapter = rowsAdapter
    }
}
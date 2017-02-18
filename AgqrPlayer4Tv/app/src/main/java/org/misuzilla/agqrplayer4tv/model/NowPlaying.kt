package org.misuzilla.agqrplayer4tv.model

import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toRxProperty
import org.misuzilla.agqrplayer4tv.infrastracture.extension.observeOnUIThread
import org.threeten.bp.DayOfWeek
import rx.Observable
import java.util.*
import java.util.concurrent.TimeUnit

class NowPlaying(timetable: Timetable) {
    val program: ReadOnlyRxProperty<TimetableProgram>
    val title: ReadOnlyRxProperty<String>
    val subtitle: ReadOnlyRxProperty<String>
    val body: ReadOnlyRxProperty<String>

    init {
        val rxPropMode = EnumSet.of(RxProperty.Mode.RAISE_LATEST_VALUE_ON_SUBSCRIBE, RxProperty.Mode.DISTINCT_UNTIL_CHANGED)

        program = Observable.just(0L)
                .mergeWith(Observable.interval(10, TimeUnit.SECONDS))
                .flatMap { timetable.getDatasetAsync().toObservable() }
                .doOnError { TimetableProgram.DEFAULT }
                .map { it.data[LogicalDateTime.now.dayOfWeek]?.firstOrNull { it.isPlaying } }
                .map { it ?: TimetableProgram.DEFAULT }
                .observeOnUIThread()
                .toRxProperty(TimetableProgram.DEFAULT, rxPropMode)

        title = program.asObservable().map { it.title }.toRxProperty(rxPropMode)
        subtitle = program.asObservable().map { (it.personality ?: "") + (it.mailAddress?.let { " <${it}>" } ?: "") }.toRxProperty(rxPropMode)
        body = program.asObservable().map { "${it.start.toShortString()}～${it.end.toShortString()}" }.toRxProperty(rxPropMode)
    }

    companion object {
        var now: NowPlaying? = null
            private set

        fun initialize(timetable: Timetable) {
            now = NowPlaying(timetable)
        }
    }
}
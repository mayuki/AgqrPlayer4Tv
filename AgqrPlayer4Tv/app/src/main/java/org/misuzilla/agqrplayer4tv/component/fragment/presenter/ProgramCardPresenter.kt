package org.misuzilla.agqrplayer4tv.component.fragment.presenter

import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.widget.TypedViewPresenter
import org.misuzilla.agqrplayer4tv.infrastracture.extension.toDevicePixel
import org.misuzilla.agqrplayer4tv.model.Reservation
import org.misuzilla.agqrplayer4tv.model.TimetableProgram
import org.misuzilla.agqrplayer4tv.model.UpdateRecommendation

/**
 * これからの番組一覧のカードを表示するためのPresenterクラスです。
 */
class ProgramCardPresenter : TypedViewPresenter<ImageCardView, ProgramCardPresenter.Item>() {

    override fun onUnbindViewHolder(viewHolder: ViewHolder, view: ImageCardView) {
    }

    override fun onCreateView(parent: ViewGroup): ImageCardView {
        return ImageCardView(parent.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ProgramCardPresenterViewHolder(onCreateView(parent))
    }

    override fun onBindViewHolderWithItem(viewHolder: ViewHolder, view: ImageCardView, item: Item) {
        view.apply {
            titleText = item.program.title
            contentText = "${item.program.start.toShortString()}～"
            val width = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_empty_width))
            val height = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_empty_height))
            setMainImageDimensions(width/2, height/2)
            setMainImageScaleType(ImageView.ScaleType.FIT_CENTER)
            setMainImage(context.getDrawable(R.drawable.transparent), false)
        }

/*        Observable.just(Unit)
                .mergeWith(Reservation.instance.onChangeAsObservable.map { Unit })
                .switchMap { UpdateRecommendation(view.context).getCardImageFromProgramAsync(item.mappingTask, item.program).toObservable() }
                .observeOnUIThread()
                .subscribe {
                    view.mainImageView.setImageBitmap(it)
                }
                .addTo((viewHolder as ProgramCardPresenterViewHolder).subscriptions)
                */
    }

    public class Item(val program: TimetableProgram, val mapping: Map<String, String>) {
    }

    public class ProgramCardPresenterViewHolder(view: View) : Presenter.ViewHolder(view) {
    }
}
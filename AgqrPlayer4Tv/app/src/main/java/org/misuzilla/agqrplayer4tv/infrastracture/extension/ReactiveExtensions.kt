package org.misuzilla.agqrplayer4tv.infrastracture.extension

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toRxProperty
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import rx.*
import rx.android.schedulers.AndroidSchedulers
import rx.internal.operators.SingleOperatorZip
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException

fun Subscription.addTo(subscriptions: CompositeSubscription) {
    subscriptions.add(this)
}

fun <T> Observable<T>.observeOnUIThread(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.observeOnUIThread(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> ReadOnlyRxProperty<T>.observeOnUIThread(): Observable<T> {
    return this.asObservable().observeOnUIThread()
}
fun <T> Observable<T>.subscribeOnUIThread(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.subscribeOnUIThread(): Single<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> ReadOnlyRxProperty<T>.subscribeOnUIThread(): Observable<T> {
    return this.asObservable().subscribeOnUIThread()
}

fun <T> ReadOnlyRxProperty<T>.subscribe(observer: (T) -> Unit): Subscription {
    return this.asObservable().subscribe(observer)
}

fun Call.enqueueAndToSingle(): Single<Response> {
    return Observable.create<Response> { observer ->
        this.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                observer.onError(e)
                observer.onCompleted()
            }
            override fun onResponse(call: Call, response: Response) {
                observer.onNext(response)
                observer.onCompleted()
            }
        })
    }.toSingle()
}

// 強参照として持っておかないと into メソッドの中は弱参照なので消え去ってしまうので雑に保持する
// キャプチャしてもSingleごと消えてしまうのはよくわからない…
private val requestingTargetsRefs = mutableSetOf<Target>()

fun RequestCreator.toSingle(): Single<Bitmap> {
    val onSubscribeWithTarget = object : Target, Observable.OnSubscribe<Bitmap> {
        private var subscriber_: Subscriber<in Bitmap>? = null

        override fun call(subscriber: Subscriber<in Bitmap>?) {
            subscriber_ = subscriber
            this@toSingle!!.into(this)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            subscriber_!!.onError(Exception("Fetch failed."))
            subscriber_!!.onCompleted()
        }

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            subscriber_!!.onNext(bitmap)
            subscriber_!!.onCompleted()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }
    }
    requestingTargetsRefs.add(onSubscribeWithTarget)
    return Observable.create(onSubscribeWithTarget)
            .doOnUnsubscribe { requestingTargetsRefs.remove(onSubscribeWithTarget) }
            .subscribeOnUIThread()
            .cache()
            .toSingle()
}

@Suppress("UNCHECKED_CAST")
fun <T> Single<List<Single<T>>>.whenAll(): Single<List<T>> {
    return this.flatMap {
        if (it.size == 0) Single.just(listOf<T>())
        else Single.zip(it, {it.map { it as T }.toList() })
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> List<Single<T>>.whenAll(): Single<List<T>> {
    if (this.size == 0) return Single.just(listOf())
    return Single.zip(this, { it.map { it as T }.toList() })
}

fun <T> Single<T>.cache(): Single<T> {
    return this.toObservable().cache().toSingle()
}

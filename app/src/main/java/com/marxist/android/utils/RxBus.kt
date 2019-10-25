package com.marxist.android.utils

import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun subscribe(@NonNull action: (Any) -> Unit): Disposable {
        return publisher.subscribe(action)
    }

    fun subscribe(@NonNull action: (Any) -> Unit, @NonNull error: (Throwable) -> Unit): Disposable {
        return publisher.subscribe(action, error)
    }

    fun publish(@NonNull message: Any) {
        publisher.onNext(message)
    }
}
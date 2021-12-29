package com.marxist.android.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Singleton

@Singleton
object EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    suspend fun invokeEvent(event: Any) = _events.emit(event)
}
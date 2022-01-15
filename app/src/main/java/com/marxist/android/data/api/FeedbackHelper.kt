package com.marxist.android.data.api

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Khaleel Jageer on 16/01/22.
 */
@Singleton
class FeedbackHelper @Inject constructor(private val feedbackService: FeedbackService) {

    suspend fun postFeedBack(type: String, name: String, email: String, comments: String) =
        feedbackService.postFeedBack(type, name, email, comments)
}
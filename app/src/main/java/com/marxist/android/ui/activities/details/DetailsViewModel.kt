package com.marxist.android.ui.activities.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val okHttpClient: OkHttpClient) : ViewModel() {

    fun triggerArticleLink(link: String) {
        Timber.d(link)
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder().url(link).build()
            okHttpClient.newCall(request).execute()
        }
    }
}
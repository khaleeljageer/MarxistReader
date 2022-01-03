package com.marxist.android.ui.activities.splash

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val preferece: SharedPreferences
) : ViewModel() {

    private var _isReady = MutableLiveData<Boolean>()
    var isReady: MutableLiveData<Boolean> = _isReady

    fun delaySplashScreen() {
        viewModelScope.launch {
            delay(timeMillis = 1000)
            _isReady.postValue(true)
        }
    }
}
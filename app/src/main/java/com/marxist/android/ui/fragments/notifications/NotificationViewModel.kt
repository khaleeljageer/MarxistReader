package com.marxist.android.ui.fragments.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marxist.android.data.repository.GitHubRepository
import com.marxist.android.model.LocalNotifications
import com.marxist.android.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    private val _notifications: MutableLiveData<List<LocalNotifications>> = MutableLiveData()
    val notifications: LiveData<List<LocalNotifications>> = _notifications

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    fun getNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            gitHubRepository.getNotification().collect {
                when (it) {
                    is NetworkResponse.Loading -> {
                        _loading.postValue(true)
                    }
                    is NetworkResponse.Success -> {
                        _loading.postValue(false)
                        _notifications.postValue(it.data.notifications)
                    }
                    is NetworkResponse.Error -> {
                        _loading.postValue(false)
                    }
                }
            }
        }
    }
}
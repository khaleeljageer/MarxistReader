package com.marxist.android.ui.fragments.quotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marxist.android.data.repository.GitHubRepository
import com.marxist.android.model.Quote
import com.marxist.android.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    private val _notifications: MutableLiveData<List<Quote>> = MutableLiveData()
    val notifications: LiveData<List<Quote>> = _notifications

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    fun getNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            gitHubRepository.getQuotes().collect {
                when (it) {
                    is NetworkResponse.Loading -> {
                        _loading.postValue(true)
                    }
                    is NetworkResponse.Success -> {
                        _loading.postValue(false)
                        _notifications.postValue(it.data.quotes)
                    }
                    is NetworkResponse.Error -> {
                        _loading.postValue(false)
                    }
                    NetworkResponse.EmptyResponse -> {
                        _loading.postValue(false)
                    }
                }
            }
        }
    }
}
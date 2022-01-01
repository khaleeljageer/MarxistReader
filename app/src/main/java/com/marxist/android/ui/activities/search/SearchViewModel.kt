package com.marxist.android.ui.activities.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marxist.android.data.model.WPPost
import com.marxist.android.data.repository.WordPressRepository
import com.marxist.android.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: WordPressRepository
) : ViewModel() {

    private val _wpPost: MutableLiveData<List<WPPost>> = MutableLiveData()
    val wpPost: LiveData<List<WPPost>> = _wpPost

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private var pageNumber = 1
    private var perPage = 50

    fun resetQueryParams() {
        pageNumber = 1
        perPage = 50
    }

    fun search(searchKey: String) {
        if (searchKey.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSearch(searchKey, perPage, pageNumber).collect {
                when (it) {
                    is NetworkResponse.Loading -> {
                        _loading.postValue(true)
                    }
                    is NetworkResponse.EmptyResponse -> {
                        _loading.postValue(false)
                    }
                    is NetworkResponse.Success -> {
                        perPage += 50
                        pageNumber += 1
                        _loading.postValue(false)
                        _wpPost.postValue(it.data)
                    }
                    is NetworkResponse.Error -> {
                        _loading.postValue(false)
                        _errorMessage.postValue(it.message)
                    }
                }
            }
        }
    }
}
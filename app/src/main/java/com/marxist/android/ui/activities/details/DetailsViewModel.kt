package com.marxist.android.ui.activities.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marxist.android.database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(appDatabase: AppDatabase) : ViewModel() {

    fun callJsoup(link: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Jsoup.connect(link)
        }
    }
}
package com.marxist.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.dao.LocalHighlightsDao
import com.marxist.android.database.entities.LocalHighlights

class HighlightViewModel(application: Application) : AndroidViewModel(application) {
    private var highlightsDao: LocalHighlightsDao =
        AppDatabase.getAppDatabase(application).localHighlightsDao()

    private var highLightLiveData: LiveData<MutableList<LocalHighlights>>

    init {
        highLightLiveData = highlightsDao.getAllHighlights()
    }

    fun insert(highlights: LocalHighlights) {
        highlightsDao.insert(highlights)
    }

    fun getHighlights(): LiveData<MutableList<LocalHighlights>> {
        return highLightLiveData
    }

    fun deleteHighlight(article: LocalHighlights) {
        highlightsDao.deleteHighlight(article)
    }
}
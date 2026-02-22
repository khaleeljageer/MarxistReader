package org.cpimtn.marxist.android.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    getSavedPostsFlowUseCase: GetSavedPostsFlowUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
) : ViewModel() {
    private val _feedUiState = MutableStateFlow<SavedFeedUiState>(SavedFeedUiState.Loading)
    val feedUiState: StateFlow<SavedFeedUiState> = _feedUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSavedPostsFlowUseCase()
                .map { feedItems ->
                    when {
                        feedItems.isEmpty() -> SavedFeedUiState.Empty
                        else -> SavedFeedUiState.Success(feedItems)
                    }
                }
                .catch { e ->
                    _feedUiState.value = SavedFeedUiState.Error(e.message ?: "Unknown error")
                }
                .collect { newState ->
                    if (_feedUiState.value !is SavedFeedUiState.Error) {
                        _feedUiState.value = newState
                    }
                }
        }
    }

    fun unsave(postId: Int) {
        viewModelScope.launch {
            unsavePostUseCase(postId)
        }
    }
}

sealed interface SavedFeedUiState {
    data object Loading : SavedFeedUiState
    data class Success(val feedItems: List<FeedItem>) : SavedFeedUiState
    data object Empty : SavedFeedUiState
    data class Error(val message: String) : SavedFeedUiState
}

enum class StateKey { Loading, Content, Empty, Error }

val SavedFeedUiState.stateKey: StateKey
    get() = when (this) {
        is SavedFeedUiState.Loading -> StateKey.Loading
        is SavedFeedUiState.Success -> StateKey.Content
        is SavedFeedUiState.Empty -> StateKey.Empty
        is SavedFeedUiState.Error -> StateKey.Error
    }
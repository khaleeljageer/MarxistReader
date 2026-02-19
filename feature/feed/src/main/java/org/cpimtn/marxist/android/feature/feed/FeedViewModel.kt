package org.cpimtn.marxist.android.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    private val getFeedItemsFlowUseCase: GetFeedItemsFlowUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
) : ViewModel() {

    private val _feedUiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val feedUiState: StateFlow<FeedUiState> = _feedUiState.asStateFlow()

    // ── NEW: Drives PullToRefreshBox indicator ──
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getFeedItemsFlowUseCase(),
                getSavedPostIdsFlowUseCase(),
            ) { feedItems, savedIds ->
                when {
                    feedItems.isEmpty() -> FeedUiState.Empty
                    else -> FeedUiState.Success(
                        feedItems = feedItems,
                        savedPostIds = savedIds,
                    )
                }
            }.catch { e ->
                _feedUiState.value = FeedUiState.Error(e.message ?: "Unknown error")
            }.collect { newState ->
                if (_feedUiState.value !is FeedUiState.Error) {
                    _feedUiState.value = newState
                }
            }
        }
    }

    fun toggleSave(postId: Int) {
        val current = _feedUiState.value
        if (current is FeedUiState.Success) {
            val isSaved = postId in current.savedPostIds
            _feedUiState.value = current.copy(
                savedPostIds = if (isSaved) {
                    current.savedPostIds - postId
                } else {
                    current.savedPostIds + postId
                }
            )
            viewModelScope.launch {
                if (isSaved) unsavePostUseCase(postId)
                else savePostUseCase(postId)
            }
        }
    }

    /**
     * CHANGED: No longer sets FeedUiState.Loading during refresh.
     *
     * Before: refresh() → Loading → content disappears → skeleton shows
     * After:  refresh() → isRefreshing=true → existing content stays visible
     *         → PullToRefreshBox shows indicator on top → done
     *
     * Only the initial load (via init block) shows the Loading/skeleton state.
     * Pull-to-refresh keeps the current list visible.
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                when (val result = syncPostsUseCase()) {
                    is SyncResult.Success -> {
                        if (_feedUiState.value is FeedUiState.Error) {
                            _feedUiState.value = FeedUiState.Loading
                        }
                    }

                    is SyncResult.NetworkError ->
                        _feedUiState.value = FeedUiState.Error(result.message ?: "Network error")

                    is SyncResult.ServerError ->
                        _feedUiState.value = FeedUiState.Error(result.message ?: "Server error")

                    is SyncResult.UnknownError ->
                        _feedUiState.value =
                            FeedUiState.Error(result.cause?.message ?: "Something went wrong")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(
        val feedItems: List<FeedItem>,
        val savedPostIds: Set<Int>,
    ) : FeedUiState

    data object Empty : FeedUiState
    data class Error(val message: String) : FeedUiState
}
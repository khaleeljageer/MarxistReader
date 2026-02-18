package org.cpimtn.marxist.android.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.model.SyncStatus
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSyncStatusUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedItemsFlowUseCase: GetFeedItemsFlowUseCase,
    private val getSyncStatusUseCase: GetSyncStatusUseCase,
    private val getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
) : ViewModel() {

    private val _feedUiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val feedUiState: StateFlow<FeedUiState> = _feedUiState.asStateFlow()

    // ── NEW: Drives PullToRefreshBox indicator ──
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val syncStatus: StateFlow<SyncStatus> =
        getSyncStatusUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SyncStatus()
            )

    val savedPostIds: StateFlow<Set<Int>> =
        getSavedPostIdsFlowUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    init {
        viewModelScope.launch {
            getFeedItemsFlowUseCase()
                .map { feedItems ->
                    when {
                        feedItems.isEmpty() -> FeedUiState.Empty
                        else -> FeedUiState.Success(feedItems)
                    }
                }
                .catch { e ->
                    _feedUiState.value = FeedUiState.Error(e.message ?: "Unknown error")
                }
                .collect { newState ->
                    if (_feedUiState.value !is FeedUiState.Error) {
                        _feedUiState.value = newState
                    }
                }
        }
    }

    fun toggleSave(postId: Int) {
        viewModelScope.launch {
            if (savedPostIds.value.contains(postId)) {
                unsavePostUseCase(postId)
            } else {
                savePostUseCase(postId)
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
                        // Room Flow in init will emit updated list automatically.
                        // If we were in Error state, clear it so the flow can update.
                        if (_feedUiState.value is FeedUiState.Error) {
                            _feedUiState.value = FeedUiState.Loading
                        }
                    }
                    is SyncResult.NetworkError ->
                        _feedUiState.value = FeedUiState.Error(result.message ?: "Network error")
                    is SyncResult.ServerError ->
                        _feedUiState.value = FeedUiState.Error(result.message ?: "Server error")
                    is SyncResult.UnknownError ->
                        _feedUiState.value = FeedUiState.Error(result.cause?.message ?: "Something went wrong")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val feedItems: List<FeedItem>) : FeedUiState
    data object Empty : FeedUiState
    data class Error(val message: String) : FeedUiState
}
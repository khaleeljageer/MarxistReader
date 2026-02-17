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
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.model.SyncResult
import org.cpimtn.marxist.android.domain.model.SyncStatus
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSyncStatusUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTagsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getPostsFlowUseCase: GetPostsFlowUseCase,
    private val getSyncStatusUseCase: GetSyncStatusUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val getTagsFlowUseCase: GetTagsFlowUseCase,
    private val getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
) : ViewModel() {

    private val _feedUiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val feedUiState: StateFlow<FeedUiState> = _feedUiState.asStateFlow()

    val syncStatus: StateFlow<SyncStatus> =
        getSyncStatusUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SyncStatus()
            )

    /** Id → name for resolving post category IDs locally. */
    val categoryNames: StateFlow<Map<Int, String>> =
        getCategoriesFlowUseCase()
            .map { list -> list.associate { it.id to it.name } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    /** Id → name for resolving post tag IDs locally. */
    val tagNames: StateFlow<Map<Int, String>> =
        getTagsFlowUseCase()
            .map { list -> list.associate { it.id to it.name } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    /** Set of post IDs the user has saved (bookmarked). */
    val savedPostIds: StateFlow<Set<Int>> =
        getSavedPostIdsFlowUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    init {
        viewModelScope.launch {
            getPostsFlowUseCase()
                .map { posts ->
                    when {
                        posts.isEmpty() -> FeedUiState.Empty
                        else -> FeedUiState.Success(posts)
                    }
                }
                .catch { e ->
                    _feedUiState.value = FeedUiState.Error(e.message ?: "Unknown error")
                }
                .collect { newState ->
                    // Don't overwrite Error from a failed refresh; let user retry
                    if (_feedUiState.value !is FeedUiState.Error) {
                        _feedUiState.value = newState
                    }
                }
        }
    }

    /** Toggle save state for a post: save if not saved, unsave if saved. */
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
     * User-triggered refresh (pull-to-refresh or retry). One-way: event → use case → state.
     */
    fun refresh() {
        viewModelScope.launch {
            _feedUiState.value = FeedUiState.Loading
            when (val result = syncPostsUseCase()) {
                is SyncResult.Success -> {
                    // Flow (collected in init) will emit updated list from Room; leave state for it to update
                }

                is SyncResult.NetworkError -> _feedUiState.value =
                    FeedUiState.Error(result.message ?: "Network error")

                is SyncResult.ServerError -> _feedUiState.value =
                    FeedUiState.Error(result.message ?: "Server error")

                is SyncResult.UnknownError -> _feedUiState.value =
                    FeedUiState.Error(result.cause?.message ?: "Something went wrong")
            }
        }
    }
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val posts: List<Post>) : FeedUiState
    data object Empty : FeedUiState
    data class Error(val message: String) : FeedUiState
}

package org.cpimtn.marxist.android.feature.feeddetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemByIdFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFeedItemByIdFlowUseCase: GetFeedItemByIdFlowUseCase,
    getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["postId"]) {
        "postId is required"
    }

    /**
     * Optimistic save state.
     * - null = use Room's truth (default)
     * - true/false = temporary override until Room Flow catches up
     *
     * Resets to null when Room emits updated savedPostIds,
     * because combine() re-evaluates and we only use this
     * when it's non-null.
     */
    private val _optimisticSaved = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<ArticleDetailUiState> = combine(
        getFeedItemByIdFlowUseCase(postId),
        getSavedPostIdsFlowUseCase(),
        _optimisticSaved,
    ) { feedItem, savedIds, optimistic ->
        when {
            feedItem == null -> ArticleDetailUiState.NotFound
            else -> {
                // Reset optimistic override once Room confirms
                val roomSaved = postId in savedIds
                val isSaved = optimistic ?: roomSaved

                // Clear override if Room now matches our optimistic value
                if (optimistic != null && optimistic == roomSaved) {
                    _optimisticSaved.value = null
                }

                ArticleDetailUiState.Success(
                    feedItem = feedItem,
                    isSaved = isSaved,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ArticleDetailUiState.Loading,
    )

    fun toggleSave() {
        val current = uiState.value
        if (current !is ArticleDetailUiState.Success) return

        val willSave = !current.isSaved

        // 1. Optimistic UI update — instant
        _optimisticSaved.value = willSave

        // 2. Persist to Room — async
        viewModelScope.launch {
            try {
                if (willSave) savePostUseCase(postId)
                else unsavePostUseCase(postId)
            } catch (_: Exception) {
                // Revert optimistic update on failure
                _optimisticSaved.value = !willSave
            }
        }
    }
}

sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState
    data class Success(
        val feedItem: FeedItem,
        val isSaved: Boolean,
    ) : ArticleDetailUiState

    data object NotFound : ArticleDetailUiState
    data class Error(val message: String) : ArticleDetailUiState
}

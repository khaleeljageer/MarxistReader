package org.cpimtn.marxist.android.feature.feeddetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.FeedItem
import org.cpimtn.marxist.android.domain.model.FontSize
import org.cpimtn.marxist.android.domain.model.HelpTopic
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemByIdFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSeenHelpTopicsUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.MarkHelpSeenUseCase
import org.cpimtn.marxist.android.domain.usecase.RecordArticleReadUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SetFontSizeUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFeedItemByIdFlowUseCase: GetFeedItemByIdFlowUseCase,
    getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
    getSettingsFlowUseCase: GetSettingsFlowUseCase,
    getSeenHelpTopicsUseCase: GetSeenHelpTopicsUseCase,
    private val savePostUseCase: SavePostUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
    private val setFontSizeUseCase: SetFontSizeUseCase,
    private val markHelpSeenUseCase: MarkHelpSeenUseCase,
    private val recordArticleReadUseCase: RecordArticleReadUseCase,
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["postId"]) {
        "postId is required"
    }

    /** Whether the "how to read an article" help sheet is showing. */
    private val _showHelp = MutableStateFlow(false)
    val showHelp: StateFlow<Boolean> = _showHelp.asStateFlow()

    init {
        // Auto-show help the first time an article is opened, then persist so it won't reappear.
        viewModelScope.launch {
            if (HelpTopic.ARTICLE.name !in getSeenHelpTopicsUseCase().first()) {
                _showHelp.value = true
                markHelpSeenUseCase(HelpTopic.ARTICLE)
            }
        }
        // Counts toward the one-time review prompt. The ViewModel is scoped to the article route,
        // so this runs once per article opened rather than on every recomposition.
        viewModelScope.launch { recordArticleReadUseCase() }
    }

    fun onHelpClicked() {
        _showHelp.value = true
    }

    fun dismissHelp() {
        _showHelp.value = false
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
        getSettingsFlowUseCase(),
    ) { feedItem, savedIds, optimistic, settings ->
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
                    fontSize = settings.fontSize,
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

    fun setFontSize(fontSize: FontSize) {
        viewModelScope.launch { setFontSizeUseCase(fontSize) }
    }
}

sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState
    data class Success(
        val feedItem: FeedItem,
        val isSaved: Boolean,
        val fontSize: FontSize,
    ) : ArticleDetailUiState

    data object NotFound : ArticleDetailUiState
    data class Error(val message: String) : ArticleDetailUiState
}

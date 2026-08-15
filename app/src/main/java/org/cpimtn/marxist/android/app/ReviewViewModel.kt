package org.cpimtn.marxist.android.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.usecase.MarkReviewPromptShownUseCase
import org.cpimtn.marxist.android.domain.usecase.ShouldShowReviewPromptUseCase
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    shouldShowReviewPromptUseCase: ShouldShowReviewPromptUseCase,
    private val markReviewPromptShownUseCase: MarkReviewPromptShownUseCase,
) : ViewModel() {

    val shouldPrompt: StateFlow<Boolean> = shouldShowReviewPromptUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun onPromptAttempted() {
        viewModelScope.launch { markReviewPromptShownUseCase() }
    }
}

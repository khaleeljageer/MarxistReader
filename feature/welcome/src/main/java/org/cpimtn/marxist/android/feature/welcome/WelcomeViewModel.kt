package org.cpimtn.marxist.android.feature.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SetWelcomeCompletedUseCase
import javax.inject.Inject

private const val MIN_POSTS_TO_CONTINUE = 50

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    getPostsFlowUseCase: GetPostsFlowUseCase,
    private val setWelcomeCompletedUseCase: SetWelcomeCompletedUseCase,
) : ViewModel() {

    /**
     * True when the posts table has at least 50 records (from background sync).
     * Continue button is enabled only when this is true.
     */
    val canContinue: StateFlow<Boolean> = getPostsFlowUseCase()
        .map { posts -> posts.size >= MIN_POSTS_TO_CONTINUE }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun onContinueClicked(onNavigate: () -> Unit) {
        viewModelScope.launch {
            setWelcomeCompletedUseCase(true)
            onNavigate()
        }
    }
}

package org.cpimtn.marxist.android.feature.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SetWelcomeCompletedUseCase
import org.cpimtn.marxist.core.config.AppConfig
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

    private val _showSkip = MutableStateFlow(false)

    /**
     * True once the first sync has had [AppConfig.Sync.WELCOME_SKIP_DELAY_MS] to land. On a very
     * slow or dead connection [canContinue] may never flip, so the screen offers a way out rather
     * than trapping the user on onboarding — sync keeps running in the background either way.
     */
    val showSkip: StateFlow<Boolean> = _showSkip.asStateFlow()

    init {
        viewModelScope.launch {
            delay(AppConfig.Sync.WELCOME_SKIP_DELAY_MS)
            _showSkip.value = true
        }
    }

    fun onContinueClicked(onNavigate: () -> Unit) {
        viewModelScope.launch {
            setWelcomeCompletedUseCase(true)
            onNavigate()
        }
    }
}

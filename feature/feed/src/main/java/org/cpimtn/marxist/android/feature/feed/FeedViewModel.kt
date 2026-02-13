package org.cpimtn.marxist.android.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    postRepository: PostRepository
) : ViewModel() {
    val feedUiState: StateFlow<FeedUiState> =
        postRepository.getPosts()
            .map { posts ->
                when {
                    posts.isEmpty() -> FeedUiState.Empty
                    else -> FeedUiState.Success(posts)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FeedUiState.Loading
            )
}

sealed interface FeedUiState {
    object Loading : FeedUiState
    data class Success(val posts: List<Post>) : FeedUiState
    object Empty : FeedUiState
    data class Error(val message: String) : FeedUiState
}

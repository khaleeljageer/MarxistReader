package org.cpimtn.marxist.android.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTagsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    getSavedPostsFlowUseCase: GetSavedPostsFlowUseCase,
    getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    getTagsFlowUseCase: GetTagsFlowUseCase,
    private val unsavePostUseCase: UnsavePostUseCase,
) : ViewModel() {

    val savedPosts: StateFlow<List<Post>> =
        getSavedPostsFlowUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val categoryNames: StateFlow<Map<Int, String>> =
        getCategoriesFlowUseCase()
            .map { list -> list.associate { it.id to it.name } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    val tagNames: StateFlow<Map<Int, String>> =
        getTagsFlowUseCase()
            .map { list -> list.associate { it.id to it.name } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    fun unsave(postId: Int) {
        viewModelScope.launch {
            unsavePostUseCase(postId)
        }
    }
}

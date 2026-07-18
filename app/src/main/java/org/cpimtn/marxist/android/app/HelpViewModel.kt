package org.cpimtn.marxist.android.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.model.HelpTopic
import org.cpimtn.marxist.android.domain.usecase.GetSeenHelpTopicsUseCase
import org.cpimtn.marxist.android.domain.usecase.MarkHelpSeenUseCase
import javax.inject.Inject

/**
 * Drives contextual help for the main tab screens: auto-shows a topic's help sheet the first time
 * that screen is visited (persisting a "seen" flag), and reopens it on demand from the help icon.
 */
@HiltViewModel
class HelpViewModel @Inject constructor(
    private val getSeenHelpTopics: GetSeenHelpTopicsUseCase,
    private val markHelpSeen: MarkHelpSeenUseCase,
) : ViewModel() {

    private val _visibleTopic = MutableStateFlow<HelpTopic?>(null)
    val visibleTopic: StateFlow<HelpTopic?> = _visibleTopic.asStateFlow()

    /** Topics already evaluated for auto-show this process, so revisiting a tab won't re-trigger. */
    private val autoShown = mutableSetOf<HelpTopic>()

    /** Called when a screen becomes visible; shows help once if the user hasn't seen it before. */
    fun onScreenShown(topic: HelpTopic) {
        if (!autoShown.add(topic)) return
        viewModelScope.launch {
            val seen = getSeenHelpTopics().first()
            if (topic.name !in seen) {
                _visibleTopic.value = topic
                markHelpSeen(topic)
            }
        }
    }

    /** Called when the user taps the help icon: always reopen this screen's help. */
    fun onHelpClicked(topic: HelpTopic) {
        _visibleTopic.value = topic
    }

    fun dismiss() {
        _visibleTopic.value = null
    }
}

package org.cpimtn.marxist.android.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.usecase.GetWelcomeCompletedUseCase
import org.cpimtn.marxist.navigation.Route
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val getWelcomeCompletedUseCase: GetWelcomeCompletedUseCase,
) : ViewModel() {

    private val _destination = MutableStateFlow<String?>(null)
    val destination: StateFlow<String?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = getWelcomeCompletedUseCase().first()
            _destination.value = if (completed) Route.Main.name else Route.Welcome.name
        }
    }
}

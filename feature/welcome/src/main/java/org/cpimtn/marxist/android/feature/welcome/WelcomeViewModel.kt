package org.cpimtn.marxist.android.feature.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.cpimtn.marxist.android.domain.usecase.SetWelcomeCompletedUseCase
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val setWelcomeCompletedUseCase: SetWelcomeCompletedUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasConnection = MutableStateFlow(true)
    val hasConnection: StateFlow<Boolean> = _hasConnection.asStateFlow()

    init {
        checkConnectionAndFetch()
    }

    fun onRetry() {
        _hasConnection.value = true
        _isLoading.value = true
        checkConnectionAndFetch()
    }

    fun onContinueClicked(onNavigate: () -> Unit) {
        viewModelScope.launch {
            setWelcomeCompletedUseCase(true)
            onNavigate()
        }
    }

    private fun checkConnectionAndFetch() {
        viewModelScope.launch {
            val connected = isNetworkAvailable()
            _hasConnection.value = connected
            if (connected) {
                delay(1500)
            }
            _isLoading.value = false
        }
    }

    private fun isNetworkAvailable() = true
}

package al.bruno.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d
 */

abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect>(initialState: State) :
    ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state
        .onStart {
            onStart()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = initialState
        )

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    // Called from UI
    fun sendEvent(event: Event) {
        viewModelScope.launch {
            handleEvent(event)
        }
    }

    // The event handler lambda — implemented in child ViewModel
    protected abstract suspend fun handleEvent(event: Event)
    protected abstract suspend fun onStart()
    protected val currentState: State get() = _state.value

    protected fun setState(reducer: State.() -> State) {
        _state.value = currentState.reducer()
    }

    protected fun setEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effects.send(builder())
        }
    }
}
package al.bruno.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseReducerViewModel<State : UiState, Event : UiEvent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state
        .onStart { onStart() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = initialState
        )

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    protected val currentState: State get() = _state.value

    /**
     * Child ViewModel implements reducer to handle events
     */
    protected abstract val reducer: Reducer<State, Event, Effect>

    /**
     * Called once when state collection starts
     */
    protected abstract suspend fun onStart()

    /**
     * Sends an event to the reducer, updates state, and triggers effects
     */
    open fun sendEvent(event: Event) {
        viewModelScope.launch {
            val (newState, effect) = reducer.reduce(currentState, event)
            _state.value = newState
            effect?.let { _effects.send(it) }
        }
    }

    /**
     * Updates state directly (use for async operation results)
     */
    protected fun setState(update: State.() -> State) {
        _state.value = currentState.update()
    }

    /**
     * Sends an effect directly (use for async operation results)
     */
    protected fun setEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effects.send(builder())
        }
    }
}
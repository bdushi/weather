package al.bruno.presentation.ui.base

interface Reducer<State : UiState, Event : UiEvent, Effect : UiEffect> {
    fun reduce(state: State, event: Event): Pair<State, Effect?>
}
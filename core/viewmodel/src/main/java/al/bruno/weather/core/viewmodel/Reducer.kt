package al.bruno.weather.core.viewmodel

interface Reducer<State : UiState, Event : UiEvent, Effect : UiEffect> {
    fun reduce(state: State, event: Event): Pair<State, Effect?>
}
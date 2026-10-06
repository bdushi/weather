package al.bruno.weather.core.viewmodel

import kotlinx.coroutines.flow.StateFlow

/** Marker for a screen's state. Rendered by the UI. */
interface UIState

/** Marker for everything the reducer reacts to: UI intents and async results. */
interface UIEvent

/** Marker for one-shot UI reactions (snackbar, navigation). */
interface UIEffect

/**
 * Everything one reducer step produces.
 *
 * @property state the new state
 * @property effects one-shot UI reactions, queued until the UI handles them
 * @property commands work for the executor (the ViewModel); never seen by the UI
 */
data class Next<out S : UIState, out Eff : UIEffect, out Cmd>(
    val state: S,
    val effects: List<Eff> = emptyList(),
    val commands: List<Cmd> = emptyList(),
)

/**
 * The only place state changes. Must be pure: no suspend, no I/O, no clock or randomness.
 * Anything it needs to remember between asking for work and getting the result goes into state.
 */
fun interface Reducer<S : UIState, Ev : UIEvent, Eff : UIEffect, Cmd> {
    fun reduce(state: S, event: Ev): Next<S, Eff, Cmd>
}

/** An effect waiting to be handled by the UI. [id] is unique per ViewModel. */
data class Pending<out Eff : UIEffect>(val id: Long, val effect: Eff)

/** What the UI depends on, so previews and UI tests can pass a fake. */
interface MviHost<S : UIState, Ev : UIEvent, Eff : UIEffect> {
    val state: StateFlow<S>

    /** Effects not yet handled, oldest first. */
    val effects: StateFlow<List<Pending<Eff>>>

    fun sendEvent(event: Ev)

    /** Call only after the effect's work is done (e.g. the snackbar was dismissed). */
    fun effectHandled(id: Long)
}

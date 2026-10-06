package al.bruno.weather.core.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pure-reducer MVI: `(State, Event) -> Next(State, Effects, Commands)`.
 *
 * The [reducer] decides; this class (the executor) does. Subclasses implement [execute] and report
 * results back only through [sendEvent]. There is deliberately no `setState`: every state change goes
 * through the reducer.
 *
 * See MVI.md and docs/architecture/adr/0001-pure-reducer-mvi.md.
 *
 * @param debugChecks when true, every event is reduced twice and the results compared, to catch an
 * impure reducer. Pass `BuildConfig.DEBUG`.
 */
abstract class MviViewModel<S : UIState, Ev : UIEvent, Eff : UIEffect, Cmd>(
    initialState: S,
    private val reducer: Reducer<S, Ev, Eff, Cmd>,
    private val debugChecks: Boolean = false,
) : ViewModel(), MviHost<S, Ev, Eff> {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<S> = _state.asStateFlow()

    // Effects are held as state until the UI confirms them, so none is lost when the screen stops
    // while handling one (see docs/architecture/mvi-patterns/borrowed-ideas.md).
    private val _effects = MutableStateFlow<List<Pending<Eff>>>(emptyList())
    final override val effects: StateFlow<List<Pending<Eff>>> = _effects.asStateFlow()
    private var nextEffectId = 0L

    private val uniqueJobs = mutableMapOf<Any, Job>()

    /**
     * The only way to change state. Main thread only, so events are processed one at a time, in order.
     * A command whose work completes synchronously may send its result event before the remaining
     * commands of the same step run; the reducer still sees every event in order.
     */
    @MainThread
    final override fun sendEvent(event: Ev) {
        val old = _state.value
        val next = try {
            reducer.reduce(old, event).also { first ->
                if (debugChecks) {
                    check(reducer.reduce(old, event) == first) { "Reducer is not pure: two runs differ for $event" }
                }
            }
        } catch (e: Exception) {
            onReducerError(event, old, e)
            return
        }
        _state.value = next.state
        onTransition(event, old, next)
        if (next.effects.isNotEmpty()) {
            _effects.update { queue -> queue + next.effects.map { Pending(nextEffectId++, it) } }
        }
        next.commands.forEach(::execute)
    }

    final override fun effectHandled(id: Long) {
        _effects.update { queue -> queue.filterNot { it.id == id } }
    }

    /** Run the work for [command]. Report back ONLY via [sendEvent]. Never decide, never touch state. */
    protected abstract fun execute(command: Cmd)

    /** Starts [block], cancelling any running work started with the same [key] (switch-latest). Main thread only. */
    protected fun launchUnique(key: Any, block: suspend CoroutineScope.() -> Unit) {
        uniqueJobs[key]?.cancel()
        uniqueJobs[key] = viewModelScope.launch(block = block)
    }

    /** Called after every reduction. Hook for logging and analytics. */
    protected open fun onTransition(event: Ev, old: S, next: Next<S, Eff, Cmd>) {}

    /** A reducer that throws is a bug. Fails fast by default; an override may log and keep [state] instead. */
    protected open fun onReducerError(event: Ev, state: S, error: Exception) {
        throw error
    }
}

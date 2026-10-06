# 04. Pure reducer with commands (Elm-style) ⭐ chosen

| | |
|---|---|
| Family | Pure reducer: Elm → Redux/redux-loop → Mobius → TCA |
| Who decides | The reducer (pure). The executor only does the work |
| In this repo | The target design. See [ADR 0001](../adr/0001-pure-reducer-mvi.md) |
| Status | **Chosen** |

> The code on this page is the **reference design**. It hasn't been compiled yet, and the implementation PR may adjust details. Where the two differ, the code in `core/viewmodel` is authoritative.

## The idea in one line

```
(State, Event) -> Next(State, Effects, Commands)
```

A reducer is a pure function and the **only** place state may change. It *decides*; the ViewModel (the **executor**) *does*.

| | Decides (pure, no I/O) | Does (impure, async) |
|---|---|---|
| Who | **Reducer** | **Executor** (ViewModel) |
| Calls use cases? | No | Yes |
| Changes state? | Yes, and it's the only one that can | Never. It only sends result events |
| Tested with | Plain JUnit | Turbine and fakes |

## The loop

```
            ┌──────────── UI intent (Search "Paris") ───────────┐
            │                                                    ▼
   ┌────────────────┐   State     ┌──────────────────────────────────────┐
   │   Compose UI   │◀────────────│  REDUCER (pure)                      │
   │                │◀── Effects ─│  (State, Event) -> Next(S, Eff, Cmd) │
   └────────────────┘  (snackbar) └──────────────────────────────────────┘
                                         │ Commands          ▲ Result events
                                         ▼ (Fetch)           │ (WeatherLoaded)
                                  ┌──────────────────────────────────────┐
                                  │  EXECUTOR (ViewModel): use cases, DB │
                                  └──────────────────────────────────────┘
```

Two rules make it work:

1. **Results are events too.** The executor never writes state. It sends `WeatherLoaded(...)` back into the reducer.
2. **Side effects are described as data.** The reducer can't call a use case, so it returns `Fetch(...)`, and the executor runs it.

## Three kinds of output

| Output | Goes to | Example | Lifetime |
|---|---|---|---|
| **State** | UI (`StateFlow`) | `loadState = Loading` | Persists |
| **Effect** | UI (pending queue, `StateFlow`) | `ShowError("too short")` | One-shot UI reaction, kept until the UI marks it handled |
| **Command** | Executor (internal) | `Fetch(requestId, "Paris")` | One-shot work order. The UI never sees it |

Effects aren't the "effect-driven logic" anti-pattern (an event produces an effect that produces another event). Effects are for the UI. Commands are a private contract between the reducer and the executor, and the loop always goes back through the reducer.

> Naming trap: Mobius and TCA call our *commands* "effects". In this codebase, **Effect means a UI one-shot** and **Command means work for the executor**.

## Base (`core/viewmodel`)

```kotlin
interface UIState
interface UIEvent
interface UIEffect

data class Next<out S : UIState, out Eff : UIEffect, out Cmd>(
    val state: S,
    val effects: List<Eff> = emptyList(),
    val commands: List<Cmd> = emptyList(),
)

fun interface Reducer<S : UIState, Ev : UIEvent, Eff : UIEffect, Cmd> {
    fun reduce(state: S, event: Ev): Next<S, Eff, Cmd>
}

/** An effect waiting to be handled by the UI. `id` is unique per ViewModel. */
data class Pending<out Eff : UIEffect>(val id: Long, val effect: Eff)

/** What the UI depends on. Compose previews and UI tests can pass a fake. */
interface MviHost<S : UIState, Ev : UIEvent, Eff : UIEffect> {
    val state: StateFlow<S>
    val effects: StateFlow<List<Pending<Eff>>>
    fun sendEvent(event: Ev)
    fun effectHandled(id: Long)
}

abstract class MviViewModel<S : UIState, Ev : UIEvent, Eff : UIEffect, Cmd>(
    initialState: S,
    private val reducer: Reducer<S, Ev, Eff, Cmd>,
) : ViewModel(), MviHost<S, Ev, Eff> {

    private val _state = MutableStateFlow(initialState)
    final override val state: StateFlow<S> = _state.asStateFlow()     // no onStart / WhileSubscribed

    // Effects are held as state until the UI confirms it handled them, so none is lost
    // if the screen stops mid-handling (Google "UI events" guidance).
    private val _effects = MutableStateFlow<List<Pending<Eff>>>(emptyList())
    final override val effects: StateFlow<List<Pending<Eff>>> = _effects.asStateFlow()
    private var nextEffectId = 0L

    private val uniqueJobs = mutableMapOf<Any, Job>()

    /** The only way to change state. Main thread only: events are processed one at a time, in order. */
    final override fun sendEvent(event: Ev) {
        val old = _state.value
        val next = try {
            reducer.reduce(old, event)
        } catch (e: Exception) {
            onReducerError(event, old, e)       // keep the old state
            return
        }
        _state.value = next.state
        onTransition(event, old, next)          // logging / analytics hook
        if (next.effects.isNotEmpty()) {
            _effects.update { queue -> queue + next.effects.map { Pending(nextEffectId++, it) } }
        }
        next.commands.forEach { execute(it) }
    }

    /** Called by the UI after an effect has actually been handled (e.g. the snackbar finished). */
    final override fun effectHandled(id: Long) {
        _effects.update { queue -> queue.filterNot { it.id == id } }
    }

    /** Run the work. Report back ONLY via sendEvent(resultEvent). Never decide, never touch state. */
    protected abstract fun execute(command: Cmd)

    /** Starting work under a key cancels any running work with the same key. */
    protected fun launchUnique(key: Any, block: suspend CoroutineScope.() -> Unit) {
        uniqueJobs[key]?.cancel()
        uniqueJobs[key] = viewModelScope.launch(block = block)
    }

    protected open fun onTransition(event: Ev, old: S, next: Next<S, Eff, Cmd>) {}

    /** A reducer that throws is a bug. Fail fast by default; release builds may log instead. */
    protected open fun onReducerError(event: Ev, state: S, error: Exception) { throw error }
}
```

The pending-effects queue is **delivery bookkeeping, not screen state**. The reducer never sees it and still just returns `effects` in `Next`, so this isn't a hybrid. See [borrowed-ideas, effect delivery](borrowed-ideas.md#effect-delivery-a-queue-held-as-state) for why we didn't use a `Channel`.

What's deliberately **missing**: `setState`, `setEffect`, `open sendEvent` and `onStart`. With no way around the reducer, the rules enforce themselves.

## Weather contract

```kotlin
data class WeatherUIState(
    val query: String = DEFAULT_QUERY,
    val weatherUiModel: WeatherUiModel? = null,
    val forecastUiModel: ForecastUiModel? = null,
    val loadState: LoadState = LoadState.Loading,
    val cacheSearch: List<CacheSearchUiModel> = emptyList(),
    // Reducer bookkeeping (not rendered)
    val requestId: Long = 0,
    val pendingSave: String? = null,
) : UIState

sealed interface WeatherUIEvent : UIEvent {
    // From the UI (intents)
    data object Started : WeatherUIEvent
    data class Search(val query: String) : WeatherUIEvent
    data class OnQueryChange(val query: String) : WeatherUIEvent
    data class OnSelectedItems(val query: String) : WeatherUIEvent
    data object OnClear : WeatherUIEvent
    data object OnRetry : WeatherUIEvent
    data class OnDeleteCacheSearch(val item: CacheSearchUiModel) : WeatherUIEvent

    // From the executor (results: facts about the world)
    data class WeatherLoaded(
        val requestId: Long,
        val cityName: String,
        val weather: WeatherUiModel,
        val forecast: ForecastUiModel,
    ) : WeatherUIEvent
    data class WeatherFailed(val requestId: Long, val message: String?) : WeatherUIEvent
    data class HistoryChanged(val items: List<CacheSearchUiModel>) : WeatherUIEvent
    data class DeleteFailed(val message: String?) : WeatherUIEvent
}

sealed interface WeatherUIEffect : UIEffect {
    data class ShowError(val message: String) : WeatherUIEffect
}

sealed interface WeatherCommand {
    data object ObserveHistory : WeatherCommand
    data class FetchByLocation(val requestId: Long) : WeatherCommand
    data class FetchByQuery(val requestId: Long, val query: String) : WeatherCommand
    data class SaveQuery(val query: String) : WeatherCommand
    data class DeleteQuery(val item: CacheSearchUiModel) : WeatherCommand
}
```

## Reducer: the whole screen's behavior in one file

```kotlin
object WeatherReducer : Reducer<WeatherUIState, WeatherUIEvent, WeatherUIEffect, WeatherCommand> {

    override fun reduce(state: WeatherUIState, event: WeatherUIEvent) = when (event) {

        WeatherUIEvent.Started ->
            state.startFetch(query = null, save = false).let {
                it.copy(commands = listOf(WeatherCommand.ObserveHistory) + it.commands)
            }

        is WeatherUIEvent.Search ->
            validate(event.query)
                ?.let { Next(state, effects = listOf(WeatherUIEffect.ShowError(it))) }
                ?: state.startFetch(event.query.trim(), save = true)          // validated ONCE, here

        is WeatherUIEvent.OnQueryChange -> Next(state.copy(query = event.query))
        is WeatherUIEvent.OnSelectedItems -> state.startFetch(event.query, save = false)
        WeatherUIEvent.OnClear -> state.startFetch(DEFAULT_QUERY, save = false)
        WeatherUIEvent.OnRetry -> state.startFetch(state.query, save = false)

        is WeatherUIEvent.OnDeleteCacheSearch ->
            Next(state, commands = listOf(WeatherCommand.DeleteQuery(event.item)))

        // ---- results ----
        is WeatherUIEvent.WeatherLoaded ->
            if (event.requestId != state.requestId) Next(state)                // stale → ignore
            else Next(
                state = state.copy(
                    loadState = LoadState.Success,
                    query = event.cityName,
                    weatherUiModel = event.weather,
                    forecastUiModel = event.forecast,
                    pendingSave = null,
                ),
                commands = listOfNotNull(state.pendingSave?.let(WeatherCommand::SaveQuery)),
            )

        is WeatherUIEvent.WeatherFailed ->
            if (event.requestId != state.requestId) Next(state)
            else Next(state.copy(loadState = LoadState.Error(event.message), pendingSave = null))

        is WeatherUIEvent.HistoryChanged -> Next(state.copy(cacheSearch = event.items))

        is WeatherUIEvent.DeleteFailed ->
            Next(state, effects = listOf(WeatherUIEffect.ShowError(event.message ?: "Failed to delete")))
    }

    private fun WeatherUIState.startFetch(query: String?, save: Boolean): Next<WeatherUIState, WeatherUIEffect, WeatherCommand> {
        val id = requestId + 1
        return Next(
            state = copy(
                requestId = id,
                loadState = LoadState.Loading,
                query = query ?: this.query,
                pendingSave = if (save) query else null,
            ),
            commands = listOf(
                if (query == null) WeatherCommand.FetchByLocation(id)
                else WeatherCommand.FetchByQuery(id, query)
            ),
        )
    }
}

/** Pure validation: an error message, or null if valid. */
fun validate(query: String): String? = when {
    query.isBlank() -> "Search query cannot be empty"
    query.trim().length < 2 -> "Search query must be at least 2 characters"
    else -> null
}
```

Two things that are hard in Option A and easy here:

- **Races, handled purely.** Each fetch gets a `requestId`, and the reducer ignores results that aren't for the current request. `launchUnique` in the executor only saves work. Correctness lives in the reducer, so it can be tested.
- **"Save only if the search succeeded."** The reducer remembers its intent in `pendingSave` and issues `SaveQuery` when `WeatherLoaded` arrives. The rule is visible and testable.

> General rule: **anything the reducer needs to remember between asking for work and getting the answer goes into state.**

## Executor: deliberately simple

```kotlin
@KoinViewModel
class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getCacheSearchUseCase: GetCacheSearchUseCase,
    private val insertCacheSearchUseCase: InsertCacheSearchUseCase,
    private val deleteCacheSearchUseCase: DeleteCacheSearchUseCase,
    private val locationRepository: LocationRepository,
) : MviViewModel<WeatherUIState, WeatherUIEvent, WeatherUIEffect, WeatherCommand>(
    initialState = WeatherUIState(),
    reducer = WeatherReducer,
) {
    init { sendEvent(WeatherUIEvent.Started) }      // runs once per ViewModel

    override fun execute(command: WeatherCommand) {
        when (command) {
            WeatherCommand.ObserveHistory -> launchUnique(WeatherCommand.ObserveHistory) {
                getCacheSearchUseCase().collect { sendEvent(WeatherUIEvent.HistoryChanged(it.toCacheSearchUiModelList())) }
            }

            is WeatherCommand.FetchByLocation ->
                locationRepository.fetchLocation { loc ->
                    fetch(command.requestId, mapOf("lat" to loc.lat.toString(), "lon" to loc.lon.toString()))
                }

            is WeatherCommand.FetchByQuery -> fetch(command.requestId, mapOf("q" to command.query))

            is WeatherCommand.SaveQuery -> viewModelScope.launch {
                insertCacheSearchUseCase(CacheSearch(id = 0, query = command.query))
            }

            is WeatherCommand.DeleteQuery -> viewModelScope.launch {
                try {
                    deleteCacheSearchUseCase(CacheSearch(id = command.item.id, query = command.item.query))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    sendEvent(WeatherUIEvent.DeleteFailed(e.message))
                }
            }
        }
    }

    private fun fetch(requestId: Long, params: Map<String, String>) = launchUnique(FETCH) {
        val weather = async { getWeatherUseCase(params) }
        val forecast = async { getForecastUseCase(params) }
        val w = weather.await()
        val f = forecast.await()
        sendEvent(
            if (w is Result.Success && f is Result.Success) {
                WeatherUIEvent.WeatherLoaded(requestId, w.data.name, w.data.toWeatherUiModel(), f.data.toForecastUiModel())
            } else {
                WeatherUIEvent.WeatherFailed(requestId, (w as? Result.Error)?.error ?: (f as? Result.Error)?.error)
            }
        )
    }

    private companion object { const val FETCH = "fetch" }
}
```

The executor contains no business-rule `if`s. It translates commands into use-case calls and results into events.

## Screen

```kotlin
@Composable
fun WeatherRoute(host: MviHost<WeatherUIState, WeatherUIEvent, WeatherUIEffect> = koinViewModel<WeatherViewModel>()) {
    val state by host.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    HandleEffects(host) { effect ->
        when (effect) {
            is WeatherUIEffect.ShowError -> snackbar.showSnackbar(effect.message)   // suspends until dismissed
        }
    }

    WeatherScreen(state = state, onEvent = host::sendEvent, snackbarHostState = snackbar)
}
```

`HandleEffects` lives in `core/viewmodel` (Compose):

```kotlin
/** Handles pending effects one at a time, only while STARTED, and confirms each one after it finishes. */
@Composable
fun <Eff : UIEffect> HandleEffects(host: MviHost<*, *, Eff>, onEffect: suspend (Eff) -> Unit) {
    val handler by rememberUpdatedState(onEffect)
    val owner = LocalLifecycleOwner.current
    val pending by host.effects.collectAsStateWithLifecycle()
    val current = pending.firstOrNull() ?: return

    LaunchedEffect(current.id, owner) {
        var done = false
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (!done) {
                handler(current.effect)         // if the screen stops here, this is cancelled...
                done = true
                host.effectHandled(current.id)  // ...so the effect stays queued and runs again on return
            }
        }
    }
}
```

An effect that's interrupted (for example, a snackbar still showing when the app goes to the background) is shown again when the screen comes back. A handled effect is never shown twice.

## Tests: plain JUnit, comparing the whole `Next`

Compare the **entire** `Next`, not selected fields. Then an unexpected extra command or effect fails the test. This mirrors TCA's exhaustive `TestStore`.

```kotlin
class WeatherReducerTest {

    @Test fun `valid search starts a fetch and remembers to save`() {
        val next = WeatherReducer.reduce(WeatherUIState(), WeatherUIEvent.Search("Paris"))

        assertEquals(
            Next(
                state = WeatherUIState(query = "Paris", loadState = LoadState.Loading, requestId = 1, pendingSave = "Paris"),
                commands = listOf(WeatherCommand.FetchByQuery(requestId = 1, query = "Paris")),
            ),
            next,
        )
    }

    @Test fun `short query shows error and does not fetch`() {
        val state = WeatherUIState()
        assertEquals(
            Next(state, effects = listOf(WeatherUIEffect.ShowError("Search query must be at least 2 characters"))),
            WeatherReducer.reduce(state, WeatherUIEvent.Search("P")),
        )
    }

    @Test fun `successful load saves the pending query`() {
        val loading = WeatherUIState(requestId = 1, pendingSave = "Paris")
        val next = WeatherReducer.reduce(loading, WeatherUIEvent.WeatherLoaded(1, "Paris", weather, forecast))
        assertEquals(listOf(WeatherCommand.SaveQuery("Paris")), next.commands)
    }

    @Test fun `stale result is ignored`() {
        val state = WeatherUIState(requestId = 2, loadState = LoadState.Loading)
        assertEquals(Next(state), WeatherReducer.reduce(state, WeatherUIEvent.WeatherFailed(requestId = 1, message = "boom")))
    }
}
```

These tests need no `runTest`, dispatchers, Koin or mocks.

## Pitfalls

- **Never put `suspend`, `viewModelScope` or use cases in the reducer.** If you're tempted to, return a command.
- **The executor must not read `state.value` to make decisions.** Put what it needs in the command (`requestId`, `query`).
- **Don't let the UI send result events.** Keep the results section separate in the sealed interface, and don't add result events to UI callbacks.
- **Reducers grow.** Split by area (`searchReducer`, `historyReducer`) and combine them. From outside it's still one function.
- **Simple screens:** a reducer that returns no commands costs about as much as Option A. Don't add ceremony you don't need.

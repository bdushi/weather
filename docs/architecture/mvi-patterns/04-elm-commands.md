# 04. Pure reducer with commands (Elm-style) ⭐ chosen

| | |
|---|---|
| Family | Pure reducer: Elm → Redux/redux-loop → Mobius → TCA |
| Who decides | The reducer (pure). The executor only does the work |
| In this repo | The target design. See [ADR 0001](../adr/0001-pure-reducer-mvi.md) |
| Status | **Chosen** |

> The code on this page is copied from the implementation in commit `449a4b2` (branch `feature/mvi-pure-reducer`). It compiles, and its tests pass. If the code changes later, the code is authoritative.

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
                                         ▼ (FetchWeather)    │ (WeatherLoaded)
                                  ┌──────────────────────────────────────┐
                                  │  EXECUTOR (ViewModel): use cases, DB │
                                  └──────────────────────────────────────┘
```

Two rules make it work:

1. **Results are events too.** The executor never writes state. It sends `WeatherLoaded(...)` back into the reducer.
2. **Side effects are described as data.** The reducer can't call a use case, so it returns `FetchWeather(...)`, and the executor runs it.

## Three kinds of output

| Output | Goes to | Example | Lifetime |
|---|---|---|---|
| **State** | UI (`StateFlow`) | `loadState = Loading` | Persists |
| **Effect** | UI (pending queue, `StateFlow`) | `ShowError("too short")` | One-shot UI reaction, kept until the UI marks it handled |
| **Command** | Executor (internal) | `FetchWeather(requestId, City("Paris"))` | One-shot work order. The UI never sees it |

Effects aren't the "effect-driven logic" anti-pattern (an event produces an effect that produces another event). Effects are for the UI. Commands are a private contract between the reducer and the executor, and the loop always goes back through the reducer.

> Naming trap: Mobius and TCA call our *commands* "effects". In this codebase, **Effect means a UI one-shot** and **Command means work for the executor**.

## Base: `core/viewmodel`

`MviContract.kt` holds the types every screen shares:

```kotlin
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
```

`MviViewModel.kt` is the executor base:

```kotlin
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
```

What's deliberately **missing**: `setState`, `setEffect`, `open sendEvent` and `onStart`. With no way around the reducer, the rules enforce themselves.

The pending-effects queue is **delivery bookkeeping, not screen state**. The reducer never sees it and still just returns `effects` in `Next`, so this isn't a hybrid. See [borrowed-ideas, effect delivery](borrowed-ideas.md#effect-delivery-a-queue-held-as-state) for why we didn't use a `Channel`.

`debugChecks` comes from the feature module's `BuildConfig.DEBUG`. `presentation/weather` sets `buildFeatures.buildConfig = true` for this, because `core/viewmodel` can't see the app's `BuildConfig`.

## Weather contract

State. `requestId` and `pendingSave` are bookkeeping the reducer needs; the UI doesn't render them:

```kotlin
data class WeatherUIState(
    val query: String = DEFAULT_QUERY,
    val weatherUiModel: WeatherUiModel? = null,
    val forecastUiModel: ForecastUiModel? = null,
    val loadState: LoadState = LoadState.Loading,
    val cacheSearch: List<CacheSearchUiModel> = emptyList(),
    // Reducer bookkeeping, not rendered
    /** Id of the latest fetch; results for any other id are stale and ignored. */
    val requestId: Long = 0,
    /** Query to save to history once the current fetch succeeds. */
    val pendingSave: String? = null,
) : UIState

const val DEFAULT_QUERY = "London"
```

Events. Intents come first, then results. The UI must never send a result event:

```kotlin
sealed interface WeatherUIEvent : UIEvent {

    // ---- Intents: sent by the UI (Started is sent by the ViewModel's init) ----

    data object Started : WeatherUIEvent
    data class Search(val query: String) : WeatherUIEvent
    data class OnQueryChange(val query: String) : WeatherUIEvent
    data class OnSelectedItems(val query: String) : WeatherUIEvent
    data object OnClear : WeatherUIEvent
    data object OnRetry : WeatherUIEvent
    data class OnDeleteCacheSearch(val item: CacheSearchUiModel) : WeatherUIEvent

    // ---- Results: sent only by the executor (WeatherViewModel), never by the UI ----

    data class LocationResolved(val requestId: Long, val lat: Double, val lon: Double) : WeatherUIEvent
    data class WeatherLoaded(
        val requestId: Long,
        val cityName: String,
        val weather: WeatherUiModel,
        val forecast: ForecastUiModel,
    ) : WeatherUIEvent
    data class WeatherFailed(val requestId: Long, val message: String?) : WeatherUIEvent
    data class HistoryChanged(val items: List<CacheSearchUiModel>) : WeatherUIEvent
    data class HistoryFailed(val message: String?) : WeatherUIEvent
    data class DeleteFailed(val item: CacheSearchUiModel, val message: String?) : WeatherUIEvent
}
```

Effects:

```kotlin
sealed interface WeatherUIEffect : UIEffect {
    data class ShowToast(val message: String) : WeatherUIEffect
    data class ShowError(val message: String) : WeatherUIEffect
}
```

Commands. `WeatherTarget` keeps API parameter names (`q`, `lat`, `lon`) out of the reducer; the executor maps them:

```kotlin
/** Work the reducer asks the executor (WeatherViewModel) to do. Never seen by the UI. */
sealed interface WeatherCommand {
    data object ObserveHistory : WeatherCommand
    data class ResolveLocation(val requestId: Long) : WeatherCommand
    data class FetchWeather(val requestId: Long, val target: WeatherTarget) : WeatherCommand
    data class SaveQuery(val query: String) : WeatherCommand
    data class DeleteQuery(val item: CacheSearchUiModel) : WeatherCommand
}

sealed interface WeatherTarget {
    data class City(val name: String) : WeatherTarget
    data class Coordinates(val lat: Double, val lon: Double) : WeatherTarget
}
```

## Reducer: the whole screen's behavior in one file

```kotlin
private typealias WeatherNext = Next<WeatherUIState, WeatherUIEffect, WeatherCommand>

/** The weather screen's complete behavior. Pure: no I/O, no coroutines. */
object WeatherReducer : Reducer<WeatherUIState, WeatherUIEvent, WeatherUIEffect, WeatherCommand> {

    override fun reduce(state: WeatherUIState, event: WeatherUIEvent): WeatherNext = when (event) {

        WeatherUIEvent.Started -> {
            val id = state.requestId + 1
            Next(
                state = state.copy(requestId = id, loadState = LoadState.Loading, pendingSave = null),
                commands = listOf(WeatherCommand.ObserveHistory, WeatherCommand.ResolveLocation(id)),
            )
        }

        is WeatherUIEvent.Search -> {
            val query = event.query.trim()
            when (val error = validate(query)) {
                null -> state.startFetch(query, save = true)
                else -> Next(state, effects = listOf(WeatherUIEffect.ShowError(error)))
            }
        }

        is WeatherUIEvent.OnQueryChange -> Next(state.copy(query = event.query))
        is WeatherUIEvent.OnSelectedItems -> state.startFetch(event.query, save = false)
        WeatherUIEvent.OnClear -> state.startFetch(DEFAULT_QUERY, save = true)
        WeatherUIEvent.OnRetry -> state.startFetch(state.query, save = false)

        is WeatherUIEvent.OnDeleteCacheSearch -> {
            // Optimistic: remove now; DeleteFailed puts it back.
            val remaining = state.cacheSearch.filterNot { it.id == event.item.id }
            Next(
                state = state.copy(cacheSearch = remaining),
                effects = if (remaining.isEmpty()) listOf(WeatherUIEffect.ShowToast("All search history cleared")) else emptyList(),
                commands = listOf(WeatherCommand.DeleteQuery(event.item)),
            )
        }

        // ---- results ----

        is WeatherUIEvent.LocationResolved ->
            if (event.requestId != state.requestId) Next(state)                       // a newer fetch started
            else Next(
                state,
                commands = listOf(
                    WeatherCommand.FetchWeather(event.requestId, WeatherTarget.Coordinates(event.lat, event.lon))
                ),
            )

        is WeatherUIEvent.WeatherLoaded ->
            if (event.requestId != state.requestId) Next(state)                       // stale -> ignore
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

        is WeatherUIEvent.HistoryFailed ->
            Next(state, effects = listOf(WeatherUIEffect.ShowError("Failed to load search history: ${event.message}")))

        is WeatherUIEvent.DeleteFailed -> Next(
            state = state.copy(
                cacheSearch = if (state.cacheSearch.any { it.id == event.item.id }) state.cacheSearch
                else state.cacheSearch + event.item,
            ),
            effects = listOf(WeatherUIEffect.ShowError("Failed to delete search: ${event.message}")),
        )
    }

    /** Starts a new fetch by city name; any in-flight fetch becomes stale. */
    private fun WeatherUIState.startFetch(query: String, save: Boolean): WeatherNext {
        val id = requestId + 1
        return Next(
            state = copy(
                requestId = id,
                query = query,
                loadState = LoadState.Loading,
                pendingSave = if (save) query else null,
            ),
            commands = listOf(WeatherCommand.FetchWeather(id, WeatherTarget.City(query))),
        )
    }
}

/** Returns an error message for an invalid (already trimmed) query, or null if it's valid. */
internal fun validate(query: String): String? = when {
    query.isEmpty() -> "Search query cannot be empty"
    query.length < 2 -> "Search query must be at least 2 characters"
    else -> null
}
```

Three things that are hard in Option A and easy here:

- **Races, handled purely.** Each fetch gets a `requestId`, and the reducer ignores results that aren't for the current request. `launchUnique` in the executor only saves work. Correctness lives in the reducer, so it can be tested.
- **A slow location fix can't overwrite a search.** Location is asked for with `ResolveLocation(id)`, and the answer comes back as `LocationResolved(id, …)`. If the user has searched in the meantime, `requestId` has moved on and the reducer ignores it. The executor never fetches by coordinates on its own.
- **"Save only if the search succeeded."** The reducer remembers its intent in `pendingSave` and issues `SaveQuery` when `WeatherLoaded` arrives. The rule is visible and testable.

> General rule: **anything the reducer needs to remember between asking for work and getting the answer goes into state.**

## Executor: deliberately simple

```kotlin
/**
 * Executor for the weather screen. Every decision lives in [WeatherReducer]; this class only turns
 * [WeatherCommand]s into use-case calls and their results into [WeatherUIEvent]s.
 */
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
    debugChecks = BuildConfig.DEBUG,
) {

    init {
        sendEvent(WeatherUIEvent.Started)
    }

    override fun execute(command: WeatherCommand) {
        when (command) {
            WeatherCommand.ObserveHistory -> launchUnique(WeatherCommand.ObserveHistory) {
                getCacheSearchUseCase()
                    .catch { e -> sendEvent(WeatherUIEvent.HistoryFailed(e.message)) }
                    .collect { sendEvent(WeatherUIEvent.HistoryChanged(it.toCacheSearchUiModelList())) }
            }

            is WeatherCommand.ResolveLocation -> try {
                locationRepository.fetchLocation { coord ->
                    sendEvent(WeatherUIEvent.LocationResolved(command.requestId, lat = coord.lat, lon = coord.lon))
                }
            } catch (e: SecurityException) {
                sendEvent(WeatherUIEvent.WeatherFailed(command.requestId, e.message))
            }

            is WeatherCommand.FetchWeather -> fetchWeather(command)

            is WeatherCommand.SaveQuery -> viewModelScope.launch {
                try {
                    insertCacheSearchUseCase(CacheSearch(id = 0, query = command.query))
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Not critical for the user: the search itself succeeded.
                }
            }

            is WeatherCommand.DeleteQuery -> viewModelScope.launch {
                try {
                    deleteCacheSearchUseCase(CacheSearch(id = command.item.id, query = command.item.query))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    sendEvent(WeatherUIEvent.DeleteFailed(command.item, e.message))
                }
            }
        }
    }

    private fun fetchWeather(command: WeatherCommand.FetchWeather) = launchUnique(FETCH_WEATHER) {
        val params = command.target.toParams()
        val event = try {
            val (weather, forecast) = coroutineScope {
                val weather = async { getWeatherUseCase(params) }
                val forecast = async { getForecastUseCase(params) }
                weather.await() to forecast.await()
            }
            if (weather is Result.Success && forecast is Result.Success) {
                WeatherUIEvent.WeatherLoaded(
                    requestId = command.requestId,
                    cityName = weather.data.name,
                    weather = weather.data.toWeatherUiModel(),
                    forecast = forecast.data.toForecastUiModel(),
                )
            } else {
                WeatherUIEvent.WeatherFailed(
                    requestId = command.requestId,
                    message = (weather as? Result.Error)?.error ?: (forecast as? Result.Error)?.error,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            WeatherUIEvent.WeatherFailed(command.requestId, e.message)
        }
        sendEvent(event)
    }

    private fun WeatherTarget.toParams(): Map<String, String> = when (this) {
        is WeatherTarget.City -> mapOf("q" to name)
        is WeatherTarget.Coordinates -> mapOf("lat" to lat.toString(), "lon" to lon.toString())
    }

    private companion object {
        const val FETCH_WEATHER = "fetchWeather"
    }
}
```

The executor contains no business-rule `if`s. It translates commands into use-case calls and results into events. Note the `coroutineScope { async … }`: if either call throws, the exception is caught here and becomes `WeatherFailed`, instead of cancelling the ViewModel's job.

## Screen

`WeatherScreen` gets its own `SnackbarHost`, because the activity's `Scaffold` doesn't have one:

```kotlin
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = koinViewModel(),
) {
    val weatherUIState by weatherViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // One-shot effects: handled while STARTED, confirmed only after the snackbar is dismissed
    HandleEffects(weatherViewModel) { effect ->
        when (effect) {
            is WeatherUIEffect.ShowError ->
                snackbarHostState.showSnackbar(message = effect.message, duration = SnackbarDuration.Long)

            is WeatherUIEffect.ShowToast ->
                snackbarHostState.showSnackbar(message = effect.message, duration = SnackbarDuration.Short)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        WeatherComponent(
            weatherUIState = weatherUIState,
            processWeatherUIEvent = weatherViewModel::sendEvent,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
```

`HandleEffects` lives in `presentation/ui`, the shared Compose module, because `core/viewmodel` has no Compose:

```kotlin
/**
 * Handles [host]'s pending effects one at a time, only while the screen is at least STARTED, and
 * confirms each with [MviHost.effectHandled] after [onEffect] returns.
 *
 * If the screen stops while [onEffect] is suspended (e.g. a snackbar is showing), the effect stays
 * queued and is handled again when the screen comes back. A confirmed effect is never handled twice.
 * Navigation handlers must navigate before returning.
 */
@Composable
fun <Eff : UIEffect> HandleEffects(
    host: MviHost<*, *, Eff>,
    onEffect: suspend (Eff) -> Unit,
) {
    val handler by rememberUpdatedState(onEffect)
    val lifecycleOwner = LocalLifecycleOwner.current
    val pending by host.effects.collectAsStateWithLifecycle()
    val current = pending.firstOrNull() ?: return

    LaunchedEffect(current.id, lifecycleOwner) {
        var handled = false
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (!handled) {
                handler(current.effect)
                handled = true
                host.effectHandled(current.id)
            }
        }
    }
}
```

## Tests: plain JUnit, comparing the whole `Next`

Compare the **entire** `Next`, not selected fields. Then an unexpected extra command or effect fails the test. This mirrors TCA's exhaustive `TestStore`. The typed `next(...)` helper is needed because `assertEquals(Any?, Any?)` gives Kotlin nothing to infer `Next`'s type arguments from.

Excerpt from `WeatherReducerTest` (21 tests in total):

```kotlin
class WeatherReducerTest {

    private val weather = mockk<WeatherUiModel>()
    private val forecast = mockk<ForecastUiModel>()

    private fun reduce(state: WeatherUIState, event: WeatherUIEvent) = WeatherReducer.reduce(state, event)

    /** Typed Next, so assertEquals can infer the type arguments. */
    private fun next(
        state: WeatherUIState,
        effects: List<WeatherUIEffect> = emptyList(),
        commands: List<WeatherCommand> = emptyList(),
    ) = Next(state, effects, commands)

    @Test
    fun `valid search trims, starts a fetch and remembers to save`() {
        assertEquals(
            next(
                state = WeatherUIState(query = "Paris", loadState = LoadState.Loading, requestId = 1, pendingSave = "Paris"),
                commands = listOf(WeatherCommand.FetchWeather(1, WeatherTarget.City("Paris"))),
            ),
            reduce(WeatherUIState(), WeatherUIEvent.Search("  Paris ")),
        )
    }

    @Test
    fun `one-character search shows an error and does not fetch`() {
        val state = WeatherUIState()
        assertEquals(
            next(state, effects = listOf(WeatherUIEffect.ShowError("Search query must be at least 2 characters"))),
            reduce(state, WeatherUIEvent.Search("P")),
        )
    }

    @Test
    fun `location resolved after the user already searched is ignored`() {
        val state = WeatherUIState(requestId = 2, query = "Paris")
        assertEquals(next(state), reduce(state, WeatherUIEvent.LocationResolved(requestId = 1, lat = 0.0, lon = 0.0)))
    }

    @Test
    fun `Paris then Rome - only Rome is shown and saved`() {
        val afterParis = reduce(WeatherUIState(), WeatherUIEvent.Search("Paris")).state
        val afterRome = reduce(afterParis, WeatherUIEvent.Search("Rome")).state
        val parisArrivesLate = reduce(afterRome, WeatherUIEvent.WeatherLoaded(1, "Paris", weather, forecast))
        val romeArrives = reduce(parisArrivesLate.state, WeatherUIEvent.WeatherLoaded(2, "Rome", weather, forecast))

        assertEquals(next(afterRome), parisArrivesLate)
        assertEquals("Rome", romeArrives.state.query)
        assertEquals(listOf(WeatherCommand.SaveQuery("Rome")), romeArrives.commands)
    }
}
```

The UI models are `mockk()` instances. The reducer only passes them through, so equality by identity is enough.

`MviViewModelTest` (8 tests, `core/viewmodel`) covers the base itself:

- events go through the reducer;
- commands run in order after the new state is set;
- effects are queued with unique ids until handled;
- a throwing reducer leaves state unchanged;
- the debug purity check catches an impure reducer;
- `launchUnique` cancels only work with the same key.

These tests need no Koin and no fake use cases. Only the base class tests need `Dispatchers.setMain`, because they use `viewModelScope`.

## Pitfalls

- **Never put `suspend`, `viewModelScope` or use cases in the reducer.** If you're tempted to, return a command.
- **The executor must not read `state.value` to make decisions.** Put what it needs in the command (`requestId`, `target`).
- **The executor must not chain work by itself.** When a callback returns (for example a location fix), send a result event (`LocationResolved`) and let the reducer issue the next command. Fetching straight from the callback would bypass the stale-request check.
- **Don't let the UI send result events.** Keep the results section separate in the sealed interface, and don't add result events to UI callbacks.
- **Reducers grow.** Split by area (`searchReducer`, `historyReducer`) and combine them. From outside it's still one function.
- **Simple screens:** a reducer that returns no commands costs about as much as Option A. Don't add ceremony you don't need.

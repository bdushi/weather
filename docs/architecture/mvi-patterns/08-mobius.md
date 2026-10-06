# 08. Mobius (Spotify)

| | |
|---|---|
| Family | Pure reducer: Elm's loop almost 1:1 |
| Who decides | The pure `Update` function. Effect handlers are the only impure part |
| Platform | Java/JVM core, plus `mobius-android`, `mobius-rx`/`rx2`/`rx3`, `mobius-coroutines`, `mobius-extras`, `mobius-test`. Not KMP, and no Compose module |
| Version checked | `2.1.2` (Maven Central and tag, 2026-08-07; the GitHub "latest release" page still shows 2.1.1 from 2024-12) · ~1.26k ★ · Apache-2.0 |
| Status | **Inspiration.** The same model as ours, with a ready-made loop runtime |

## Core API (verified from source)

```java
interface Update<M, E, F> { Next<M, F> update(M model, E event); }   // "Implementations of this interface must be pure"
```

- `Next.next(model)`, `Next.next(model, effects)`, `Next.dispatch(effects)`, `Next.noChange()`. **We took the name `Next` from here.**
- `First.first(model[, effects])` and `Init<M, F>`. `MobiusLoop.Builder.init(...)` is **deprecated**; pass initial effects to `startFrom(model, effects)`.
- Effect handlers: `Connectable<F, E>` / `Connection`, `RxMobius.subtypeEffectHandler()`, `MobiusCoroutines.subtypeEffectHandler()`.
- `Mobius.loop(update, effectHandler)`, `MobiusLoop.Controller`, `MobiusAndroid.controller(loopFactory, defaultModel)`. `MobiusLoopViewModel` delivers "View Effects" through a `LiveQueue` only while the lifecycle is active.
- **Naming trap:** a Mobius **Effect `F` is a command** (work for a handler), not a UI one-shot.

## Name mapping

| Elm | Mobius | Ours |
|---|---|---|
| `Model` | `Model` (M) | `WeatherUIState` |
| `Msg` | `Event` (E) | `WeatherUIEvent` (intents + results) |
| `update` | `Update` | `WeatherReducer` |
| `Cmd` | **`Effect` (F)** | `WeatherCommand` |
| runtime | `EffectHandler` + `MobiusLoop` | executor (`MviViewModel.execute`) |
| `init` | `Init` / `First` | the `Started` event |

## Weather example

```kotlin
sealed interface WeatherEffect {                                      // Mobius "Effect" = command
    data class Fetch(val requestId: Long, val query: String) : WeatherEffect
    data class SaveQuery(val query: String) : WeatherEffect
    data class DeleteQuery(val item: CacheSearchUiModel) : WeatherEffect
    data class ShowError(val message: String) : WeatherEffect          // UI one-shots are effects too
}

fun update(model: WeatherUIState, event: WeatherUIEvent): Next<WeatherUIState, WeatherEffect> = when (event) {
    is WeatherUIEvent.Search ->
        validate(event.query)?.let { dispatch(setOf(WeatherEffect.ShowError(it))) }
            ?: model.startFetch(event.query, save = true)
    WeatherUIEvent.OnRetry -> model.startFetch(model.query, save = false)
    is WeatherUIEvent.OnDeleteCacheSearch -> dispatch(setOf(WeatherEffect.DeleteQuery(event.item)))
    is WeatherUIEvent.WeatherLoaded ->
        if (event.requestId != model.requestId) noChange()
        else next(
            model.copy(loadState = LoadState.Success, weatherUiModel = event.weather,
                       forecastUiModel = event.forecast, pendingSave = null),
            setOfNotNull(model.pendingSave?.let(WeatherEffect::SaveQuery)),
        )
    is WeatherUIEvent.WeatherFailed ->
        if (event.requestId != model.requestId) noChange()
        else next(model.copy(loadState = LoadState.Error(event.message), pendingSave = null))
    is WeatherUIEvent.DeleteFailed -> dispatch(setOf(WeatherEffect.ShowError(event.message ?: "Failed to delete")))
    else -> noChange()
}

private fun WeatherUIState.startFetch(query: String, save: Boolean): Next<WeatherUIState, WeatherEffect> {
    val id = requestId + 1
    return next(
        copy(query = query, loadState = LoadState.Loading, requestId = id, pendingSave = if (save) query else null),
        setOf(WeatherEffect.Fetch(id, query)),
    )
}
// Effect handler: runs Fetch/SaveQuery/DeleteQuery and sends WeatherLoaded / WeatherFailed / DeleteFailed back.
// Wiring: Mobius.loop(Update(::update), effectHandler), then MobiusAndroid.controller(loopFactory, WeatherUIState()).
```

It's almost identical to [04](04-elm-commands.md). The only differences are that UI one-shots and work orders share one `Effect` type, and that effects are a `Set`.

## Testing: `mobius-test`

```kotlin
UpdateSpec(::update)
    .given(WeatherUIState())
    .whenEvent(WeatherUIEvent.Search("Paris"))
    .then(assertThatNext(
        hasModel(WeatherUIState(query = "Paris", loadState = LoadState.Loading, requestId = 1, pendingSave = "Paris")),
        hasEffects(WeatherEffect.Fetch(1, "Paris")),
    ))
```

The matchers are `hasModel`, `hasNoModel`, `hasEffects`, `hasNoEffects` and `hasNothing`. There's also `InitSpec`.

## What we take

1. **The `Next` name and shape.**
2. **The Given / When / Then reducer test DSL** (`UpdateSpec`). It's a nice optional layer over our `assertEquals(Next(...), reduce(...))` tests if tests get long.
3. **Effects as a `Set`.** We use `List` deliberately, because the order of commands can matter (e.g. `ObserveHistory` before `Fetch`).
4. **We don't depend on it**: it has a Java-first API, no Compose or KMP support, and slow releases.

## References

- Update: https://spotify.github.io/mobius/reference-guide/update/
- Creating a loop: https://spotify.github.io/mobius/getting-started/creating-a-loop/
- Mobius and Android: https://spotify.github.io/mobius/getting-started/mobius-and-android/
- Pure vs impure functions: https://spotify.github.io/mobius/patterns/pure-vs-impure-functions/

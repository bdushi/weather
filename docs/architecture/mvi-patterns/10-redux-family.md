# 10. The Redux family: Redux, Redux Toolkit, redux-loop, NgRx, ReduxKotlin

All of these have pure reducers. They differ in **where side effects go**.

| Library | Platform | Version checked | Side effects | Status |
|---|---|---|---|---|
| **Redux** | JS/TS | `5.0.1` (2023-12-23) · ~61.5k ★ | Middleware (thunks, listeners) | Stable; `createStore` is `@deprecated` in favor of RTK's `configureStore` |
| **Redux Toolkit** | JS/TS | `2.13.0` (2026-09-29) · ~11.2k ★ | `createAsyncThunk`, `createListenerMiddleware` | Active |
| **redux-loop** | JS | npm `6.2.0` (2021-11-09) · ~1.9k ★ | **Reducer returns `[state, Cmd]`**, Elm-style | Dormant: last code change in 2021, and peer deps only up to Redux 4 |
| **NgRx** | Angular | `22.0.1` (2026-09-10) · ~8.3k ★ | `createEffect`: actions in, actions out | Active |
| **ReduxKotlin** | Kotlin MP | `1.0.0-alpha06` (2026-07-18) · ~510 ★ | Middleware and thunks | Revived in 2026 (alpha); the last stable release is `0.6.x` |

## Redux: the purity rule

> "**Reducers Must Not Have Side Effects.** They must not execute any kind of asynchronous logic (AJAX calls, timeouts, promises), generate random values (`Date.now()`, `Math.random()`), modify variables outside the reducer, or run other code that affects things outside the scope of the reducer function."
> (Redux Style Guide, Priority A)

That's our reducer rule word for word. Redux puts side effects in **middleware**: the reducer doesn't say what should happen, and something outside reacts to actions. The style guide recommends thunks for imperative logic and the listener middleware for reactive logic, and recommends *against* Redux-Saga and Redux-Observable in most cases.

## Redux Toolkit: less boilerplate

```ts
const weatherSlice = createSlice({
  name: 'weather',
  initialState,
  reducers: {
    searchStarted(state, action: PayloadAction<string>) { state.query = action.payload; state.loading = true },
    weatherLoaded(state, action: PayloadAction<Weather>) { state.loading = false; state.weather = action.payload },
  },
})

const selectIsEmpty = createSelector([selectWeather], w => w == null)   // memoized derived state (Reselect)
```

- `createSlice` generates action types and creators from the reducer cases.
- **`createSelector`** (Reselect, re-exported) gives **memoized derived state**.
- `createListenerMiddleware()` provides `startListening` and `cancelActiveListeners()` (takeLatest-style) plus `signal: AbortSignal`.

## redux-loop: Elm commands in Redux

```js
function reducer(state, action) {
  switch (action.type) {
    case 'SEARCH':
      return loop(
        { ...state, loading: true },
        Cmd.run(fetchWeather, { args: [action.query], successActionCreator: weatherLoaded, failActionCreator: weatherFailed })
      )
    case 'WEATHER_LOADED':
      return { ...state, loading: false, weather: action.weather }
  }
}
```

> "Calling the reducer will not cause the effect to run."

This is our `Next(state, commands)` in JavaScript. Commands are **data you can compare** (`expect(cmd).toEqual(Cmd.run(...))`), and there's `Cmd.list(cmds, { sequence, batch })`. It also needs its own `combineReducers`, because the built-in one doesn't understand `loop`. The only cancellation is for timers (`Cmd.clearTimeout`).

## NgRx: reducer, effects and selectors, strictly separated

```ts
export const weatherReducer = createReducer(initialState,
  on(search, (state, { query }) => ({ ...state, query, loading: true })),
  on(weatherLoaded, (state, { weather }) => ({ ...state, loading: false, weather })),
)

search$ = createEffect(() => this.actions$.pipe(
  ofType(search),
  switchMap(({ query }) => this.api.fetch(query).pipe(       // switchMap = cancel previous
    map(weather => weatherLoaded({ weather })),
    catchError(e => of(weatherFailed({ message: e.message }))),
  )),
))
```

- Effects are **actions in, actions out**. Results go back through the reducer, just like our result events.
- **Races are handled by choosing an operator**: `switchMap` (cancel the previous one), `exhaustMap` (ignore new ones while busy), `concatMap` (queue) or `mergeMap` (run in parallel). That's a useful vocabulary for naming `launchUnique` variants.
- Errors must be caught *inside* the flattening operator, or the effect stream stops.
- Testing: `provideMockStore`, `overrideSelector` and `provideMockActions` with marble tests.

## ReduxKotlin: Redux ported to Kotlin

```kotlin
typealias TypedReducer<State, Action> = (state: State, action: Action) -> State
val store = createStore(reducer, WeatherState(), applyMiddleware(createThunkMiddleware()))
```

A direct port: one global store, reducers that return only state, side effects via middleware and thunks. It was revived in 2026 as a monorepo (compose, threadsafe, devtools modules), but `1.0.0` is still alpha. It's interesting as a Kotlin reference, but it doesn't fit per-screen ViewModels.

## What we take

1. **The purity rule, quoted** in our rules (see [ADR 0001](../adr/0001-pure-reducer-mvi.md)).
2. **Commands as comparable data** (redux-loop), which we already do in [04](04-elm-commands.md).
3. **Memoized selectors**: derived values computed from state, not stored in it. In Kotlin these are extension properties on the state (`val WeatherUIState.isEmpty get() = ...`), not extra fields the reducer has to keep in sync.
4. **NgRx's operator vocabulary** for concurrency policy: `launchUnique` is `switchMap`. If needed, add `launchExhaust` (ignore while busy) and plain `launch` (merge).

## References

- Redux style guide, reducers must not have side effects: https://redux.js.org/style-guide/#reducers-must-not-have-side-effects
- Redux Fundamentals, rules of reducers: https://redux.js.org/tutorials/fundamentals/part-3-state-actions-reducers
- RTK `createSlice`: https://redux-toolkit.js.org/api/createSlice · listener middleware: https://redux-toolkit.js.org/api/createListenerMiddleware
- redux-loop: https://github.com/redux-loop/redux-loop (see `docs/api-docs/loop.md` and `cmds.md`)
- NgRx reducers: https://ngrx.io/guide/store/reducers · effects: https://ngrx.io/guide/effects · selectors: https://ngrx.io/guide/store/selectors
- ReduxKotlin: https://github.com/reduxkotlin/redux-kotlin · https://reduxkotlin.org

# 09. The Composable Architecture (TCA), Swift

| | |
|---|---|
| Family | Pure reducer (Elm-style). The reducer returns `Effect<Action>` |
| Who decides | The reducer |
| Platform | Swift, Apple platforms (iOS 16+, macOS 13+, tvOS 16+, watchOS 9+) |
| Version checked | `1.26.2` (2026-08-28); backport `1.23.3` (2026-09-18) · ~15.0k ★ · MIT |
| Status | **Inspiration.** The closest mobile match to our design and the most important one to study |

## Core API (verified from source)

```swift
@Reducer
struct Weather {
  @ObservableState
  struct State: Equatable { var query = ""; var isLoading = false; var weather: WeatherModel? }

  enum Action { case searchTapped(String), weatherResponse(Result<WeatherModel, Error>) }

  @Dependency(\.weatherClient) var weatherClient
  enum CancelID { case search }

  var body: some ReducerOf<Self> {
    Reduce { state, action in                       // (inout State, Action) -> Effect<Action>
      switch action {
      case let .searchTapped(query):
        state.query = query
        state.isLoading = true
        return .run { send in
          await send(.weatherResponse(Result { try await weatherClient.fetch(query) }))
        }
        .cancellable(id: CancelID.search, cancelInFlight: true)   // switch-latest
      case let .weatherResponse(.success(w)):
        state.isLoading = false
        state.weather = w
        return .none
      case .weatherResponse(.failure):
        state.isLoading = false
        return .none
      }
    }
  }
}
```

- `protocol Reducer<State, Action>` with `reduce(into state: inout State, action: Action) -> Effect<Action>`, usually written as `var body: some ReducerOf<Self>` plus `Reduce { state, action in ... }`.
- `Effect<Action>` provides `.none`, `.run { send in ... }`, `.send(_:)`, `.merge`, `.concatenate` and `.map`.
- `.cancellable(id:cancelInFlight:)` and `.cancel(id:)`. `cancelInFlight: true` gives switch-latest. The deprecated `Effect.debounce`/`throttle` are replaced by this pattern.
- `Scope(state:action:) { Child() }` composes child reducers into a parent.
- Dependencies are injected with `@Dependency`, and tests override them with `withDependencies`.

## Testing: `TestStore` is exhaustive by default

```swift
let store = TestStore(initialState: Weather.State()) { Weather() } withDependencies: {
  $0.weatherClient.fetch = { _ in .paris }
}
await store.send(.searchTapped("Paris")) { $0.query = "Paris"; $0.isLoading = true }
await store.receive(\.weatherResponse.success) { $0.isLoading = false; $0.weather = .paris }
```

`exhaustivity` defaults to `.on`. The test **fails** on any state change you didn't assert, any action received but not asserted, or any effect still running when the test ends. `.off(showSkippedAssertions:)` relaxes this.

## Compared with ours

| TCA | Ours ([04](04-elm-commands.md)) |
|---|---|
| `Effect` is a **closure** (`.run { send in }`), so you can't compare it in a test | `Command` is **data**, so you can compare it with `assertEquals` |
| Cancellation is declared in the reducer (`.cancellable(id:)`) | Cancellation in the executor (`launchUnique`), plus `requestId` in state |
| `inout State` mutation | `copy(...)` on an immutable data class |
| `Scope` for composing children | Split reducers by hand (no helper yet) |

## What we take

1. **Exhaustive tests.** Assert the *whole* `Next(state, effects, commands)`, not chosen fields (see the test section in [04](04-elm-commands.md#tests-plain-junit-comparing-the-whole-next)). Later, a small `ReducerTestStore` could also fail on unconsumed result events.
2. **Declare cancellation where the work starts.** TCA keeps the cancel ID next to the effect. Our `launchUnique(key)` is the executor-side version, and `requestId` keeps correctness in the reducer.
3. **Use `Scope` to compose reducers** when one screen's reducer gets big.

## References

- Docs: https://swiftpackageindex.com/pointfreeco/swift-composable-architecture/main/documentation/composablearchitecture
- Testing: https://swiftpackageindex.com/pointfreeco/swift-composable-architecture/main/documentation/composablearchitecture/testingtca
- Cancellation source: https://github.com/pointfreeco/swift-composable-architecture/blob/main/Sources/ComposableArchitecture/Effects/Cancellation.swift
- Releases: https://github.com/pointfreeco/swift-composable-architecture/releases

# MVI Architecture Guide (Android + Jetpack Compose)

> A practical, opinionated guide to implementing **Model–View–Intent (MVI)** on Android using **Jetpack Compose**, **StateFlow**, and **ViewModel**.
>
> This README summarizes multiple valid MVI approaches, explains **why they exist**, highlights **common pitfalls**, and clearly **recommends the best approach** based on real-world usage.

---

## Table of Contents

1. [What is MVI?](#what-is-mvi)
2. [Core Building Blocks](#core-building-blocks)
3. [Unidirectional Data Flow](#unidirectional-data-flow)
4. [Events vs Effects (Critical Distinction)](#events-vs-effects-critical-distinction)
5. [Reducer Role Explained](#reducer-role-explained)
6. [Three Valid MVI Approaches](#three-valid-mvi-approaches)
7. [Recommended Approach ⭐](#recommended-approach-)
8. [Base Architecture](#base-architecture)
9. [UI Integration (Compose)](#ui-integration-compose)
10. [Common Pitfalls](#common-pitfalls)
11. [Best Practices](#best-practices)
12. [Decision Cheat Sheet](#decision-cheat-sheet)

---

## What is MVI?

**MVI (Model–View–Intent)** is a reactive UI architecture that enforces:

- **Single source of truth** (State)
- **Explicit user actions** (Events / Intents)
- **Unidirectional data flow**
- **Predictable and testable state changes**

MVI is especially powerful with **Jetpack Compose**, where UI is a pure function of state.

---

## Core Building Blocks

### 1. State (ViewModel → UI)

Persistent UI data.

```kotlin
data class WeatherUIState(
    val query: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherUiModel? = null,
    val error: String? = null
) : UiState
```

---

### 2. Event (UI → ViewModel)

Represents **user actions**.

```kotlin
sealed class WeatherUIEvent : UiEvent {
    data class Search(val query: String) : WeatherUIEvent()
    data class QueryChanged(val query: String) : WeatherUIEvent()
    data object Retry : WeatherUIEvent()
}
```

---

### 3. Effect (ViewModel → UI)

One-time UI reactions (NOT state).

```kotlin
sealed class WeatherUIEffect : UiEffect {
    data class ShowToast(val message: String) : WeatherUIEffect()
    data class ShowValidationError(val field: String) : WeatherUIEffect()
    data object NavigateBack : WeatherUIEffect()
}
```

---

## Unidirectional Data Flow

```
User Action
    ↓
UI sends Event
    ↓
ViewModel / Reducer
    ↓
State updated
    ↓
UI recomposes
```

Effects are emitted **out-of-band** and consumed once by the UI.

---

## Events vs Effects (Critical Distinction)

### Rule of Thumb

| Question | Use |
|--------|-----|
| Triggered directly by user? | **Event** |
| One-time UI reaction? | **Effect** |
| Persistent UI data? | **State** |

### ❌ Wrong

```kotlin
sealed class UIEffect {
    data class PerformSearch(val query: String) : UIEffect()
}
```

### ✅ Correct

```kotlin
sealed class UIEvent {
    data class Search(val query: String) : UIEvent()
}
```

---

## Reducer Role Explained

A **Reducer** is a **pure function**:

```kotlin
State + Event → New State (+ optional Effect)
```

It must:

- Be synchronous
- Have no side effects
- Be deterministic

---

## Three Valid MVI Approaches

### Approach 1 — State-Only Reducer

```kotlin
fun reduce(state: State, event: Event): State
```

**Pros**
- Very simple
- Easy to learn

**Cons**
- Effects handled outside reducer
- Less declarative

**Best for:** small apps, beginners

---

### Approach 2 — Pair<State, Effect?> ⭐

```kotlin
fun reduce(state: State, event: Event): Pair<State, Effect?>
```

Reducer declares **state changes** and **UI effects**.
Async work happens elsewhere.

**Pros**
- Declarative
- Highly testable
- Clear intent

**Cons**
- Slightly more complex

---

### Approach 3 — Effect-Driven Logic ❌

```kotlin
init {
    effects.collect { effect ->
        // trigger business logic
    }
}
```

**Never use this.**

Creates circular dependencies:

```
Event → Effect → Event → Effect → ...
```

---

## Recommended Approach ⭐

### ✅ **Approach 2: Reducer returns `Pair<State, Effect?>`**

Why?

- Reducer remains pure
- UI effects are declarative
- Async work stays explicit
- Excellent testability
- Clean separation of concerns

---

## Base Architecture

```kotlin
interface Reducer<State : UiState, Event : UiEvent, Effect : UiEffect> {
    fun reduce(state: State, event: Event): Pair<State, Effect?>
}
```

```kotlin
abstract class BaseReducerViewModel<State : UiState, Event : UiEvent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    protected val currentState get() = _state.value

    protected abstract val reducer: Reducer<State, Event, Effect>

    open fun sendEvent(event: Event) {
        val (newState, effect) = reducer.reduce(currentState, event)
        _state.value = newState
        effect?.let { viewModelScope.launch { _effects.send(it) } }
    }

    protected fun setState(block: State.() -> State) {
        _state.value = currentState.block()
    }
}
```

Async work is triggered **after** `sendEvent()`.

---

## UI Integration (Compose)

```kotlin
@Composable
fun Screen(viewModel: MyViewModel) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MyEffect.ShowToast -> { /* show snackbar */ }
            }
        }
    }

    // Render UI from state
}
```

---

## Common Pitfalls

### ❌ Effects as Events
### ❌ Async work in reducer
### ❌ Collecting effects to trigger logic
### ❌ UI holding its own state
### ❌ Multiple StateFlows

---

## Best Practices

- Reducer = pure + synchronous
- Events = user actions only
- Effects = UI reactions only
- State = single source of truth
- Async work in ViewModel, not reducer
- Exhaustive `when` on sealed classes

---

## Decision Cheat Sheet

```
User clicked something?      → Event
Need API / DB call?          → ViewModel async
Need to update UI forever?  → State
Need toast / navigation?    → Effect
Need validation?            → Reducer
```

---

## Final Recommendation

🏆 **Use Approach 2 (`Pair<State, Effect?>`)**

It provides the best balance of:

- Clarity
- Testability
- Explicit intent
- Scalability

---

Happy coding 🚀


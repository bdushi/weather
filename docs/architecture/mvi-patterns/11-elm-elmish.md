# 11. The origin: Elm and Elmish

| | |
|---|---|
| Family | The original pure reducer with commands |
| Platform | Elm compiles to JS for the browser. Elmish is F# on .NET and Fable (JS) |
| Version checked | Elm `0.19.3` (2026-10-02; "There are no language changes"), `0.19.2` (2026-07-06), before that `0.19.1` (2019) · ~7.9k ★ · Elmish NuGet `5.0.2` (2025-07-15) · ~0.9k ★ |
| Status | **Study material.** We'll never use either language, but this is where every pure-reducer design comes from |

## Elm: the Elm Architecture (TEA)

Elm is a small functional language for web front ends. In Elm the pure loop isn't a convention, it's **the only option**: `update` *can't* do I/O.

```elm
init : () -> (Model, Cmd Msg)
update : Msg -> Model -> (Model, Cmd Msg)
subscriptions : Model -> Sub Msg
view : Model -> Html Msg
```

The weather example:

```elm
type Msg
    = Search
    | GotWeather Int (Result Http.Error Weather)

update msg model =
    case msg of
        Search ->
            case validate model.query of
                Just err -> ( { model | error = Just err }, Cmd.none )
                Nothing ->
                    let id = model.requestId + 1
                    in ( { model | loading = True, requestId = id, pendingSave = Just model.query }
                       , fetchWeather id model.query )        -- a Cmd: a description, not a call

        GotWeather id (Ok data) ->
            if id /= model.requestId then ( model, Cmd.none )
            else ( { model | loading = False, weather = Just data, pendingSave = Nothing }
                 , model.pendingSave |> Maybe.map saveQuery |> Maybe.withDefault Cmd.none )

        GotWeather id (Err _) ->
            if id /= model.requestId then ( model, Cmd.none )
            else ( { model | loading = False, error = Just "Failed to load" }, Cmd.none )
```

- A `Cmd` describes work, and the runtime runs it. `Platform.Cmd` only exposes `none`, `batch` and `map`, so you **can't inspect a Cmd** in a test. Elm tests assert on the model.
- **Subscriptions** (`Sub`) are long-running sources, such as `Time.every 1000 Tick`. They're *derived from the model*: when `subscriptions model` stops returning one, it's stopped.
- Cancellation: only for HTTP (`tracker` plus `Http.cancel`).

## Elmish: TEA in F#

```fsharp
init   : 'arg -> 'model * Cmd<'msg>
update : 'msg -> 'model -> 'model * Cmd<'msg>
view   : 'model -> Dispatch<'msg> -> 'view

type Cmd<'msg> = Effect<'msg> list            // Effect<'msg> = Dispatch<'msg> -> unit
Cmd.OfAsync.either fetch query WeatherLoaded WeatherFailed
Cmd.batch [ cmdA; cmdB ]
```

- `Cmd.OfAsync`, `OfTask` and similar, each with `either`, `perform` and `attempt`. These map a success or failure straight to a message, like our result events.
- Since v4, subscriptions take the model and get a `SubId`. They're **started and stopped automatically** as the set of IDs returned for the current model changes.

## Where the ideas went

```
Elm (2012, web language) ── pure update + Cmd loop
 ├─▶ Elmish (F#)
 ├─▶ Redux (JS, 2015) ── pure reducers; side effects moved to middleware
 │    ├─▶ redux-loop ── Elm's Cmd brought back into Redux
 │    ├─▶ NgRx ── effects: actions in → actions out
 │    └─▶ React useReducer ── reducer + useEffect   (→ our pattern 03)
 ├─▶ Mobius (Spotify, JVM) ── Elm's loop almost 1:1, Cmd is called "Effect"
 ├─▶ TCA (Swift) ── reducer returns Effect<Action>
 └─▶ Android "MVI" articles ── usually reducer + "async work happens elsewhere"   (→ our pattern 02)

Different branch (the executor decides, the reducer applies):
 MVIKotlin · adidas MVI · Orbit · Ballast · FlowRedux
```

## What we take

1. **Model-driven subscriptions.** Our `ObserveHistory` is started once by `Started`. Elm's version would be "observe history while `state.x` says so". We don't need that yet; noted for screens with conditional streams.
2. **`Cmd.none` / `Cmd.batch`** are our `commands = emptyList()` / `listOf(a, b)`. There's nothing new to add, which confirms the shape is right.
3. **One caution.** Elm and Elmish commands are opaque functions, so they can't be tested by equality. Keeping our commands as `data class`es is a deliberate improvement.

## References

- The Elm Architecture: https://guide.elm-lang.org/architecture/
- Commands and Subscriptions: https://guide.elm-lang.org/effects/ (HTTP: https://guide.elm-lang.org/effects/http.html · Time/subscriptions: https://guide.elm-lang.org/effects/time.html)
- Elm 0.19.3 release: https://github.com/elm/compiler/releases/tag/0.19.3
- Elmish: https://github.com/elmish/elmish · docs: https://elmish.github.io/elmish/ (`docs/basics.html`, `docs/subscription.html`)

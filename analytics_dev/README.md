# Analytics module

This module is responsible for sending analytic events to specific analytic services.

Currently there are 2 implementations in use: Firebase Analytics and AppsFlyer.

Read below chapters if you want to know how to define your own analytic events and how
the analytics module works.

## Overview

The main goal of this analytics abstraction is to hide the fact that there are 2 different analytics
platforms in use. It also tries to minimize the amount of parameters needed to be passed "manually"
to create an event instance. Additionally and hide the logic that prepares and sets event properties.

The main entry point, through which we can interact with analytics services is `Analytics`
interface. All of the exposed methods don't have a way to manipulate specific analytics services
directly (with 2 exceptions that will accept service specifier as an option). Configuration
for properties targeting different services is moved to event definitions.

All of the old analytics events that were migrated to new implementation are defined inside
the analytics module under the `Events` sealed class hierarchy. This is to allow for composition
and to make use of autocompletion in IDE. In general, there is no obstacle in defining new
events elsewhere, as long as those implement the `Event` interface.

## Defining basic analytic events

The minimal valid event just needs one parameter - event `name`. For example:

```kotlin
data class AppStarted(override val name: String) : Event
```

This type of event doesn't have any properties, and if sent, would be passed to all defined
analytics services in the same form.

If an event has to be passed only to one specific analytics service, this can be
achieved by overriding the `Event.serviceId` value (by default, this is set to null, meaning
all services). Such an event could look like:

```kotlin
data class OnlyForFirebase(override val name: String) : Event {
    override val serviceId = Analytics.FIREBASAE
}
```

Now that we have basic events defined, we can log them using an instance of `Analytics` -
`analytics.logEvent(OnlyForFirebase("event-name"))`.

## Configuring analytic event properties

The example events shown above have very limited use. For most of the real world scenarios,
we need would like to pass some additional data as well. Each analytics event can have 0 or more
properties, that are basically a simple key-value pairs.

There could be various ways of configuring event properties, and often those involve using
complex conditions. To provide most flexible way of configuration and at the same time keep
everything within event definition, `Event` interface offers a custom DSL.

Let's start with a simple example - a screen visited event that should pass the name of the screen
as a property.

```kotlin
data class ScreenVisited(
    override val name: String,
    val screenName: String
) : Event {
    override val configuration: EventConfiguration = {
        set("screen", screenName)
    }
}

analytics.logEvent(ScreenVisited("screen-visited", "home screen"))
```

You might notice that there is some repetition when using the above event - the "ScreenVisited"
event class name and the "screen-visited" name. Also the "screen-visited" identifier doesn't
need to be exposed to the consumers of analytics module.

For simplicity let's introduce a base class, that will make the event definitions a bit shorter.

```kotlin
sealed class MyEvent(
    override val name: String,
    override val configuration: EventConfiguration = {}
) : Event
```

Then our previous example event definition could be just:

```kotlin
class ScreenVisited(screenName: String) : MyEvent("screen-visited", { set("screen", screenName) })
```

When looking at existing event definitions in `Events`, you will notice this approach is used
extensively.

## Automatic properties injection

`ScreenVisited` example is still very simple, so let's make it into something of a real value.
Let's say that along this event, we need to pass properties like screen resolution,
screen orientation, Android version, and optional device name. Sure, those could be just passed
through event constructor, but that would be tedious. Not to mention that most of the time,
those values would be the same, so there is no point in passing them every time.

For that purpose, the event configuration DSL allows for declaring properties without passing its
values.

```kotlin
class ScreenVisited(
    screenName: String
) : MyEvent(
    "screen-visited",
    {
        set("screen-name", screenName)

        require("screen-resolution")
        require("screen-orientation")
        require("android-version")

        optional("device-name")
    }
)
```

There 2 possibilities of declaring a property - `require()` means that we expect the value
of this property to be present, and `optional()` which adds the given property only if its value
is present. In case of required properties, if those are not found, the event will be skipped,
and an error will be logged.

How those property values are provided is explained in further down this guide.

## Service specific event configuration

What if each analytics service has it's own set of properties. This would be also possible to set
in the configuration DSL.

```kotlin
class UserRegistered(
    login: String
) : MyEvent(
    "user-registered",
    {
        set("login-name", login)

        service(Analytics.FIREBASE) {
            set("method", "email")
        }

        service(Analytics.APPS_FLYER) {
            require("android-version")
        }
    }
)
```

Sending this event instance as `UserRegistered("jdoe")` would send an event of name `user-registered`.
For Firebase, it would contain properties `["login-name": "jdoe", "method": "email"]`,
and for AppsFlyer `["login-name": "jdoe", "android-version": "12"]`.

## Altering event names

The old implementation of analytics had one specific use case - some of the Apps Flyer events
had event name prefixed with "af_". To make it work, the `service()` block allows for
event name alteration.

```kotlin
data object LoginSuccess : MyEvent(
    "login-success",
    {
        service(Analytics.APPS_FLYER) {
            name { "af_$it" }
        }
    }
)
```

This way, the event will keep the name of "login-success" in Firebase, but for Apps Flyer
the event would be named "af_login-success".

## Property providers

Last important topic is how the analytics implementation automatically injects properties.
`AnalyticsImpl` takes a set of `PropertyProvider`s. The implementation of such provider needs
2 things - the list of provided property names, and the function that outputs
a `Map<String, String>` with values.

```kotlin
class MyProvider : PropertiesProvider {
    override val providedProperties = setOf("is-debug")

    override val provide = {
        val props = mutableMapOf("is-debug" to BuildConfig.DEBUG.toString())

        val deviceName = getDeviceNameOrNull()
        if (deviceName != null) {
            props["device-name"] = deviceName
        }

        return props.toMap()
    }
}
```

An important thing to know for implementing correctly a properties provider, is that
the `provide` lambda should always return properties with names within the `providedProperties` set,
and it can be a subset of it.

Another one is that property providers should be non blocking. In other words, do not try
to use `runBlocking {}` inside the `provide` lambda to fetch some value from the network.

[clappr](../../index.md) / [io.clappr.player.periodicTimer](../index.md) / [PeriodicTimeElapsedHandler](.)

# PeriodicTimeElapsedHandler

`class PeriodicTimeElapsedHandler : Handler` [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/periodicTimer/PeriodicTimeElapsedHandler.kt#L5)

### Types

| Name | Summary |
|---|---|
| [TimeElapsedRunnable](-time-elapsed-runnable/index.md) | `inner class TimeElapsedRunnable : `[`Runnable`](http://docs.oracle.com/javase/6/docs/api/java/lang/Runnable.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PeriodicTimeElapsedHandler(interval: Long, function: () -> Unit)` |

### Properties

| Name | Summary |
|---|---|
| [function](function.md) | `val function: () -> Unit` |
| [interval](interval.md) | `val interval: Long` |

### Functions

| Name | Summary |
|---|---|
| [cancel](cancel.md) | `fun cancel(): Unit` |
| [start](start.md) | `fun start(): Unit` |

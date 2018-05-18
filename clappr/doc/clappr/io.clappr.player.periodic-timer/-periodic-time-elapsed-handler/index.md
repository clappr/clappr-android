[clappr](../../index.md) / [io.clappr.player.periodicTimer](../index.md) / [PeriodicTimeElapsedHandler](./index.md)

# PeriodicTimeElapsedHandler

`class PeriodicTimeElapsedHandler : `[`Handler`](https://developer.android.com/reference/android/os/Handler.html)

### Types

| Name | Summary |
|---|---|
| [TimeElapsedRunnable](-time-elapsed-runnable/index.md) | `inner class TimeElapsedRunnable : `[`Runnable`](https://developer.android.com/reference/java/lang/Runnable.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PeriodicTimeElapsedHandler(interval: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, function: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [function](function.md) | `val function: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [interval](interval.md) | `val interval: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |

### Functions

| Name | Summary |
|---|---|
| [cancel](cancel.md) | `fun cancel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [start](start.md) | `fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

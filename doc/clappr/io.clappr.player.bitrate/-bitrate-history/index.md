[clappr](../../index.md) / [io.clappr.player.bitrate](../index.md) / [BitrateHistory](./index.md)

# BitrateHistory

`class BitrateHistory`

### Types

| Name | Summary |
|---|---|
| [BitrateLog](-bitrate-log/index.md) | `data class BitrateLog` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BitrateHistory(clockInNano: () -> `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [clockInNano](clock-in-nano.md) | `val clockInNano: () -> `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |

### Functions

| Name | Summary |
|---|---|
| [addBitrate](add-bitrate.md) | `fun addBitrate(bitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?, currentTimestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = clockInNano()): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [averageBitrate](average-bitrate.md) | `fun averageBitrate(currentTimestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = clockInNano()): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [clear](clear.md) | `fun clear(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

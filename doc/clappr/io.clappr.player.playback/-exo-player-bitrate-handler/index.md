[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [ExoPlayerBitrateHandler](./index.md)

# ExoPlayerBitrateHandler

`class ExoPlayerBitrateHandler`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoPlayerBitrateHandler(bitrateHistory: `[`BitrateHistory`](../../io.clappr.player.bitrate/-bitrate-history/index.md)` = BitrateHistory { System.nanoTime() }, didUpdateBitrate: (bitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [analyticsListener](analytics-listener.md) | `val analyticsListener: AnalyticsListener` |
| [averageBitrate](average-bitrate.md) | `val averageBitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [currentBitrate](current-bitrate.md) | `var currentBitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |

### Functions

| Name | Summary |
|---|---|
| [reset](reset.md) | `fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

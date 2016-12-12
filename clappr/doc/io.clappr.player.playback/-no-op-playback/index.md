[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [NoOpPlayback](.)

# NoOpPlayback

`open class NoOpPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/playback/NoOpPlayback.kt#L7)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `NoOpPlayback(source: String, mimeType: String? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Inherited Properties

| Name | Summary |
|---|---|
| [canPause](../../io.clappr.player.components/-playback/can-pause.md) | `open val canPause: Boolean` |
| [canPlay](../../io.clappr.player.components/-playback/can-play.md) | `open val canPlay: Boolean` |
| [canSeek](../../io.clappr.player.components/-playback/can-seek.md) | `open val canSeek: Boolean` |
| [duration](../../io.clappr.player.components/-playback/duration.md) | `open val duration: Double` |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: String?` |
| [options](../../io.clappr.player.components/-playback/options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [position](../../io.clappr.player.components/-playback/position.md) | `open val position: Double` |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: String` |
| [state](../../io.clappr.player.components/-playback/state.md) | `open val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |

### Inherited Functions

| Name | Summary |
|---|---|
| [load](../../io.clappr.player.components/-playback/load.md) | `open fun load(source: String, mimeType: String? = null): Boolean` |
| [pause](../../io.clappr.player.components/-playback/pause.md) | `open fun pause(): Boolean` |
| [play](../../io.clappr.player.components/-playback/play.md) | `open fun play(): Boolean` |
| [render](../../io.clappr.player.components/-playback/render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [seek](../../io.clappr.player.components/-playback/seek.md) | `open fun seek(seconds: Int): Boolean` |
| [stop](../../io.clappr.player.components/-playback/stop.md) | `open fun stop(): Boolean` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: String` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: String, mimeType: String?): Boolean` |

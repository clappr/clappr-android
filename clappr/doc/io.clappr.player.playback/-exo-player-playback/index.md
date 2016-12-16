[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [ExoPlayerPlayback](.)

# ExoPlayerPlayback

`open class ExoPlayerPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/playback/ExoPlayerPlayBack.kt#L32)

### Types

| Name | Summary |
|---|---|
| [ExoplayerEventsListener](-exoplayer-events-listener/index.md) | `inner class ExoplayerEventsListener : AdaptiveMediaSourceEventListener, EventListener, EventListener` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoPlayerPlayback(source: String, mimeType: String? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Properties

| Name | Summary |
|---|---|
| [canPause](can-pause.md) | `open val canPause: Boolean` |
| [canPlay](can-play.md) | `open val canPlay: Boolean` |
| [canSeek](can-seek.md) | `open val canSeek: Boolean` |
| [duration](duration.md) | `open val duration: Double` |
| [position](position.md) | `open val position: Double` |
| [state](state.md) | `open val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |
| [viewClass](view-class.md) | `open val viewClass: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: String?` |
| [options](../../io.clappr.player.components/-playback/options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: String` |

### Functions

| Name | Summary |
|---|---|
| [load](load.md) | `open fun load(source: String, mimeType: String?): Boolean` |
| [pause](pause.md) | `open fun pause(): Boolean` |
| [play](play.md) | `open fun play(): Boolean` |
| [seek](seek.md) | `open fun seek(seconds: Int): Boolean` |
| [stop](stop.md) | `open fun stop(): Boolean` |

### Inherited Functions

| Name | Summary |
|---|---|
| [render](../../io.clappr.player.components/-playback/render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: String` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: String, mimeType: String?): Boolean` |

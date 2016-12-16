[clappr](../../index.md) / [io.clappr.player.components](../index.md) / [Playback](.)

# Playback

`abstract class Playback : `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md)`, `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/components/Playback.kt#L12)

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `enum class State : Enum<`[`State`](-state/index.md)`>` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Playback(source: String, mimeType: String? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Properties

| Name | Summary |
|---|---|
| [canPause](can-pause.md) | `open val canPause: Boolean` |
| [canPlay](can-play.md) | `open val canPlay: Boolean` |
| [canSeek](can-seek.md) | `open val canSeek: Boolean` |
| [duration](duration.md) | `open val duration: Double` |
| [mimeType](mime-type.md) | `var mimeType: String?` |
| [options](options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [position](position.md) | `open val position: Double` |
| [source](source.md) | `var source: String` |
| [state](state.md) | `open val state: `[`State`](-state/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [name](../../io.clappr.player.base/-named-type/name.md) | `open val name: String?` |
| [view](../../io.clappr.player.base/-u-i-object/view.md) | `var view: View?` |
| [viewClass](../../io.clappr.player.base/-u-i-object/view-class.md) | `open val viewClass: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<*>` |

### Functions

| Name | Summary |
|---|---|
| [load](load.md) | `open fun load(source: String, mimeType: String? = null): Boolean` |
| [pause](pause.md) | `open fun pause(): Boolean` |
| [play](play.md) | `open fun play(): Boolean` |
| [render](render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [seek](seek.md) | `open fun seek(seconds: Int): Boolean` |
| [stop](stop.md) | `open fun stop(): Boolean` |

### Inherited Functions

| Name | Summary |
|---|---|
| [ensureView](../../io.clappr.player.base/-u-i-object/ensure-view.md) | `fun ensureView(): Unit` |
| [remove](../../io.clappr.player.base/-u-i-object/remove.md) | `fun remove(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: String` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: String, mimeType: String?): Boolean` |

### Inheritors

| Name | Summary |
|---|---|
| [ExoPlayerPlayback](../../io.clappr.player.playback/-exo-player-playback/index.md) | `open class ExoPlayerPlayback : Playback` |
| [MediaPlayerPlayback](../../io.clappr.player.playback/-media-player-playback/index.md) | `class MediaPlayerPlayback : Playback` |
| [NoOpPlayback](../../io.clappr.player.playback/-no-op-playback/index.md) | `open class NoOpPlayback : Playback` |

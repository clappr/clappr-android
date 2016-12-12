[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [MediaPlayerPlayback](.)

# MediaPlayerPlayback

`class MediaPlayerPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/playback/MediaPlayerPlayback.kt#L17)

### Types

| Name | Summary |
|---|---|
| [InternalState](-internal-state/index.md) | `enum class InternalState : Enum<`[`InternalState`](-internal-state/index.md)`>` |
| [MediaType](-media-type/index.md) | `enum class MediaType : Enum<`[`MediaType`](-media-type/index.md)`>` |
| [PlaybackView](-playback-view/index.md) | `class PlaybackView : SurfaceView` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MediaPlayerPlayback(source: String, mimeType: String? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Properties

| Name | Summary |
|---|---|
| [canPause](can-pause.md) | `val canPause: Boolean` |
| [canPlay](can-play.md) | `val canPlay: Boolean` |
| [canSeek](can-seek.md) | `val canSeek: Boolean` |
| [canStop](can-stop.md) | `val canStop: Boolean` |
| [duration](duration.md) | `val duration: Double` |
| [position](position.md) | `val position: Double` |
| [state](state.md) | `val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |
| [viewClass](view-class.md) | `val viewClass: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: String?` |
| [options](../../io.clappr.player.components/-playback/options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: String` |

### Functions

| Name | Summary |
|---|---|
| [pause](pause.md) | `fun pause(): Boolean` |
| [play](play.md) | `fun play(): Boolean` |
| [render](render.md) | `fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [seek](seek.md) | `fun seek(seconds: Int): Boolean` |
| [stop](stop.md) | `fun stop(): Boolean` |

### Inherited Functions

| Name | Summary |
|---|---|
| [load](../../io.clappr.player.components/-playback/load.md) | `open fun load(source: String, mimeType: String? = null): Boolean` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [TAG](-t-a-g.md) | `val TAG: String` |
| [name](name.md) | `val name: String?` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: String, mimeType: String?): Boolean` |

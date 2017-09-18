[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [MediaPlayerPlayback](.)

# MediaPlayerPlayback

`class MediaPlayerPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md)

### Types

| Name | Summary |
|---|---|
| [InternalState](-internal-state/index.md) | `enum class InternalState` |
| [PlaybackView](-playback-view/index.md) | `class PlaybackView : `[`SurfaceView`](https://developer.android.com/reference/android/view/SurfaceView.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MediaPlayerPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Properties

| Name | Summary |
|---|---|
| [canPause](can-pause.md) | `val canPause: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canPlay](can-play.md) | `val canPlay: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canSeek](can-seek.md) | `val canSeek: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canStop](can-stop.md) | `val canStop: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [duration](duration.md) | `val duration: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [mediaType](media-type.md) | `val mediaType: `[`MediaType`](../../io.clappr.player.components/-playback/-media-type/index.md) |
| [position](position.md) | `val position: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [state](state.md) | `val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |
| [viewClass](view-class.md) | `val viewClass: `[`Class`](https://developer.android.com/reference/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `open val hasMediaOptionAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [options](../../io.clappr.player.components/-playback/options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [pause](pause.md) | `fun pause(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [play](play.md) | `fun play(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [render](render.md) | `fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [seek](seek.md) | `fun seek(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [stop](stop.md) | `fun stop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [addAvailableMediaOption](../../io.clappr.player.components/-playback/add-available-media-option.md) | `fun addAvailableMediaOption(media: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`, index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = mediaOptionList.size): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [availableMediaOptions](../../io.clappr.player.components/-playback/available-media-options.md) | `fun availableMediaOptions(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`>` |
| [convertSelectedMediaOptionsToJson](../../io.clappr.player.components/-playback/convert-selected-media-options-to-json.md) | `fun convertSelectedMediaOptionsToJson(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [destroy](../../io.clappr.player.components/-playback/destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `fun hasMediaOptionAvailable(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [load](../../io.clappr.player.components/-playback/load.md) | `open fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [resetAvailableMediaOptions](../../io.clappr.player.components/-playback/reset-available-media-options.md) | `open fun resetAvailableMediaOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [selectedMediaOption](../../io.clappr.player.components/-playback/selected-media-option.md) | `fun selectedMediaOption(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`?` |
| [setSelectedMediaOption](../../io.clappr.player.components/-playback/set-selected-media-option.md) | `open fun setSelectedMediaOption(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setupInitialMediasFromClapprOptions](../../io.clappr.player.components/-playback/setup-initial-medias-from-clappr-options.md) | `fun setupInitialMediasFromClapprOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [TAG](-t-a-g.md) | `val TAG: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

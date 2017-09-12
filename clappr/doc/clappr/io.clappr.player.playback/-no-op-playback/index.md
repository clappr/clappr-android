[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [NoOpPlayback](.)

# NoOpPlayback

`open class NoOpPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `NoOpPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Inherited Properties

| Name | Summary |
|---|---|
| [canPause](../../io.clappr.player.components/-playback/can-pause.md) | `open val canPause: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canPlay](../../io.clappr.player.components/-playback/can-play.md) | `open val canPlay: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canSeek](../../io.clappr.player.components/-playback/can-seek.md) | `open val canSeek: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [duration](../../io.clappr.player.components/-playback/duration.md) | `open val duration: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `open val hasMediaOptionAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mediaType](../../io.clappr.player.components/-playback/media-type.md) | `open val mediaType: `[`MediaType`](../../io.clappr.player.components/-playback/-media-type/index.md) |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [options](../../io.clappr.player.components/-playback/options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [position](../../io.clappr.player.components/-playback/position.md) | `open val position: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [state](../../io.clappr.player.components/-playback/state.md) | `open val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |

### Inherited Functions

| Name | Summary |
|---|---|
| [addAvailableMediaOption](../../io.clappr.player.components/-playback/add-available-media-option.md) | `fun addAvailableMediaOption(media: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`, index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = mediaOptionList.size): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [availableMediaOptions](../../io.clappr.player.components/-playback/available-media-options.md) | `fun availableMediaOptions(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`>` |
| [convertSelectedMediaOptionsToJson](../../io.clappr.player.components/-playback/convert-selected-media-options-to-json.md) | `fun convertSelectedMediaOptionsToJson(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [destroy](../../io.clappr.player.components/-playback/destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `fun hasMediaOptionAvailable(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [load](../../io.clappr.player.components/-playback/load.md) | `open fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [pause](../../io.clappr.player.components/-playback/pause.md) | `open fun pause(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [play](../../io.clappr.player.components/-playback/play.md) | `open fun play(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [render](../../io.clappr.player.components/-playback/render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [resetAvailableMediaOptions](../../io.clappr.player.components/-playback/reset-available-media-options.md) | `open fun resetAvailableMediaOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [seek](../../io.clappr.player.components/-playback/seek.md) | `open fun seek(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [selectedMediaOption](../../io.clappr.player.components/-playback/selected-media-option.md) | `fun selectedMediaOption(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`?` |
| [setSelectedMediaOption](../../io.clappr.player.components/-playback/set-selected-media-option.md) | `open fun setSelectedMediaOption(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setupInitialMediasFromClapprOptions](../../io.clappr.player.components/-playback/setup-initial-medias-from-clappr-options.md) | `fun setupInitialMediasFromClapprOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [stop](../../io.clappr.player.components/-playback/stop.md) | `open fun stop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [NoOpPlayback](./index.md)

# NoOpPlayback

`class NoOpPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `NoOpPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Inherited Properties

| Name | Summary |
|---|---|
| [avgBitrate](../../io.clappr.player.components/-playback/avg-bitrate.md) | `open val avgBitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [bitrate](../../io.clappr.player.components/-playback/bitrate.md) | `open val bitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [canPause](../../io.clappr.player.components/-playback/can-pause.md) | `open val canPause: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canPlay](../../io.clappr.player.components/-playback/can-play.md) | `open val canPlay: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canSeek](../../io.clappr.player.components/-playback/can-seek.md) | `open val canSeek: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [currentDate](../../io.clappr.player.components/-playback/current-date.md) | `open val currentDate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [currentTime](../../io.clappr.player.components/-playback/current-time.md) | `open val currentTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [duration](../../io.clappr.player.components/-playback/duration.md) | `open val duration: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `open val hasMediaOptionAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isDvrAvailable](../../io.clappr.player.components/-playback/is-dvr-available.md) | `open val isDvrAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isDvrInUse](../../io.clappr.player.components/-playback/is-dvr-in-use.md) | `open val isDvrInUse: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mediaOptionList](../../io.clappr.player.components/-playback/media-option-list.md) | `var mediaOptionList: `[`LinkedList`](https://developer.android.com/reference/java/util/LinkedList.html)`<`[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`>` |
| [mediaType](../../io.clappr.player.components/-playback/media-type.md) | `open val mediaType: `[`MediaType`](../../io.clappr.player.components/-playback/-media-type/index.md) |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [name](../../io.clappr.player.components/-playback/name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [options](../../io.clappr.player.components/-playback/options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [position](../../io.clappr.player.components/-playback/position.md) | `open val position: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [selectedMediaOptionList](../../io.clappr.player.components/-playback/selected-media-option-list.md) | `var selectedMediaOptionList: `[`ArrayList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-array-list/index.html)`<`[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`>` |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [state](../../io.clappr.player.components/-playback/state.md) | `open val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |
| [supportsSource](../../io.clappr.player.components/-playback/supports-source.md) | `val supportsSource: `[`PlaybackSupportCheck`](../../io.clappr.player.components/-playback-support-check.md) |
| [volume](../../io.clappr.player.components/-playback/volume.md) | `open var volume: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`?`<br>Playback volume. Its not the device volume. If the playback has this capability. You can set the volume from 0.0f to 1.0f. Where 0.0f is muted and 1.0f is the playback maximum volume. PS.: If you set a volume less than 0.0f we'll set the volume to 0.0f PS.: If you set a volume greater than 1.0f we'll set the volume to 1.0f |

### Inherited Functions

| Name | Summary |
|---|---|
| [addAvailableMediaOption](../../io.clappr.player.components/-playback/add-available-media-option.md) | `fun addAvailableMediaOption(media: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`, index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = mediaOptionList.size): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [availableMediaOptions](../../io.clappr.player.components/-playback/available-media-options.md) | `fun availableMediaOptions(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`>` |
| [createAudioMediaOptionFromLanguage](../../io.clappr.player.components/-playback/create-audio-media-option-from-language.md) | `fun createAudioMediaOptionFromLanguage(language: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, raw: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md) |
| [createOriginalOption](../../io.clappr.player.components/-playback/create-original-option.md) | `fun createOriginalOption(raw: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md) |
| [createSubtitleMediaOptionFromLanguage](../../io.clappr.player.components/-playback/create-subtitle-media-option-from-language.md) | `fun createSubtitleMediaOptionFromLanguage(language: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, raw: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md) |
| [destroy](../../io.clappr.player.components/-playback/destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `fun hasMediaOptionAvailable(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [load](../../io.clappr.player.components/-playback/load.md) | `open fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [pause](../../io.clappr.player.components/-playback/pause.md) | `open fun pause(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [play](../../io.clappr.player.components/-playback/play.md) | `open fun play(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [render](../../io.clappr.player.components/-playback/render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [seek](../../io.clappr.player.components/-playback/seek.md) | `open fun seek(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [seekToLivePosition](../../io.clappr.player.components/-playback/seek-to-live-position.md) | `open fun seekToLivePosition(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [selectedMediaOption](../../io.clappr.player.components/-playback/selected-media-option.md) | `fun selectedMediaOption(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`?` |
| [setSelectedMediaOption](../../io.clappr.player.components/-playback/set-selected-media-option.md) | `open fun setSelectedMediaOption(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setupInitialMediasFromClapprOptions](../../io.clappr.player.components/-playback/setup-initial-medias-from-clappr-options.md) | `fun setupInitialMediasFromClapprOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [startAt](../../io.clappr.player.components/-playback/start-at.md) | `open fun startAt(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [stop](../../io.clappr.player.components/-playback/stop.md) | `open fun stop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [entry](entry.md) | `val entry: `[`PlaybackEntry`](../../io.clappr.player.components/-playback-entry/index.md) |
| [name](name.md) | `const val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [supportsSource](supports-source.md) | `val supportsSource: `[`PlaybackSupportCheck`](../../io.clappr.player.components/-playback-support-check.md) |

[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [ExoPlayerPlayback](./index.md)

# ExoPlayerPlayback

`open class ExoPlayerPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`PlaybackSupportInterface`](../../io.clappr.player.components/-playback-support-interface/index.md) |
| [ExoplayerDrmEventsListeners](-exoplayer-drm-events-listeners/index.md) | `inner class ExoplayerDrmEventsListeners : EventListener` |
| [ExoplayerEventsListener](-exoplayer-events-listener/index.md) | `inner class ExoplayerEventsListener : EventListener` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoPlayerPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options())` |

### Properties

| Name | Summary |
|---|---|
| [bandwidthMeter](bandwidth-meter.md) | `val bandwidthMeter: DefaultBandwidthMeter` |
| [canPause](can-pause.md) | `open val canPause: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canPlay](can-play.md) | `open val canPlay: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canSeek](can-seek.md) | `open val canSeek: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [currentDate](current-date.md) | `open val currentDate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [currentTime](current-time.md) | `open val currentTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [duration](duration.md) | `open val duration: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [isDvrAvailable](is-dvr-available.md) | `open val isDvrAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isDvrInUse](is-dvr-in-use.md) | `open val isDvrInUse: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mediaType](media-type.md) | `open val mediaType: `[`MediaType`](../../io.clappr.player.components/-playback/-media-type/index.md) |
| [minDvrSize](min-dvr-size.md) | `open val minDvrSize: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [player](player.md) | `var player: SimpleExoPlayer?` |
| [position](position.md) | `open val position: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [state](state.md) | `open val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |
| [trackSelector](track-selector.md) | `var trackSelector: DefaultTrackSelector?` |
| [viewClass](view-class.md) | `open val viewClass: `[`Class`](https://developer.android.com/reference/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `open val hasMediaOptionAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [options](../../io.clappr.player.components/-playback/options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [addListeners](add-listeners.md) | `open fun addListeners(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [configureTrackSelector](configure-track-selector.md) | `open fun configureTrackSelector(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getSubtitleStyle](get-subtitle-style.md) | `open fun getSubtitleStyle(): CaptionStyleCompat` |
| [handleError](handle-error.md) | `fun handleError(error: `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [load](load.md) | `open fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [pause](pause.md) | `open fun pause(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [play](play.md) | `open fun play(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeListeners](remove-listeners.md) | `open fun removeListeners(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [seek](seek.md) | `open fun seek(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [seekToLivePosition](seek-to-live-position.md) | `open fun seekToLivePosition(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [setSelectedMediaOption](set-selected-media-option.md) | `open fun setSelectedMediaOption(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [stop](stop.md) | `open fun stop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [addAvailableMediaOption](../../io.clappr.player.components/-playback/add-available-media-option.md) | `fun addAvailableMediaOption(media: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`, index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = mediaOptionList.size): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [availableMediaOptions](../../io.clappr.player.components/-playback/available-media-options.md) | `fun availableMediaOptions(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`>` |
| [convertSelectedMediaOptionsToJson](../../io.clappr.player.components/-playback/convert-selected-media-options-to-json.md) | `fun convertSelectedMediaOptionsToJson(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [hasMediaOptionAvailable](../../io.clappr.player.components/-playback/has-media-option-available.md) | `fun hasMediaOptionAvailable(mediaOption: `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [render](../../io.clappr.player.components/-playback/render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [resetAvailableMediaOptions](../../io.clappr.player.components/-playback/reset-available-media-options.md) | `open fun resetAvailableMediaOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [selectedMediaOption](../../io.clappr.player.components/-playback/selected-media-option.md) | `fun selectedMediaOption(type: `[`MediaOptionType`](../../io.clappr.player.components/-media-option-type/index.md)`): `[`MediaOption`](../../io.clappr.player.components/-media-option/index.md)`?` |
| [setupInitialMediasFromClapprOptions](../../io.clappr.player.components/-playback/setup-initial-medias-from-clappr-options.md) | `fun setupInitialMediasFromClapprOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [tag](tag.md) | `val tag: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [supportsSource](supports-source.md) | `fun supportsSource(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

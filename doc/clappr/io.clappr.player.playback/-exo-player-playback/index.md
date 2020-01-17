[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [ExoPlayerPlayback](./index.md)

# ExoPlayerPlayback

`open class ExoPlayerPlayback : `[`Playback`](../../io.clappr.player.components/-playback/index.md)

### Types

| Name | Summary |
|---|---|
| [ExoPlayerDrmEventsListeners](-exo-player-drm-events-listeners/index.md) | `inner class ExoPlayerDrmEventsListeners : DefaultDrmSessionEventListener` |
| [ExoPlayerEventsListener](-exo-player-events-listener/index.md) | `inner class ExoPlayerEventsListener : EventListener` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoPlayerPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)` = Options(), createDefaultTrackSelector: () -> DefaultTrackSelector = {
        DefaultTrackSelector(AdaptiveTrackSelection.Factory())
    })` |

### Properties

| Name | Summary |
|---|---|
| [avgBitrate](avg-bitrate.md) | `open val avgBitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [bandwidthMeter](bandwidth-meter.md) | `val bandwidthMeter: DefaultBandwidthMeter` |
| [bitrate](bitrate.md) | `open val bitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [canPause](can-pause.md) | `open val canPause: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canPlay](can-play.md) | `open val canPlay: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [canSeek](can-seek.md) | `open val canSeek: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [createDefaultTrackSelector](create-default-track-selector.md) | `val createDefaultTrackSelector: () -> DefaultTrackSelector` |
| [currentDate](current-date.md) | `open val currentDate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [currentTime](current-time.md) | `open val currentTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [duration](duration.md) | `open val duration: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [eventsListener](events-listener.md) | `val eventsListener: `[`ExoPlayerEventsListener`](-exo-player-events-listener/index.md) |
| [handleAudioFocus](handle-audio-focus.md) | `open val handleAudioFocus: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isDvrAvailable](is-dvr-available.md) | `open val isDvrAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isDvrInUse](is-dvr-in-use.md) | `open val isDvrInUse: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mediaType](media-type.md) | `open val mediaType: `[`MediaType`](../../io.clappr.player.components/-playback/-media-type/index.md) |
| [minDvrSize](min-dvr-size.md) | `open val minDvrSize: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [player](player.md) | `var player: SimpleExoPlayer?` |
| [position](position.md) | `open val position: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [selectedAudio](selected-audio.md) | `open var selectedAudio: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [selectedSubtitle](selected-subtitle.md) | `open var selectedSubtitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [state](state.md) | `open val state: `[`State`](../../io.clappr.player.components/-playback/-state/index.md) |
| [trackSelector](track-selector.md) | `var trackSelector: DefaultTrackSelector?` |
| [viewClass](view-class.md) | `open val viewClass: `[`Class`](https://developer.android.com/reference/java/lang/Class.html)`<*>` |
| [volume](volume.md) | `open var volume: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`?`<br>Playback volume. Its not the device volume. If the playback has this capability. You can set the volume from 0.0f to 1.0f. Where 0.0f is muted and 1.0f is the playback maximum volume. PS.: If you set a volume less than 0.0f we'll set the volume to 0.0f PS.: If you set a volume greater than 1.0f we'll set the volume to 1.0f |

### Inherited Properties

| Name | Summary |
|---|---|
| [availableAudios](../../io.clappr.player.components/-playback/available-audios.md) | `val availableAudios: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [availableSubtitles](../../io.clappr.player.components/-playback/available-subtitles.md) | `val availableSubtitles: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [internalSelectedAudio](../../io.clappr.player.components/-playback/internal-selected-audio.md) | `var internalSelectedAudio: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [internalSelectedSubtitle](../../io.clappr.player.components/-playback/internal-selected-subtitle.md) | `var internalSelectedSubtitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [name](../../io.clappr.player.components/-playback/name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [options](../../io.clappr.player.components/-playback/options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [supportsSource](../../io.clappr.player.components/-playback/supports-source.md) | `val supportsSource: `[`PlaybackSupportCheck`](../../io.clappr.player.components/-playback-support-check.md) |

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
| [render](render.md) | `open fun render(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [seek](seek.md) | `open fun seek(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [seekToLivePosition](seek-to-live-position.md) | `open fun seekToLivePosition(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [startAt](start-at.md) | `open fun startAt(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [stop](stop.md) | `open fun stop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [setupInitialMediasFromClapprOptions](../../io.clappr.player.components/-playback/setup-initial-medias-from-clappr-options.md) | `fun setupInitialMediasFromClapprOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [entry](entry.md) | `val entry: `[`PlaybackEntry`](../../io.clappr.player.components/-playback-entry/index.md) |
| [name](name.md) | `const val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [supportsSource](supports-source.md) | `val supportsSource: `[`PlaybackSupportCheck`](../../io.clappr.player.components/-playback-support-check.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [buildMediaOptionsJson](../../io.clappr.player.components/build-media-options-json.md) | `fun `[`Playback`](../../io.clappr.player.components/-playback/index.md)`.~~buildMediaOptionsJson~~(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [MediaPlayerPlayback](./index.md)

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
| [availableAudios](../../io.clappr.player.components/-playback/available-audios.md) | `val availableAudios: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [availableSubtitles](../../io.clappr.player.components/-playback/available-subtitles.md) | `val availableSubtitles: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [avgBitrate](../../io.clappr.player.components/-playback/avg-bitrate.md) | `open val avgBitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [bitrate](../../io.clappr.player.components/-playback/bitrate.md) | `open val bitrate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [currentDate](../../io.clappr.player.components/-playback/current-date.md) | `open val currentDate: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [currentTime](../../io.clappr.player.components/-playback/current-time.md) | `open val currentTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [internalSelectedAudio](../../io.clappr.player.components/-playback/internal-selected-audio.md) | `var internalSelectedAudio: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [internalSelectedSubtitle](../../io.clappr.player.components/-playback/internal-selected-subtitle.md) | `var internalSelectedSubtitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [isDvrAvailable](../../io.clappr.player.components/-playback/is-dvr-available.md) | `open val isDvrAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isDvrInUse](../../io.clappr.player.components/-playback/is-dvr-in-use.md) | `open val isDvrInUse: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mimeType](../../io.clappr.player.components/-playback/mime-type.md) | `var mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [name](../../io.clappr.player.components/-playback/name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [options](../../io.clappr.player.components/-playback/options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [selectedAudio](../../io.clappr.player.components/-playback/selected-audio.md) | `open var selectedAudio: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [selectedSubtitle](../../io.clappr.player.components/-playback/selected-subtitle.md) | `open var selectedSubtitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [source](../../io.clappr.player.components/-playback/source.md) | `var source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [supportsSource](../../io.clappr.player.components/-playback/supports-source.md) | `val supportsSource: `[`PlaybackSupportCheck`](../../io.clappr.player.components/-playback-support-check.md) |
| [volume](../../io.clappr.player.components/-playback/volume.md) | `open var volume: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`?`<br>Playback volume. Its not the device volume. If the playback has this capability. You can set the volume from 0.0f to 1.0f. Where 0.0f is muted and 1.0f is the playback maximum volume. PS.: If you set a volume less than 0.0f we'll set the volume to 0.0f PS.: If you set a volume greater than 1.0f we'll set the volume to 1.0f |

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
| [destroy](../../io.clappr.player.components/-playback/destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [load](../../io.clappr.player.components/-playback/load.md) | `open fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [seekToLivePosition](../../io.clappr.player.components/-playback/seek-to-live-position.md) | `open fun seekToLivePosition(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [setupInitialMediasFromClapprOptions](../../io.clappr.player.components/-playback/setup-initial-medias-from-clappr-options.md) | `fun setupInitialMediasFromClapprOptions(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [startAt](../../io.clappr.player.components/-playback/start-at.md) | `open fun startAt(seconds: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

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

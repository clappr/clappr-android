[clappr](../../../index.md) / [io.clappr.player.playback](../../index.md) / [ExoPlayerPlayback](../index.md) / [ExoplayerEventsListener](.)

# ExoplayerEventsListener

`inner class ExoplayerEventsListener : EventListener, EventListener`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoplayerEventsListener()` |

### Functions

| Name | Summary |
|---|---|
| [onLoadError](on-load-error.md) | `fun onLoadError(error: `[`IOException`](https://developer.android.com/reference/java/io/IOException.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onLoadingChanged](on-loading-changed.md) | `fun onLoadingChanged(isLoading: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPlaybackParametersChanged](on-playback-parameters-changed.md) | `fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPlayerError](on-player-error.md) | `fun onPlayerError(error: ExoPlaybackException?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPlayerStateChanged](on-player-state-changed.md) | `fun onPlayerStateChanged(playWhenReady: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, playbackState: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPositionDiscontinuity](on-position-discontinuity.md) | `fun onPositionDiscontinuity(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTimelineChanged](on-timeline-changed.md) | `fun onTimelineChanged(timeline: Timeline?, manifest: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTracksChanged](on-tracks-changed.md) | `fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

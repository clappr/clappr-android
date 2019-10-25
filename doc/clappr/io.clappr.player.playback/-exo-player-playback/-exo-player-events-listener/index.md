[clappr](../../../index.md) / [io.clappr.player.playback](../../index.md) / [ExoPlayerPlayback](../index.md) / [ExoPlayerEventsListener](./index.md)

# ExoPlayerEventsListener

`inner class ExoPlayerEventsListener : EventListener`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoPlayerEventsListener()` |

### Functions

| Name | Summary |
|---|---|
| [onLoadingChanged](on-loading-changed.md) | `fun onLoadingChanged(isLoading: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPlaybackParametersChanged](on-playback-parameters-changed.md) | `fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPlayerError](on-player-error.md) | `fun onPlayerError(error: ExoPlaybackException?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPlayerStateChanged](on-player-state-changed.md) | `fun onPlayerStateChanged(playWhenReady: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, playbackState: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPositionDiscontinuity](on-position-discontinuity.md) | `fun onPositionDiscontinuity(reason: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRepeatModeChanged](on-repeat-mode-changed.md) | `fun onRepeatModeChanged(repeatMode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSeekProcessed](on-seek-processed.md) | `fun onSeekProcessed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onShuffleModeEnabledChanged](on-shuffle-mode-enabled-changed.md) | `fun onShuffleModeEnabledChanged(shuffleModeEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTimelineChanged](on-timeline-changed.md) | `fun onTimelineChanged(timeline: Timeline?, manifest: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, reason: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTracksChanged](on-tracks-changed.md) | `fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

[clappr](../../../index.md) / [io.clappr.player.playback](../../index.md) / [ExoPlayerPlayback](../index.md) / [ExoplayerEventsListener](.)

# ExoplayerEventsListener

`inner class ExoplayerEventsListener : AdaptiveMediaSourceEventListener, EventListener, EventListener` [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/playback/ExoPlayerPlayBack.kt#L242)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExoplayerEventsListener()` |

### Functions

| Name | Summary |
|---|---|
| [onDownstreamFormatChanged](on-downstream-format-changed.md) | `fun onDownstreamFormatChanged(trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaTimeMs: Long): Unit` |
| [onLoadCanceled](on-load-canceled.md) | `fun onLoadCanceled(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long): Unit` |
| [onLoadCompleted](on-load-completed.md) | `fun onLoadCompleted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long): Unit` |
| [onLoadError](on-load-error.md) | `fun onLoadError(error: `[`IOException`](http://docs.oracle.com/javase/6/docs/api/java/io/IOException.html)`?): Unit`<br>`fun onLoadError(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long, error: `[`IOException`](http://docs.oracle.com/javase/6/docs/api/java/io/IOException.html)`?, wasCanceled: Boolean): Unit` |
| [onLoadStarted](on-load-started.md) | `fun onLoadStarted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long): Unit` |
| [onLoadingChanged](on-loading-changed.md) | `fun onLoadingChanged(isLoading: Boolean): Unit` |
| [onPlayerError](on-player-error.md) | `fun onPlayerError(error: ExoPlaybackException?): Unit` |
| [onPlayerStateChanged](on-player-state-changed.md) | `fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int): Unit` |
| [onPositionDiscontinuity](on-position-discontinuity.md) | `fun onPositionDiscontinuity(): Unit` |
| [onTimelineChanged](on-timeline-changed.md) | `fun onTimelineChanged(timeline: Timeline?, manifest: Any?): Unit` |
| [onUpstreamDiscarded](on-upstream-discarded.md) | `fun onUpstreamDiscarded(trackType: Int, mediaStartTimeMs: Long, mediaEndTimeMs: Long): Unit` |

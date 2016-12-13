[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [Event](.)

# Event

`enum class Event : Enum<Event>` [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/base/Events.kt#L3)

### Enum Values

| Name | Summary |
|---|---|
| [READY](-r-e-a-d-y.md) | Player is ready for playback |
| [ERROR](-e-r-r-o-r.md) | Player or media error detected |
| [PLAYING](-p-l-a-y-i-n-g.md) | Did change to PLAYING state |
| [DID_COMPLETE](-d-i-d_-c-o-m-p-l-e-t-e.md) | Media playback completed |
| [DID_PAUSE](-d-i-d_-p-a-u-s-e.md) | Did change to PAUSE state |
| [STALLED](-s-t-a-l-l-e-d.md) | Changed to STALLED state |
| [DID_STOP](-d-i-d_-s-t-o-p.md) | Media playback stopped |
| [DID_SEEK](-d-i-d_-s-e-e-k.md) | Seek completed |
| [DID_CHANGE_SOURCE](-d-i-d_-c-h-a-n-g-e_-s-o-u-r-c-e.md) | Media source changed |
| [BUFFER_UPDATE](-b-u-f-f-e-r_-u-p-d-a-t-e.md) | Media buffer percentage updated |
| [POSITION_UPDATE](-p-o-s-i-t-i-o-n_-u-p-d-a-t-e.md) | Media position updated |
| [WILL_PLAY](-w-i-l-l_-p-l-a-y.md) | Will change to PLAYING state |
| [WILL_PAUSE](-w-i-l-l_-p-a-u-s-e.md) | Will change to PAUSE state |
| [WILL_SEEK](-w-i-l-l_-s-e-e-k.md) | Will change media position |
| [WILL_STOP](-w-i-l-l_-s-t-o-p.md) | Will stop media playback |
| [WILL_CHANGE_SOURCE](-w-i-l-l_-c-h-a-n-g-e_-s-o-u-r-c-e.md) | Will change media source |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: String` |

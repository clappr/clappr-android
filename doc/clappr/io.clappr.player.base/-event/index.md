[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [Event](./index.md)

# Event

`enum class Event`

### Enum Values

| Name | Summary |
|---|---|
| [READY](-r-e-a-d-y.md) | Player is ready for playback |
| [ERROR](-e-r-r-o-r.md) | Player or media error detected |
| [PLAYING](-p-l-a-y-i-n-g.md) | Did change to PLAYING state |
| [DID_COMPLETE](-d-i-d_-c-o-m-p-l-e-t-e.md) | Media playback completed |
| [DID_PAUSE](-d-i-d_-p-a-u-s-e.md) | Did change to PAUSE state |
| [STALLING](-s-t-a-l-l-i-n-g.md) | Changed to STALLING state |
| [DID_STOP](-d-i-d_-s-t-o-p.md) | Media playback stopped |
| [DID_SEEK](-d-i-d_-s-e-e-k.md) | Seek completed |
| [DID_LOAD_SOURCE](-d-i-d_-l-o-a-d_-s-o-u-r-c-e.md) | Media source loaded |
| [DID_UPDATE_BUFFER](-d-i-d_-u-p-d-a-t-e_-b-u-f-f-e-r.md) | Media buffer percentage updated |
| [DID_UPDATE_POSITION](-d-i-d_-u-p-d-a-t-e_-p-o-s-i-t-i-o-n.md) | Media position updated |
| [WILL_PLAY](-w-i-l-l_-p-l-a-y.md) | Will change to PLAYING state |
| [WILL_PAUSE](-w-i-l-l_-p-a-u-s-e.md) | Will change to PAUSE state |
| [WILL_SEEK](-w-i-l-l_-s-e-e-k.md) | Will change media position |
| [WILL_STOP](-w-i-l-l_-s-t-o-p.md) | Will stop media playback |
| [WILL_LOAD_SOURCE](-w-i-l-l_-l-o-a-d_-s-o-u-r-c-e.md) | Will load media source |
| [REQUEST_FULLSCREEN](-r-e-q-u-e-s-t_-f-u-l-l-s-c-r-e-e-n.md) | Player is requesting to enter fullscreen |
| [EXIT_FULLSCREEN](-e-x-i-t_-f-u-l-l-s-c-r-e-e-n.md) | Player is requesting to exit fullscreen |
| [REQUEST_POSTER_UPDATE](-r-e-q-u-e-s-t_-p-o-s-t-e-r_-u-p-d-a-t-e.md) | Request to update poster |
| [WILL_UPDATE_POSTER](-w-i-l-l_-u-p-d-a-t-e_-p-o-s-t-e-r.md) | Will update poster image |
| [DID_UPDATE_POSTER](-d-i-d_-u-p-d-a-t-e_-p-o-s-t-e-r.md) | Poster image updated |
| [MEDIA_OPTIONS_SELECTED](-m-e-d-i-a_-o-p-t-i-o-n-s_-s-e-l-e-c-t-e-d.md) | Media Options Selected. Triggered when the user select a Media Option. Data provided with the [EventData.MEDIA_OPTIONS_SELECTED_RESPONSE](../-event-data/-m-e-d-i-a_-o-p-t-i-o-n-s_-s-e-l-e-c-t-e-d_-r-e-s-p-o-n-s-e.md) key. |
| [MEDIA_OPTIONS_UPDATE](-m-e-d-i-a_-o-p-t-i-o-n-s_-u-p-d-a-t-e.md) | Media Options Update. Triggered when the Playback load a media option |
| [DID_CHANGE_DVR_STATUS](-d-i-d_-c-h-a-n-g-e_-d-v-r_-s-t-a-t-u-s.md) | There was a change in DVR status |
| [DID_CHANGE_DVR_AVAILABILITY](-d-i-d_-c-h-a-n-g-e_-d-v-r_-a-v-a-i-l-a-b-i-l-i-t-y.md) | There was a change in DVR availability |
| [DID_UPDATE_BITRATE](-d-i-d_-u-p-d-a-t-e_-b-i-t-r-a-t-e.md) | Bitrate was updated |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

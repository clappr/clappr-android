[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [ClapprOption](./index.md)

# ClapprOption

`enum class ClapprOption`

### Enum Values

| Name | Summary |
|---|---|
| [START_AT](-s-t-a-r-t_-a-t.md) | This value can be a number, but will be converted to Integer and may cause a truncated value |
| [POSTER](-p-o-s-t-e-r.md) | Poster URL |
| [DRM_LICENSE_URL](-d-r-m_-l-i-c-e-n-s-e_-u-r-l.md) | Inform the URL license if DRM is necessary |
| [SUBTITLES](-s-u-b-t-i-t-l-e-s.md) | Map from subtitles URL`s with name and URL to each one |
| [DRM_LICENSES](-d-r-m_-l-i-c-e-n-s-e-s.md) | Byte Array of drm licenses |
| [MIN_DVR_SIZE](-m-i-n_-d-v-r_-s-i-z-e.md) | The minimum size in seconds to a video be considered with DVR |
| [MEDIA_CONTROL_PLUGINS](-m-e-d-i-a_-c-o-n-t-r-o-l_-p-l-u-g-i-n-s.md) | The sequence in which the plugins will be displayed in the media control. Names are separated by commas and are case sensitive. |
| [LOOP](-l-o-o-p.md) | If true the video will be played forever (loop mode). If false the video will be stopped when it ends |
| [HANDLE_AUDIO_FOCUS](-h-a-n-d-l-e_-a-u-d-i-o_-f-o-c-u-s.md) | Boolean value indicating if Audio Focus should be handled by Clappr. Default value is false. |
| [SELECTED_MEDIA_OPTIONS](-s-e-l-e-c-t-e-d_-m-e-d-i-a_-o-p-t-i-o-n-s.md) | String List to selected MediaOptions. |
| [DEFAULT_AUDIO](-d-e-f-a-u-l-t_-a-u-d-i-o.md) | String that represents default audio |
| [DEFAULT_SUBTITLE](-d-e-f-a-u-l-t_-s-u-b-t-i-t-l-e.md) | String that represents default subtitle |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

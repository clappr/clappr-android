[clappr](../index.md) / [io.clappr.player.components](./index.md)

## Package io.clappr.player.components

### Types

| Name | Summary |
|---|---|
| [AudioLanguage](-audio-language/index.md) | `enum class AudioLanguage` |
| [Container](-container/index.md) | `class Container : `[`UIObject`](../io.clappr.player.base/-u-i-object/index.md) |
| [Core](-core/index.md) | `class Core : `[`UIObject`](../io.clappr.player.base/-u-i-object/index.md) |
| [MediaOption](-media-option/index.md) | `data class MediaOption` |
| [MediaOptionType](-media-option-type/index.md) | `enum class MediaOptionType` |
| [Playback](-playback/index.md) | `abstract class Playback : `[`UIObject`](../io.clappr.player.base/-u-i-object/index.md)`, `[`NamedType`](../io.clappr.player.base/-named-type/index.md) |
| [PlaybackEntry](-playback-entry/index.md) | `data class PlaybackEntry` |
| [SubtitleLanguage](-subtitle-language/index.md) | `enum class SubtitleLanguage` |

### Type Aliases

| Name | Summary |
|---|---|
| [PlaybackFactory](-playback-factory.md) | `typealias PlaybackFactory = (`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, `[`Options`](../io.clappr.player.base/-options/index.md)`) -> `[`Playback`](-playback/index.md) |
| [PlaybackSupportCheck](-playback-support-check.md) | `typealias PlaybackSupportCheck = (`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Properties

| Name | Summary |
|---|---|
| [SUBTITLE_OFF](-s-u-b-t-i-t-l-e_-o-f-f.md) | `val SUBTITLE_OFF: `[`MediaOption`](-media-option/index.md) |

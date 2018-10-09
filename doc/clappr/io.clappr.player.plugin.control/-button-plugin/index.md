[clappr](../../index.md) / [io.clappr.player.plugin.control](../index.md) / [ButtonPlugin](./index.md)

# ButtonPlugin

`abstract class ButtonPlugin : `[`Plugin`](../-media-control/-plugin/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ButtonPlugin(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [idResourceDrawable](id-resource-drawable.md) | `abstract val idResourceDrawable: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [resourceDrawable](resource-drawable.md) | `abstract val resourceDrawable: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [resourceLayout](resource-layout.md) | `abstract val resourceLayout: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [view](view.md) | `open val view: `[`ImageButton`](https://developer.android.com/reference/android/widget/ImageButton.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [isEnabled](../-media-control/-plugin/is-enabled.md) | `open val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isPlaybackIdle](../-media-control/-plugin/is-playback-idle.md) | `open val isPlaybackIdle: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [panel](../-media-control/-plugin/panel.md) | `open var panel: `[`Panel`](../-media-control/-plugin/-panel/index.md) |
| [position](../-media-control/-plugin/position.md) | `open var position: `[`Position`](../-media-control/-plugin/-position/index.md) |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onClick](on-click.md) | `open fun onClick(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [FullscreenButton](../-fullscreen-button/index.md) | `open class FullscreenButton : `[`ButtonPlugin`](./index.md) |
| [PlayButton](../-play-button/index.md) | `open class PlayButton : `[`ButtonPlugin`](./index.md) |

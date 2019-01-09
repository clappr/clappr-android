[clappr](../../../index.md) / [io.clappr.player.plugin.control](../../index.md) / [MediaControl](../index.md) / [Plugin](./index.md)

# Plugin

`abstract class Plugin : `[`UICorePlugin`](../../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Panel](-panel/index.md) | `enum class Panel` |
| [Position](-position/index.md) | `enum class Position` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Plugin(core: `[`Core`](../../../io.clappr.player.components/-core/index.md)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [isEnabled](is-enabled.md) | `open val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isPlaybackIdle](is-playback-idle.md) | `open val isPlaybackIdle: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [panel](panel.md) | `open var panel: `[`Panel`](-panel/index.md) |
| [position](position.md) | `open var position: `[`Position`](-position/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [base](../../../io.clappr.player.plugin.core/-u-i-core-plugin/base.md) | `open val base: `[`UIObject`](../../../io.clappr.player.base/-u-i-object/index.md) |
| [uiObject](../../../io.clappr.player.plugin.core/-u-i-core-plugin/ui-object.md) | `open val uiObject: `[`UIObject`](../../../io.clappr.player.base/-u-i-object/index.md) |
| [visibility](../../../io.clappr.player.plugin.core/-u-i-core-plugin/visibility.md) | `open var visibility: `[`Visibility`](../../../io.clappr.player.plugin/-u-i-plugin/-visibility/index.md) |

### Inheritors

| Name | Summary |
|---|---|
| [ButtonPlugin](../../-button-plugin/index.md) | `abstract class ButtonPlugin : `[`Plugin`](./index.md) |
| [SeekbarPlugin](../../-seekbar-plugin/index.md) | `open class SeekbarPlugin : `[`Plugin`](./index.md) |
| [TimeIndicatorPlugin](../../-time-indicator-plugin/index.md) | `open class TimeIndicatorPlugin : `[`Plugin`](./index.md) |

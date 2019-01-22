[clappr](../../index.md) / [io.clappr.player.plugin.control](../index.md) / [MediaControl](./index.md)

# MediaControl

`class MediaControl : `[`UICorePlugin`](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) |
| [Plugin](-plugin/index.md) | `abstract class Plugin : `[`UICorePlugin`](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MediaControl(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [isEnabled](is-enabled.md) | `val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [state](state.md) | `var state: `[`State`](../../io.clappr.player.plugin/-plugin/-state/index.md) |
| [view](view.md) | `val view: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [base](../../io.clappr.player.plugin.core/-u-i-core-plugin/base.md) | `open val base: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [uiObject](../../io.clappr.player.plugin.core/-u-i-core-plugin/ui-object.md) | `open val uiObject: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [visibility](../../io.clappr.player.plugin.core/-u-i-core-plugin/visibility.md) | `open var visibility: `[`Visibility`](../../io.clappr.player.plugin/-u-i-plugin/-visibility/index.md) |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hide](hide.md) | `fun hide(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [show](show.md) | `fun show(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [entry](entry.md) | `val entry: `[`Core`](../../io.clappr.player.plugin/-plugin-entry/-core/index.md) |
| [modalPanelViewKey](modal-panel-view-key.md) | `const val modalPanelViewKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

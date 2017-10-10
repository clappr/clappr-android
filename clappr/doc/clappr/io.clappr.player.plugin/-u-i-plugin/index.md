[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [UIPlugin](.)

# UIPlugin

`abstract class UIPlugin : `[`Plugin`](../-plugin/index.md)`, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)

### Types

| Name | Summary |
|---|---|
| [Visibility](-visibility/index.md) | `enum class Visibility` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UIPlugin(component: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`, uiObject: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md)` = UIObject())` |

### Properties

| Name | Summary |
|---|---|
| [view](view.md) | `open val view: `[`View`](https://developer.android.com/reference/android/view/View.html)`?` |
| [visibility](visibility.md) | `open var visibility: `[`Visibility`](-visibility/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [component](../-plugin/component.md) | `val component: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md) |
| [state](../-plugin/state.md) | `open var state: `[`State`](../-plugin/-state/index.md) |

### Functions

| Name | Summary |
|---|---|
| [hide](hide.md) | `open fun hide(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [show](show.md) | `open fun show(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [destroy](../-plugin/destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [UIContainerPlugin](../../io.clappr.player.plugin.container/-u-i-container-plugin/index.md) | `open class UIContainerPlugin : UIPlugin` |
| [UICorePlugin](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md) | `open class UICorePlugin : UIPlugin` |

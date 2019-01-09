[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [UIPlugin](./index.md)

# UIPlugin

`interface UIPlugin : `[`Plugin`](../-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Visibility](-visibility/index.md) | `enum class Visibility` |

### Properties

| Name | Summary |
|---|---|
| [uiObject](ui-object.md) | `abstract val uiObject: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [view](view.md) | `open val view: `[`View`](https://developer.android.com/reference/android/view/View.html)`?` |
| [visibility](visibility.md) | `abstract var visibility: `[`Visibility`](-visibility/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [state](../-plugin/state.md) | `open val state: `[`State`](../-plugin/-state/index.md) |

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
| [UIContainerPlugin](../../io.clappr.player.plugin.container/-u-i-container-plugin/index.md) | `open class UIContainerPlugin : `[`UIPlugin`](./index.md)`, `[`ContainerPlugin`](../../io.clappr.player.plugin.container/-container-plugin/index.md) |
| [UICorePlugin](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md) | `open class UICorePlugin : `[`CorePlugin`](../../io.clappr.player.plugin.core/-core-plugin/index.md)`, `[`UIPlugin`](./index.md) |

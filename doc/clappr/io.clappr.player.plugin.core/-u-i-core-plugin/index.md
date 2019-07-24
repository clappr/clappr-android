[clappr](../../index.md) / [io.clappr.player.plugin.core](../index.md) / [UICorePlugin](./index.md)

# UICorePlugin

`open class UICorePlugin : `[`CorePlugin`](../-core-plugin/index.md)`, `[`UIPlugin`](../../io.clappr.player.plugin/-u-i-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UICorePlugin(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`, base: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md)` = UIObject(), name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Companion.name)` |

### Properties

| Name | Summary |
|---|---|
| [base](base.md) | `open val base: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [uiObject](ui-object.md) | `open val uiObject: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [visibility](visibility.md) | `open var visibility: `[`Visibility`](../../io.clappr.player.plugin/-u-i-plugin/-visibility/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [applicationContext](../-core-plugin/application-context.md) | `val applicationContext: `[`Context`](https://developer.android.com/reference/android/content/Context.html) |
| [core](../-core-plugin/core.md) | `val core: `[`Core`](../../io.clappr.player.components/-core/index.md) |
| [name](../-core-plugin/name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [view](../../io.clappr.player.plugin/-u-i-plugin/view.md) | `open val view: `[`View`](https://developer.android.com/reference/android/view/View.html)`?` |

### Inherited Functions

| Name | Summary |
|---|---|
| [hide](../../io.clappr.player.plugin/-u-i-plugin/hide.md) | `open fun hide(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](../../io.clappr.player.plugin/-u-i-plugin/render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [show](../../io.clappr.player.plugin/-u-i-plugin/show.md) | `open fun show(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [MediaControl](../../io.clappr.player.plugin.control/-media-control/index.md) | `open class MediaControl : `[`UICorePlugin`](./index.md) |
| [Plugin](../../io.clappr.player.plugin.control/-media-control/-plugin/index.md) | `abstract class Plugin : `[`UICorePlugin`](./index.md) |

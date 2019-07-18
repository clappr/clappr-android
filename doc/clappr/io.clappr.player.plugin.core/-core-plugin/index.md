[clappr](../../index.md) / [io.clappr.player.plugin.core](../index.md) / [CorePlugin](./index.md)

# CorePlugin

`open class CorePlugin : `[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CorePlugin(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`, base: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)` = BaseObject(), name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Companion.name)` |

### Properties

| Name | Summary |
|---|---|
| [applicationContext](application-context.md) | `val applicationContext: `[`Context`](https://developer.android.com/reference/android/content/Context.html) |
| [base](base.md) | `open val base: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md) |
| [core](core.md) | `val core: `[`Core`](../../io.clappr.player.components/-core/index.md) |
| [name](name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [state](../../io.clappr.player.plugin/-plugin/state.md) | `open val state: `[`State`](../../io.clappr.player.plugin/-plugin/-state/index.md) |

### Inherited Functions

| Name | Summary |
|---|---|
| [destroy](../../io.clappr.player.plugin/-plugin/destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [ExternalInputPlugin](../../io.clappr.player.plugin.core.externalinput/-external-input-plugin/index.md) | `class ExternalInputPlugin : `[`CorePlugin`](./index.md)`, `[`ExternalInputDevice`](../../io.clappr.player.plugin.core.externalinput/-external-input-device/index.md) |
| [UICorePlugin](../-u-i-core-plugin/index.md) | `open class UICorePlugin : `[`CorePlugin`](./index.md)`, `[`UIPlugin`](../../io.clappr.player.plugin/-u-i-plugin/index.md) |

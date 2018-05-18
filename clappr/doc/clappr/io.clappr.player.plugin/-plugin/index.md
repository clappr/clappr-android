[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [Plugin](./index.md)

# Plugin

`abstract class Plugin : `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`, `[`NamedType`](../../io.clappr.player.base/-named-type/index.md)

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `enum class State` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Plugin(component: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [component](component.md) | `val component: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md) |
| [state](state.md) | `open var state: `[`State`](-state/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [id](../../io.clappr.player.base/-base-object/id.md) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](../../io.clappr.player.base/-named-type/name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [listenTo](../../io.clappr.player.base/-base-object/listen-to.md) | `open fun listenTo(obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`, eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../../io.clappr.player.base/-callback/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [off](../../io.clappr.player.base/-base-object/off.md) | `open fun off(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [on](../../io.clappr.player.base/-base-object/on.md) | `open fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../../io.clappr.player.base/-callback/index.md)`, obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [once](../../io.clappr.player.base/-base-object/once.md) | `open fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../../io.clappr.player.base/-callback/index.md)`, obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [stopListening](../../io.clappr.player.base/-base-object/stop-listening.md) | `open fun stopListening(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [trigger](../../io.clappr.player.base/-base-object/trigger.md) | `open fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, userData: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [ContainerPlugin](../../io.clappr.player.plugin.container/-container-plugin/index.md) | `open class ContainerPlugin : `[`Plugin`](./index.md) |
| [CorePlugin](../../io.clappr.player.plugin.core/-core-plugin/index.md) | `open class CorePlugin : `[`Plugin`](./index.md) |
| [UIPlugin](../-u-i-plugin/index.md) | `abstract class UIPlugin : `[`Plugin`](./index.md)`, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md) |

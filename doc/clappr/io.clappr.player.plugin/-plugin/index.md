[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [Plugin](./index.md)

# Plugin

`interface Plugin : `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`, `[`NamedType`](../../io.clappr.player.base/-named-type/index.md)

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `enum class State` |

### Properties

| Name | Summary |
|---|---|
| [state](state.md) | `open val state: `[`State`](-state/index.md) |

### Inherited Properties

| Name | Summary |
|---|---|
| [id](../../io.clappr.player.base/-event-interface/id.md) | `abstract val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](../../io.clappr.player.base/-named-type/name.md) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [listenTo](../../io.clappr.player.base/-event-interface/listen-to.md) | `abstract fun listenTo(obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`, eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../../io.clappr.player.base/-event-handler.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [off](../../io.clappr.player.base/-event-interface/off.md) | `abstract fun off(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [on](../../io.clappr.player.base/-event-interface/on.md) | `abstract fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../../io.clappr.player.base/-event-handler.md)`, obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>`open fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../../io.clappr.player.base/-event-handler.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [once](../../io.clappr.player.base/-event-interface/once.md) | `abstract fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../../io.clappr.player.base/-event-handler.md)`, obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>`open fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../../io.clappr.player.base/-event-handler.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [stopListening](../../io.clappr.player.base/-event-interface/stop-listening.md) | `abstract fun stopListening(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun stopListening(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [trigger](../../io.clappr.player.base/-event-interface/trigger.md) | `abstract fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, userData: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [ContainerPlugin](../../io.clappr.player.plugin.container/-container-plugin/index.md) | `open class ContainerPlugin : `[`Plugin`](./index.md)`, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md) |
| [CorePlugin](../../io.clappr.player.plugin.core/-core-plugin/index.md) | `open class CorePlugin : `[`Plugin`](./index.md)`, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md) |
| [UIPlugin](../-u-i-plugin/index.md) | `interface UIPlugin : `[`Plugin`](./index.md) |

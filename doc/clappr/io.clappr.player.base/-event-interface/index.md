[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [EventInterface](./index.md)

# EventInterface

`interface EventInterface`

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | `abstract val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [listenTo](listen-to.md) | `abstract fun listenTo(obj: `[`EventInterface`](./index.md)`, eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../-event-handler.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [off](off.md) | `abstract fun off(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [on](on.md) | `abstract fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../-event-handler.md)`, obj: `[`EventInterface`](./index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>`open fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../-event-handler.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [once](once.md) | `abstract fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../-event-handler.md)`, obj: `[`EventInterface`](./index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>`open fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`EventHandler`](../-event-handler.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [stopListening](stop-listening.md) | `abstract fun stopListening(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun stopListening(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [trigger](trigger.md) | `abstract fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, userData: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BaseObject](../-base-object/index.md) | `open class BaseObject : `[`EventInterface`](./index.md) |
| [ContainerPlugin](../../io.clappr.player.plugin.container/-container-plugin/index.md) | `open class ContainerPlugin : `[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`, `[`EventInterface`](./index.md) |
| [CorePlugin](../../io.clappr.player.plugin.core/-core-plugin/index.md) | `open class CorePlugin : `[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`, `[`EventInterface`](./index.md) |
| [Player](../../io.clappr.player/-player/index.md) | `open class Player : Fragment, `[`EventInterface`](./index.md)<br>Main Player class. |
| [Plugin](../../io.clappr.player.plugin/-plugin/index.md) | `interface Plugin : `[`EventInterface`](./index.md)`, `[`NamedType`](../-named-type/index.md) |

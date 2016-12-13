[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [EventInterface](.)

# EventInterface

`interface EventInterface : Any` [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/base/EventInterface.kt#L14)

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | `abstract val id: String` |

### Functions

| Name | Summary |
|---|---|
| [listenTo](listen-to.md) | `abstract fun listenTo(obj: EventInterface, eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [off](off.md) | `abstract fun off(listenId: String): Unit` |
| [on](on.md) | `abstract fun on(eventName: String, handler: `[`Callback`](../-callback/index.md)`, obj: EventInterface): String`<br>`open fun on(eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [once](once.md) | `abstract fun once(eventName: String, handler: `[`Callback`](../-callback/index.md)`, obj: EventInterface): String`<br>`open fun once(eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [stopListening](stop-listening.md) | `abstract fun stopListening(listenId: String?): Unit`<br>`open fun stopListening(): Unit` |
| [trigger](trigger.md) | `abstract fun trigger(eventName: String, userData: Bundle?): Unit`<br>`open fun trigger(eventName: String): Unit` |

### Inheritors

| Name | Summary |
|---|---|
| [BaseObject](../-base-object/index.md) | `open class BaseObject : EventInterface` |
| [Player](../../io.clappr.player/-player/index.md) | `open class Player : Fragment, EventInterface`<br>Main Player class. |
| [UIPlugin](../../io.clappr.player.plugin/-u-i-plugin/index.md) | `abstract class UIPlugin : `[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`, EventInterface` |

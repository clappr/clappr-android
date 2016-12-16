[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [BaseObject](.)

# BaseObject

`open class BaseObject : `[`EventInterface`](../-event-interface/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/base/BaseObject.kt#L10)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BaseObject()` |

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | `open val id: String` |

### Functions

| Name | Summary |
|---|---|
| [listenTo](listen-to.md) | `open fun listenTo(obj: `[`EventInterface`](../-event-interface/index.md)`, eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [off](off.md) | `open fun off(listenId: String): Unit` |
| [on](on.md) | `open fun on(eventName: String, handler: `[`Callback`](../-callback/index.md)`, obj: `[`EventInterface`](../-event-interface/index.md)`): String` |
| [once](once.md) | `open fun once(eventName: String, handler: `[`Callback`](../-callback/index.md)`, obj: `[`EventInterface`](../-event-interface/index.md)`): String` |
| [stopListening](stop-listening.md) | `open fun stopListening(listenId: String?): Unit` |
| [trigger](trigger.md) | `open fun trigger(eventName: String, userData: Bundle?): Unit` |

### Inherited Functions

| Name | Summary |
|---|---|
| [on](../-event-interface/on.md) | `open fun on(eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [once](../-event-interface/once.md) | `open fun once(eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [stopListening](../-event-interface/stop-listening.md) | `open fun stopListening(): Unit` |
| [trigger](../-event-interface/trigger.md) | `open fun trigger(eventName: String): Unit` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [CONTEXT_KEY](-c-o-n-t-e-x-t_-k-e-y.md) | `const val CONTEXT_KEY: String` |
| [USERDATA_KEY](-u-s-e-r-d-a-t-a_-k-e-y.md) | `const val USERDATA_KEY: String` |
| [context](context.md) | `var context: Context?` |

### Inheritors

| Name | Summary |
|---|---|
| [Plugin](../../io.clappr.player.plugin/-plugin/index.md) | `abstract class Plugin : BaseObject, `[`NamedType`](../-named-type/index.md) |
| [UIObject](../-u-i-object/index.md) | `open class UIObject : BaseObject` |

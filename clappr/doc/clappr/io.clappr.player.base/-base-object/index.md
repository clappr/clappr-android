[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [BaseObject](.)

# BaseObject

`open class BaseObject : `[`EventInterface`](../-event-interface/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BaseObject()` |

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [listenTo](listen-to.md) | `open fun listenTo(obj: `[`EventInterface`](../-event-interface/index.md)`, eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../-callback/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [off](off.md) | `open fun off(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [on](on.md) | `open fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../-callback/index.md)`, obj: `[`EventInterface`](../-event-interface/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [once](once.md) | `open fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../-callback/index.md)`, obj: `[`EventInterface`](../-event-interface/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [stopListening](stop-listening.md) | `open fun stopListening(listenId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [trigger](trigger.md) | `open fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, userData: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [on](../-event-interface/on.md) | `open fun on(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../-callback/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [once](../-event-interface/once.md) | `open fun once(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, handler: `[`Callback`](../-callback/index.md)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [stopListening](../-event-interface/stop-listening.md) | `open fun stopListening(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [trigger](../-event-interface/trigger.md) | `open fun trigger(eventName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [CONTEXT_KEY](-c-o-n-t-e-x-t_-k-e-y.md) | `const val CONTEXT_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [USERDATA_KEY](-u-s-e-r-d-a-t-a_-k-e-y.md) | `const val USERDATA_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [context](context.md) | `var context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`?` |

### Inheritors

| Name | Summary |
|---|---|
| [Plugin](../../io.clappr.player.plugin/-plugin/index.md) | `abstract class Plugin : BaseObject, `[`NamedType`](../-named-type/index.md) |
| [UIObject](../-u-i-object/index.md) | `open class UIObject : BaseObject` |

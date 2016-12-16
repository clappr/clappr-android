[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [UIObject](.)

# UIObject

`open class UIObject : `[`BaseObject`](../-base-object/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/base/UIObject.kt#L7)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UIObject()` |

### Properties

| Name | Summary |
|---|---|
| [view](view.md) | `var view: View?` |
| [viewClass](view-class.md) | `open val viewClass: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [id](../-base-object/id.md) | `open val id: String` |

### Functions

| Name | Summary |
|---|---|
| [ensureView](ensure-view.md) | `fun ensureView(): Unit` |
| [remove](remove.md) | `fun remove(): UIObject` |
| [render](render.md) | `open fun render(): UIObject` |

### Inherited Functions

| Name | Summary |
|---|---|
| [listenTo](../-base-object/listen-to.md) | `open fun listenTo(obj: `[`EventInterface`](../-event-interface/index.md)`, eventName: String, handler: `[`Callback`](../-callback/index.md)`): String` |
| [off](../-base-object/off.md) | `open fun off(listenId: String): Unit` |
| [on](../-base-object/on.md) | `open fun on(eventName: String, handler: `[`Callback`](../-callback/index.md)`, obj: `[`EventInterface`](../-event-interface/index.md)`): String` |
| [once](../-base-object/once.md) | `open fun once(eventName: String, handler: `[`Callback`](../-callback/index.md)`, obj: `[`EventInterface`](../-event-interface/index.md)`): String` |
| [stopListening](../-base-object/stop-listening.md) | `open fun stopListening(listenId: String?): Unit` |
| [trigger](../-base-object/trigger.md) | `open fun trigger(eventName: String, userData: Bundle?): Unit` |

### Inheritors

| Name | Summary |
|---|---|
| [Container](../../io.clappr.player.components/-container/index.md) | `class Container : UIObject` |
| [Core](../../io.clappr.player.components/-core/index.md) | `class Core : UIObject` |
| [Playback](../../io.clappr.player.components/-playback/index.md) | `abstract class Playback : UIObject, `[`NamedType`](../-named-type/index.md) |

[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [Plugin](.)

# Plugin

`abstract class Plugin : `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`, `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/plugin/Plugin.kt#L6)

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `enum class State : Enum<`[`State`](-state/index.md)`>` |

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
| [id](../../io.clappr.player.base/-base-object/id.md) | `open val id: String` |
| [name](../../io.clappr.player.base/-named-type/name.md) | `open val name: String?` |

### Inherited Functions

| Name | Summary |
|---|---|
| [listenTo](../../io.clappr.player.base/-base-object/listen-to.md) | `open fun listenTo(obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`, eventName: String, handler: `[`Callback`](../../io.clappr.player.base/-callback/index.md)`): String` |
| [off](../../io.clappr.player.base/-base-object/off.md) | `open fun off(listenId: String): Unit` |
| [on](../../io.clappr.player.base/-base-object/on.md) | `open fun on(eventName: String, handler: `[`Callback`](../../io.clappr.player.base/-callback/index.md)`, obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`): String` |
| [once](../../io.clappr.player.base/-base-object/once.md) | `open fun once(eventName: String, handler: `[`Callback`](../../io.clappr.player.base/-callback/index.md)`, obj: `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)`): String` |
| [stopListening](../../io.clappr.player.base/-base-object/stop-listening.md) | `open fun stopListening(listenId: String?): Unit` |
| [trigger](../../io.clappr.player.base/-base-object/trigger.md) | `open fun trigger(eventName: String, userData: Bundle?): Unit` |

### Inheritors

| Name | Summary |
|---|---|
| [ContainerPlugin](../../io.clappr.player.plugin.container/-container-plugin/index.md) | `open class ContainerPlugin : Plugin` |
| [CorePlugin](../../io.clappr.player.plugin.core/-core-plugin/index.md) | `open class CorePlugin : Plugin` |
| [UIPlugin](../-u-i-plugin/index.md) | `abstract class UIPlugin : Plugin, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md) |

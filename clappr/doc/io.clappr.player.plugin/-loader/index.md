[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [Loader](.)

# Loader

`class Loader : Any` [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/plugin/Loader.kt#L11)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Loader(extraPlugins: List<KClass<out `[`Plugin`](../-plugin/index.md)`>> = emptyList(), extraPlaybacks: List<KClass<out `[`Playback`](../../io.clappr.player.components/-playback/index.md)`>> = emptyList())` |

### Properties

| Name | Summary |
|---|---|
| [availablePlaybacks](available-playbacks.md) | `val availablePlaybacks: <ERROR CLASS>` |
| [availablePlugins](available-plugins.md) | `val availablePlugins: <ERROR CLASS>` |
| [externalPlaybacks](external-playbacks.md) | `val externalPlaybacks: <ERROR CLASS>` |
| [externalPlugins](external-plugins.md) | `val externalPlugins: <ERROR CLASS>` |

### Functions

| Name | Summary |
|---|---|
| [loadPlayback](load-playback.md) | `fun loadPlayback(source: String, mimeType: String? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)`): `[`Playback`](../../io.clappr.player.components/-playback/index.md)`?` |
| [loadPlugins](load-plugins.md) | `fun loadPlugins(context: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`): List<`[`Plugin`](../-plugin/index.md)`>` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [registeredPlaybacks](registered-playbacks.md) | `val registeredPlaybacks: <ERROR CLASS>` |
| [registeredPlugins](registered-plugins.md) | `val registeredPlugins: <ERROR CLASS>` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [clearPlaybacks](clear-playbacks.md) | `fun clearPlaybacks(): Unit` |
| [clearPlugins](clear-plugins.md) | `fun clearPlugins(): Unit` |
| [registerPlayback](register-playback.md) | `fun registerPlayback(playbackClass: KClass<out `[`Playback`](../../io.clappr.player.components/-playback/index.md)`>): Boolean` |
| [registerPlugin](register-plugin.md) | `fun registerPlugin(pluginClass: KClass<out `[`Plugin`](../-plugin/index.md)`>): Boolean` |
| [supportsSource](supports-source.md) | `fun supportsSource(playbackClass: KClass<out `[`Playback`](../../io.clappr.player.components/-playback/index.md)`>, source: String, mimeType: String? = null): Boolean` |

[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [Loader](./index.md)

# Loader

`object Loader`

### Functions

| Name | Summary |
|---|---|
| [clearPlaybacks](clear-playbacks.md) | `fun clearPlaybacks(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [clearPlugins](clear-plugins.md) | `fun clearPlugins(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [loadPlayback](load-playback.md) | `fun loadPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)`): `[`Playback`](../../io.clappr.player.components/-playback/index.md)`?` |
| [loadPlugins](load-plugins.md) | `fun <Context : `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`> loadPlugins(context: `[`Context`](load-plugins.md#Context)`, externalPlugins: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`PluginEntry`](../-plugin-entry/index.md)`> = emptyList()): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Plugin`](../-plugin/index.md)`>` |
| [register](register.md) | `fun register(pluginEntry: `[`PluginEntry`](../-plugin-entry/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>`fun register(playbackEntry: `[`PlaybackEntry`](../../io.clappr.player.components/-playback-entry/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [unregisterPlayback](unregister-playback.md) | `fun unregisterPlayback(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [unregisterPlugin](unregister-plugin.md) | `fun unregisterPlugin(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

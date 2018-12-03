[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [Loader](./index.md)

# Loader

`class Loader`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Loader(extraPlugins: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Plugin`](../-plugin/index.md)`>> = emptyList(), extraPlaybacks: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Playback`](../../io.clappr.player.components/-playback/index.md)`>> = emptyList())` |

### Functions

| Name | Summary |
|---|---|
| [loadPlayback](load-playback.md) | `fun loadPlayback(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, options: `[`Options`](../../io.clappr.player.base/-options/index.md)`): `[`Playback`](../../io.clappr.player.components/-playback/index.md)`?` |
| [loadPlugins](load-plugins.md) | `fun loadPlugins(context: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Plugin`](../-plugin/index.md)`>` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [clearPlaybacks](clear-playbacks.md) | `fun clearPlaybacks(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [clearPlugins](clear-plugins.md) | `fun clearPlugins(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [registerPlayback](register-playback.md) | `fun registerPlayback(playbackClass: `[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Playback`](../../io.clappr.player.components/-playback/index.md)`>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [registerPlugin](register-plugin.md) | `fun registerPlugin(pluginClass: `[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Plugin`](../-plugin/index.md)`>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [supportsSource](supports-source.md) | `fun supportsSource(playbackClass: `[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Playback`](../../io.clappr.player.components/-playback/index.md)`>, source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [unregisterPlugin](unregister-plugin.md) | `fun unregisterPlugin(pluginClass: `[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Plugin`](../-plugin/index.md)`>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

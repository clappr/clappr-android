[clappr](../../index.md) / [io.clappr.player.components](../index.md) / [Container](./index.md)

# Container

`class Container : `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Container(options: `[`Options`](../../io.clappr.player.base/-options/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [options](options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [playback](playback.md) | `var playback: `[`Playback`](../-playback/index.md)`?` |
| [plugins](plugins.md) | `val plugins: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`>` |
| [sharedData](shared-data.md) | `val sharedData: `[`SharedData`](../../io.clappr.player.shared/-shared-data/index.md) |
| [viewClass](view-class.md) | `val viewClass: `[`Class`](https://developer.android.com/reference/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [view](../../io.clappr.player.base/-u-i-object/view.md) | `var view: `[`View`](https://developer.android.com/reference/android/view/View.html)`?` |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [load](load.md) | `fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [render](render.md) | `fun render(): `[`Container`](./index.md) |

### Inherited Functions

| Name | Summary |
|---|---|
| [remove](../../io.clappr.player.base/-u-i-object/remove.md) | `fun remove(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |

[clappr](../../index.md) / [io.clappr.player.components](../index.md) / [Core](./index.md)

# Core

`class Core : `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md)

### Types

| Name | Summary |
|---|---|
| [FullscreenState](-fullscreen-state/index.md) | `enum class FullscreenState` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Core(options: `[`Options`](../../io.clappr.player.base/-options/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [activeContainer](active-container.md) | `var activeContainer: `[`Container`](../-container/index.md)`?` |
| [activePlayback](active-playback.md) | `val activePlayback: `[`Playback`](../-playback/index.md)`?` |
| [containers](containers.md) | `val containers: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Container`](../-container/index.md)`>` |
| [environment](environment.md) | `val environment: `[`Environment`](../../io.clappr.player.utils/-environment/index.md) |
| [fullscreenState](fullscreen-state.md) | `var fullscreenState: `[`FullscreenState`](-fullscreen-state/index.md) |
| [options](options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
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
| [load](load.md) | `fun load(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `fun render(): `[`Core`](./index.md) |

### Inherited Functions

| Name | Summary |
|---|---|
| [remove](../../io.clappr.player.base/-u-i-object/remove.md) | `fun remove(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |

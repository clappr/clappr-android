[clappr](../../index.md) / [io.clappr.player.components](../index.md) / [Core](.)

# Core

`class Core : `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md)

### Types

| Name | Summary |
|---|---|
| [FullscreenState](-fullscreen-state/index.md) | `enum class FullscreenState` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Core(loader: `[`Loader`](../../io.clappr.player.plugin/-loader/index.md)`, options: `[`Options`](../../io.clappr.player.base/-options/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [activeContainer](active-container.md) | `var activeContainer: `[`Container`](../-container/index.md)`?` |
| [activePlayback](active-playback.md) | `val activePlayback: `[`Playback`](../-playback/index.md)`?` |
| [containers](containers.md) | `val containers: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Container`](../-container/index.md)`>` |
| [frameLayout](frame-layout.md) | `val frameLayout: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |
| [fullscreenState](fullscreen-state.md) | `var fullscreenState: `[`FullscreenState`](-fullscreen-state/index.md) |
| [loader](loader.md) | `val loader: `[`Loader`](../../io.clappr.player.plugin/-loader/index.md) |
| [options](options.md) | `var options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [plugins](plugins.md) | `val plugins: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`>` |
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
| [render](render.md) | `fun render(): Core` |

### Inherited Functions

| Name | Summary |
|---|---|
| [ensureView](../../io.clappr.player.base/-u-i-object/ensure-view.md) | `fun ensureView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [remove](../../io.clappr.player.base/-u-i-object/remove.md) | `fun remove(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
